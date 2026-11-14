/* Copyright by Barry G. Becker, 2019-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm

import java.awt.image.BufferedImage
import scala.compiletime.uninitialized
import com.barrybecker4.ui.util.ColorMap
import java.awt.{Color, Graphics2D}
import com.barrybecker4.simulation.complexmapping.algorithm.model.{Box, Grid, MeshPoint}
import javax.vecmath.Point2d
import GridRenderer.{MARGIN, AXIS_COLOR}


object GridRenderer {
  private val AXIS_COLOR = new Color(230, 230, 245)
  private val MARGIN = 0.3
}
case class GridRenderer(grid: Grid, cmap: ColorMap) {

  // these are global to avoid having to pass them in every internal method.
  private var xScale: Double = uninitialized
  private var yScale: Double = uninitialized
  private var xOffset: Double = uninitialized
  private var yOffset: Double = uninitialized

  /**
    * The viewport will be automatically determined from the bounds of the grid.
    * It will then be scaled to the specified pixel width and height.
    * @return the image with grid rendered onto it
    */
  def render(width: Int, height: Int): BufferedImage = {
    render(findViewport(), width, height)
  }

  /**
    * The viewport will be scaled to the specified pixel width and height.
    * @return the image with grid rendered onto it
    */
  def render(viewport: Box, width: Int, height: Int): BufferedImage = {
    val buf = createBufferedImage(width, height)
    val g: Graphics2D = buf.createGraphics()
    try {
      xScale = width / viewport.width
      yScale = -height / viewport.height
      xOffset = viewport.leftX
      yOffset = viewport.topY

      renderGrid(g)
      renderAxes(g, viewport)
    } finally {
      g.dispose()
    }
    buf
  }

  /** Bounds from every finite mesh point (mesh cost is dominated by the transform, not this scan). */
  private[algorithm] def findViewport(): Box = {
    var box = Box(new Point2d(0, 0), new Point2d(0, 0))
    var started = false
    for (i <- 0 until grid.width; j <- 0 until grid.height) {
      val pt = grid(i, j).pt
      if (pt.x.isFinite && pt.y.isFinite) {
        if (!started) {
          box = Box(new Point2d(pt.x, pt.y), new Point2d(pt.x, pt.y))
          started = true
        } else {
          box = box.extendBy(pt)
        }
      }
    }
    box.addMargin(MARGIN)
  }

  private def renderGrid(g: Graphics2D): Unit = {
    for (j <- 0 until grid.height - 1)
      for (i <- 0 until grid.width - 1) {
        val p0 = grid(i, j)
        val p1 = grid(i + 1, j)
        val p2 = grid(i + 1, j + 1)
        val p3 = grid(i, j + 1)
        renderQuadrilateral(p0, p1, p2, p3, g)
      }
  }

  private def renderAxes(g: Graphics2D, viewport: Box): Unit = {
    val x0 = convertX(0)
    val y0 = convertY(0)
    g.setColor(AXIS_COLOR)
    g.drawLine(x0, convertY(viewport.bottomY), x0, convertY(viewport.topY))
    g.drawLine(convertX(viewport.leftX), y0, convertX(viewport.rightX), y0)
  }

  /** Wireframe quads with spokes to the cell center. */
  private def renderQuadrilateral(p0: MeshPoint, p1: MeshPoint, p2: MeshPoint, p3: MeshPoint, g: Graphics2D): Unit = {
    val centerValue = (p0.value + p1.value + p2.value + p3.value) / 4.0
    val avgX = (p0.x + p1.x + p2.x + p3.x) / 4.0
    val avgY = (p0.y + p1.y + p2.y + p3.y) / 4.0
    if (avgX.isFinite && avgY.isFinite) {
      val center = MeshPoint(new Point2d(avgX, avgY), centerValue)
      drawLine(p0, p1, g)
      drawLine(p1, p2, g)
      drawLine(p2, p3, g)
      drawLine(p3, p0, g)
      drawLine(p0, center, g)
      drawLine(p1, center, g)
      drawLine(p2, center, g)
      drawLine(p3, center, g)
    }
  }

  private def convertX(x: Double): Int = ((x - xOffset) * xScale).toInt
  private def convertY(y: Double): Int = ((y - yOffset) * yScale).toInt

  private def drawLine(from: MeshPoint, to: MeshPoint, g: Graphics2D): Unit = {
    g.setColor(getInterpolatedColor(from, to))
    g.drawLine(convertX(from.x), convertY(from.y), convertX(to.x), convertY(to.y))
  }

  private def getInterpolatedColor(a: MeshPoint, b: MeshPoint): Color = {
    if (Math.abs(a.value - b.value) > 0.2) {
      // avoid strange values across the colormap wrap threshold
      cmap.getColorForValue(a.value)
    } else {
      cmap.getColorForValue((a.value + b.value) / 2.0)
    }
  }

  /** @return the buffered image to draw into. */
  private def createBufferedImage(width: Int, height: Int): BufferedImage = {
    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  }
}
