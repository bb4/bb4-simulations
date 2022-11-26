// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.ui

import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Arc
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.ArcKey
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.BreakPoint
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Parabola
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.VoronoiEdge

import javax.swing.JPanel
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import com.barrybecker4.simulation.voronoi.ui.VoronoiPanel.MARGIN

import scala.collection.mutable



object VoronoiRenderer {
  val MIN_DRAW_DIM: Double = -5
  val MAX_DRAW_DIM: Double = 5
  // Ghetto but just for drawing stuff
  val MAX_DIM: Double = 10
  val MIN_DIM: Double = -10
  val RADIUS: Double = 0.01
}

class VoronoiRenderer(val width: Int, val height: Int, val panel: JPanel) {
  private val offlineGraphics = new OfflineGraphics(new Dimension(width + 2 * MARGIN, height + 2 * MARGIN), Color.WHITE)

  def show(sites: IndexedSeq[Point], edgeList: IndexedSeq[VoronoiEdge]): Unit = {
    setColor(Color.RED)

    for (p <- sites) {
      fillCircle(p, VoronoiRenderer.RADIUS)
    }
    setColor(Color.BLACK)

    for (e <- edgeList) {
      if (e.p1 != null && e.p2 != null) {
        val topY = if (e.p1.y == Double.PositiveInfinity) VoronoiRenderer.MAX_DIM
        else e.p1.y // HACK to draw from infinity
        drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
      }
    }
    show()
  }

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def draw(sites: IndexedSeq[Point], edgeList: IndexedSeq[VoronoiEdge],
           breakPoints: mutable.Set[BreakPoint], arcs: mutable.TreeMap[ArcKey, CircleEvent], sweepLoc: Double): Unit = {
    clear()
    setColor(Color.RED)

    for (p <- sites) {
      fillCircle(p, VoronoiRenderer.RADIUS)
    }
    for (bp <- breakPoints) {
      drawBreakPoint(bp)
    }
    setColor(Color.BLACK)
    for (a <- arcs.keySet) {
      drawArc(a.asInstanceOf[Arc])
    }
    for (e <- edgeList) {
      if (e.p1 != null && e.p2 != null) {
        val topY = if (e.p1.y == Double.PositiveInfinity) VoronoiRenderer.MAX_DIM
        else e.p1.y
        drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
      }
    }
    drawLine(VoronoiRenderer.MIN_DIM, sweepLoc, VoronoiRenderer.MAX_DIM, sweepLoc)
    show(1)
  }

  def clear(): Unit = {
    offlineGraphics.clear()
  }

  def show(): Unit = {
    panel.repaint()
  }

  def show(value: Int): Unit = {
    panel.repaint()
    try Thread.sleep(10)
    catch {
      case e: InterruptedException =>
        throw new RuntimeException(e)
    }
  }

  private def fillCircle(p: Point, radius: Double, color: Color): Unit = {
    offlineGraphics.setColor(color)
    fillCircle(p, radius)
  }

  private def fillCircle(p: Point, radius: Double): Unit = {
    val x: Int = MARGIN + (width * p.x).toInt
    val y: Int = MARGIN + (height * p.y).toInt
    val rad = (0.5 * width * radius).toInt
    offlineGraphics.fillCircle(x, y, rad)
  }

  private def drawPoint(x: Double, y: Double): Unit = {
    val xx = MARGIN + (width * x).toInt
    val yy = MARGIN + (height * y).toInt
    offlineGraphics.drawPoint(xx, yy)
  }

  private def drawBreakPoint(bp: BreakPoint): Unit = {
    val p = bp.getPoint
    setColor(Color.BLUE)
    fillCircle(p, VoronoiRenderer.RADIUS)
    drawLine(bp.edgeBegin.x, bp.edgeBegin.y, p.x, p.y)
    setColor(Color.BLACK)
    if (bp.isEdgeLeft && bp.getEdge.p2 != null) drawLine(bp.edgeBegin.x, bp.edgeBegin.y, bp.getEdge.p2.x, bp.getEdge.p2.y)
    else if (!bp.isEdgeLeft && bp.getEdge.p1 != null) drawLine(bp.edgeBegin.x, bp.edgeBegin.y, bp.getEdge.p1.x, bp.getEdge.p1.y)
  }

  private def drawArc(arc: Arc): Unit = {
    val l = arc.getLeft
    val r = arc.getRight
    val par = new Parabola(arc.site, arc.getSweepLoc)
    val min = if (l.x == Double.NegativeInfinity) VoronoiRenderer.MIN_DRAW_DIM
    else l.x
    val max: Double = if (r.x == Double.PositiveInfinity) VoronoiRenderer.MAX_DRAW_DIM
    else r.x
    drawParabola(par, min, max)
  }

  private def drawParabola(par: Parabola, min: Double, max: Double): Unit = {
    val mini = if (min > -(2)) min
    else -2
    val maxi = if (max < 2) max
    else 2
    var x = mini
    while ( {
      x < maxi
    }) {
      val y = ((x - par.a) * (x - par.a) + (par.b * par.b) - (par.c * par.c)) / (2 * (par.b - par.c))
      drawPoint(x, y)

      x += .001
    }
  }

  private def setColor(color: Color): Unit = {
    offlineGraphics.setColor(color)
  }

  private def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = {
    val xx1 = MARGIN + (width * x1).toInt
    val yy1 = MARGIN + (height * y1).toInt
    val xx2 = MARGIN + (width * x2).toInt
    val yy2 = MARGIN + (height * y2).toInt
    offlineGraphics.drawLine(xx1, yy1, xx2, yy2)
  }
}
