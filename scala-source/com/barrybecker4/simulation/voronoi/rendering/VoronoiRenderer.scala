// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.rendering

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.*
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.{BasicStroke, Color, Dimension, Polygon}
import java.awt.image.BufferedImage
import javax.swing.JComponent

import VoronoiRenderer.*


object VoronoiRenderer {

  val MARGIN: Int = 120
  val INFINITY_MARGIN: Double = 10000.0
  val RADIUS: Double = 1.0

  // Visible to companion class below; not part of public API beyond this file.
  private[voronoi] val POINT_COLOR = Color.YELLOW
  private[voronoi] val LINE_COLOR = Color.WHITE
  private[voronoi] val BREAK_COLOR = new Color(255, 110, 55)
  private[voronoi] val POLYGON_FILL_COLOR = new Color(100, 70, 225, 50)
  private[voronoi] val POLYGON_EDGE_COLOR = new Color(140, 90, 255, 210)
  private[voronoi] val BACKGROUND_COLOR = Color.BLACK
  private[voronoi] val STROKE: BasicStroke = new BasicStroke(0.5)
  private[voronoi] val BREAK_STROKE: BasicStroke = new BasicStroke(1.0)
  private[voronoi] val PARABOLA_INC = 2
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
  }

  def drawPoints(samples: Seq[Point]): Unit = {
    setColor(POINT_COLOR)
    for (point <- samples) {
      fillCircle(point, RADIUS)
    }
  }

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def draw(sites: IndexedSeq[Point], geometry: VoronoiGeometry, sweepLoc: Double): Unit = {
    clear()
    drawSiteMarkers(sites)
    drawBreakPointsLayer(geometry)
    drawArcParabolas(geometry)
    drawCompletedEdges(geometry)
    drawSweepLine(sweepLoc)
    for (poly <- geometry.getPolygonsForPoints)
      drawPolygon(poly)
  }

  private def drawSiteMarkers(sites: IndexedSeq[Point]): Unit = {
    setColor(POINT_COLOR)
    for (p <- sites)
      fillCircle(p, RADIUS)
  }

  private def drawBreakPointsLayer(geometry: VoronoiGeometry): Unit = {
    for (bp <- geometry.getBreakPoints)
      drawBreakPoint(bp)
  }

  private def drawArcParabolas(geometry: VoronoiGeometry): Unit = {
    setColor(BREAK_COLOR)
    setStroke(BREAK_STROKE)
    for (a <- geometry.getArcs.keySet)
      drawArc(a.asInstanceOf[Arc])
  }

  private def drawCompletedEdges(geometry: VoronoiGeometry): Unit = {
    setColor(LINE_COLOR)
    for (e <- geometry.getEdgeList if e.p1 != null && e.p2 != null) {
      val topY = topYForEdgeDraw(e.p1.y)
      drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
    }
  }

  private def topYForEdgeDraw(p1y: Double): Double =
    if (p1y == Double.PositiveInfinity) height + INFINITY_MARGIN
    else p1y

  private def drawSweepLine(sweepLoc: Double): Unit =
    drawLine(-INFINITY_MARGIN, sweepLoc, height + INFINITY_MARGIN, sweepLoc)

  def clear(): Unit = {
    offlineGraphics.clear()
  }

  private def fillCircle(p: Point, radius: Double): Unit = {
    val x: Int = p.x.toInt
    val y: Int = p.y.toInt
    val rad = radius.toInt
    offlineGraphics.fillCircle(x, y, rad)
  }

  private def drawPolygon(points: Seq[Point]): Unit = {
    def polygon: Polygon = new Polygon(points.map(_.x.toInt).toArray, points.map(_.y.toInt).toArray, points.length)
    setColor(POLYGON_FILL_COLOR)
    offlineGraphics.fillPolygon(polygon)
    setColor(POLYGON_EDGE_COLOR)
    offlineGraphics.drawPolygon(polygon)
  }

  private def drawBreakPoint(bp: BreakPoint): Unit = {
    val p = bp.getPoint
    setColor(BREAK_COLOR)
    fillCircle(p, RADIUS)
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
    var px = min
    var lastPoint: Point = null
    while (px <= max) {
      val y = ((px - par.a) * (px - par.a) + (par.b * par.b) - (par.c * par.c)) / (2 * (par.b - par.c))
      val pt = new Point(px, y)
      if (lastPoint != null) {
        drawLine(px, y, lastPoint.x, lastPoint.y)
      }
      lastPoint = pt
      px += PARABOLA_INC
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
