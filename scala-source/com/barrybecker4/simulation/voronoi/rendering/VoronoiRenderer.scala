// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.rendering

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.*
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer.*
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.{BasicStroke, Color, Dimension}
import java.awt.image.BufferedImage
import javax.swing.JComponent
import scala.collection.mutable


object VoronoiRenderer {

  val MARGIN: Int = 120
  val INFINITY_MARGIN: Double = 10000.0
  val RADIUS: Double = 1.0

  private val POINT_COLOR = Color.YELLOW
  private val LINE_COLOR = Color.WHITE
  private val BREAK_COLOR = new Color(255, 110, 55)
  private val BACKGROUND_COLOR = Color.BLACK
  private val STROKE: BasicStroke = new BasicStroke(0.5)
  private val BREAK_STROKE: BasicStroke = new BasicStroke(1.0)
  private val PARABOLA_INC = 2
}

class VoronoiRenderer(val width: Int, val height: Int, val panel: JComponent) extends IPointRenderer {
  private val offlineGraphics = new OfflineGraphics(new Dimension(width, height), BACKGROUND_COLOR)

  def show(sites: IndexedSeq[Point], edgeList: IndexedSeq[VoronoiEdge]): Unit = {
    drawPoints(sites)
    setColor(LINE_COLOR)
    setStroke(STROKE)
    for (e <- edgeList) {
      if (e.p1 != null && e.p2 != null) {
        val topY = if (e.p1.y == Double.PositiveInfinity) height + INFINITY_MARGIN
        else e.p1.y // HACK to draw from infinity
        drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
      }
    }
    show()
  }

  def drawPoints(samples: Seq[Point]): Unit = {
    setColor(POINT_COLOR)
    for (point <- samples) {
      fillCircle(point, RADIUS)
    }
  }

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def draw(sites: IndexedSeq[Point], edgeList: IndexedSeq[VoronoiEdge],
           breakPoints: mutable.Set[BreakPoint], arcs: mutable.TreeMap[ArcKey, CircleEvent], sweepLoc: Double): Unit = {
    clear()
    setColor(POINT_COLOR)

    for (p <- sites) {
      fillCircle(p, VoronoiRenderer.RADIUS)
    }
    for (bp <- breakPoints) {
      drawBreakPoint(bp)
    }
    setColor(BREAK_COLOR)
    setStroke(BREAK_STROKE)
    for (a <- arcs.keySet) {
      drawArc(a.asInstanceOf[Arc])
    }
    setColor(LINE_COLOR)
    for (e <- edgeList) {
      if (e.p1 != null && e.p2 != null) {
        val topY = if (e.p1.y == Double.PositiveInfinity) height + INFINITY_MARGIN
        else e.p1.y
        drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
      }
    }
    drawLine(-INFINITY_MARGIN, sweepLoc, height + INFINITY_MARGIN, sweepLoc)
    // the original code passed a delay here to give interaction a chance to process, but I'm not sure that it is needed.
    show() // pass 1?
  }

  def clear(): Unit = {
    offlineGraphics.clear()
  }

  def show(): Unit = {
    if (panel != null)
        panel.repaint()
  }

  private def fillCircle(p: Point, radius: Double): Unit = {
    val x: Int = p.x.toInt
    val y: Int = p.y.toInt
    val rad = radius.toInt
    offlineGraphics.fillCircle(x, y, rad)
  }

  private def drawBreakPoint(bp: BreakPoint): Unit = {
    val p = bp.getPoint
    setColor(BREAK_COLOR)
    fillCircle(p, VoronoiRenderer.RADIUS)
    drawLine(bp.edgeBegin.x, bp.edgeBegin.y, p.x, p.y)
    setColor(LINE_COLOR)
    setStroke(STROKE)
    if (bp.isEdgeLeft && bp.getEdge.p2 != null) drawLine(bp.edgeBegin.x, bp.edgeBegin.y, bp.getEdge.p2.x, bp.getEdge.p2.y)
    else if (!bp.isEdgeLeft && bp.getEdge.p1 != null) drawLine(bp.edgeBegin.x, bp.edgeBegin.y, bp.getEdge.p1.x, bp.getEdge.p1.y)
  }

  private def drawArc(arc: Arc): Unit = {
    val l = arc.getLeft
    val r = arc.getRight
    val par = new Parabola(arc.site, arc.getSweepLoc)
    val min = if (l.x == Double.NegativeInfinity) 0 else l.x
    val max: Double = if (r.x == Double.PositiveInfinity) width else r.x
    drawParabola(par, min, max)
  }

  private def drawParabola(par: Parabola, min: Double, max: Double): Unit = {
    var x = min
    var lastPoint: Point = null
    while (x <= max) {
      val y = ((x - par.a) * (x - par.a) + (par.b * par.b) - (par.c * par.c)) / (2 * (par.b - par.c))
      val point = new Point(x, y)
      if (lastPoint != null) {
        drawLine(x, y, lastPoint.x, lastPoint.y)
      }
      lastPoint = point
      x += PARABOLA_INC
    }
  }

  private def setColor(color: Color): Unit = {
    offlineGraphics.setColor(color)
  }

  private def setStroke(stroke: BasicStroke): Unit = {
    offlineGraphics.setStroke(stroke)
  }

  private def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = {
    val xx1 = x1.toInt
    val yy1 = y1.toInt
    val xx2 = x2.toInt
    val yy2 = y2.toInt
    offlineGraphics.drawLine(xx1, yy1, xx2, yy2)
  }
}
