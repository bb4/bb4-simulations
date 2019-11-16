/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm

import java.awt.image.BufferedImage
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
  private var xScale: Double = _
  private var yScale: Double = _
  private var xOffset: Double = _
  private var yOffset: Double = _

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

    xScale = width / viewport.width
    yScale = -height / viewport.height
    xOffset = viewport.leftX
    yOffset = viewport.topY

    renderGrid(g)
    renderAxes(g, viewport)
    buf
  }

  private def findViewport(): Box = {
    var box: Box = Box(new Point2d(0, 0), new Point2d(0, 0))
    for (j <- 0 until grid.height - 1) {
       box = box.extendBy(grid(0, j).pt)
       box = box.extendBy(grid(grid.width - 1, j).pt)
    }
    for (i <- 0 until grid.width - 1) {
      box = box.extendBy(grid(i, 0).pt)
      box = box.extendBy(grid(i, grid.height - 1).pt)
    }
    box.addMargin(MARGIN);
  }

  private def renderGrid(g: Graphics2D): Unit = {
    for (j <- 0 until grid.height - 1)
      for (i <- 0 until grid.width - 1) {
        val meshPoints = Array(grid(i, j), grid(i + 1, j), grid(i, j+ 1), grid(i + 1, j + 1))
        renderQuadrilateral(meshPoints, g)
      }
  }

  private def renderAxes(g: Graphics2D, viewport: Box): Unit = {
    val x0 = convertX(0)
    val y0 = convertY(0)
    g.setColor(AXIS_COLOR)
    g.drawLine(x0, convertY(viewport.bottomY), x0, convertY(viewport.topY))
    g.drawLine(convertX(viewport.leftX), y0, convertX(viewport.rightX), y0)
  }

  /** Draw 4 triangles to approximate the quadrilateral region.
    * It we want to get fancy, we can try interpolating the colors using Barycentric coordinates
    * as described here: https://codeplea.com/triangular-interpolation */
  private def renderQuadrilateral(meshPoints: Array[MeshPoint], g: Graphics2D): Unit = {
    val centerValue = meshPoints.map(_.value).sum / 4.0
    val avgX: Double = meshPoints.map(_.x).sum / 4.0
    val avgY: Double = meshPoints.map(_.y).sum / 4.0
    val centerPoint = model.MeshPoint(new Point2d(avgX, avgY), centerValue)

    renderTriangle(Array(meshPoints(0), meshPoints(1), centerPoint), g)
    renderTriangle(Array(meshPoints(1), meshPoints(3), centerPoint), g)
    renderTriangle(Array(meshPoints(3), meshPoints(2), centerPoint), g)
    renderTriangle(Array(meshPoints(2), meshPoints(0), centerPoint), g)
  }

  private def convertX(x: Double): Int = ((x - xOffset) * xScale).toInt
  private def convertY(y: Double): Int = ((y - yOffset) * yScale).toInt

  private def renderTriangle(pts: Array[MeshPoint], g: Graphics2D): Unit = {
    g.setColor(getInterpolatedColor(pts))
    val x = pts.map(pt => convertX(pt.x))
    val y = pts.map(pt => convertY(pt.y))
    g.fillPolygon(x, y, x.length)
  }

  private def getInterpolatedColor(pts: Array[MeshPoint]): Color = {
    if (Math.abs(2 * pts(0).value - pts(1).value - pts(2).value) > 0.2) {
      // avoid strange values across the colormap wrap threshold
      cmap.getColorForValue(pts(0).value)
    } else {
      val value = pts.map(_.value).sum / 3
      cmap.getColorForValue(value)
    }
  }

  /** @return the buffered image to draw into. */
  private def createBufferedImage(width: Int, height: Int): BufferedImage = {
    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  }
}
