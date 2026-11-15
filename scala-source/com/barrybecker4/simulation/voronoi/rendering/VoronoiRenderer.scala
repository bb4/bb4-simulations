// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.rendering

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.*
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.{BasicStroke, Color, Dimension}
import java.awt.image.BufferedImage
import javax.swing.JComponent

import VoronoiRenderer.*


object VoronoiRenderer {

  val INFINITY_MARGIN: Double = 10000.0
  val RADIUS: Double = 1.0

  // Visible to companion class below; not part of public API beyond this file.
  private[voronoi] val POINT_COLOR = Color.YELLOW
  private[voronoi] val LINE_COLOR = Color.WHITE
  private[voronoi] val BREAK_COLOR = new Color(255, 110, 55)
  private[voronoi] val BACKGROUND_COLOR = Color.BLACK
  private[voronoi] val STROKE: BasicStroke = new BasicStroke(0.5f)
  private[voronoi] val BREAK_STROKE: BasicStroke = new BasicStroke(1.0f)
  private[voronoi] val PARABOLA_INC = 2
}

/**
  * Offline image buffer for Voronoi animation. All buffer mutations and
  * [[getImage]] are synchronized so the EDT paint path never samples a
  * half-cleared frame (which caused flicker during the Fortune sweep).
  */
class VoronoiRenderer(val width: Int, val height: Int, val panel: JComponent) extends IPointRenderer {
  private val offlineGraphics = new OfflineGraphics(new Dimension(width, height), BACKGROUND_COLOR)

  /** Final diagram: sites + completed edges (no beach-line debug overlay). */
  def show(sites: IndexedSeq[Point], edgeList: IndexedSeq[VoronoiEdge]): Unit = synchronized {
    clearUnlocked()
    drawPointsUnlocked(sites)
    setColor(LINE_COLOR)
    setStroke(STROKE)
    for (e <- edgeList if e.p1 != null && e.p2 != null) {
      val topY = topYForEdgeDraw(e.p1.y)
      drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
    }
  }

  def drawPoints(samples: Seq[Point]): Unit = synchronized {
    drawPointsUnlocked(samples)
  }

  def getImage: BufferedImage = synchronized {
    offlineGraphics.getOfflineImage.get
  }

  /**
    * In-progress Fortune sweep frame: sites, beach line, breakpoints, and
    * finished edge segments. Omits the approximate polygon overlay (not real
    * cells) which produced blotchy fills and amplified flicker.
    */
  def draw(sites: IndexedSeq[Point], geometry: VoronoiGeometry, sweepLoc: Double): Unit = synchronized {
    clearUnlocked()
    drawPointsUnlocked(sites)
    drawBreakPointsLayer(geometry)
    drawArcParabolas(geometry)
    drawCompletedEdges(geometry)
    drawSweepLine(sweepLoc)
  }

  private def drawPointsUnlocked(samples: Seq[Point]): Unit = {
    setColor(POINT_COLOR)
    for (point <- samples) {
      fillCircle(point, RADIUS)
    }
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
    setStroke(STROKE)
    for (e <- geometry.getEdgeList if e.p1 != null && e.p2 != null) {
      val topY = topYForEdgeDraw(e.p1.y)
      drawLine(e.p1.x, topY, e.p2.x, e.p2.y)
    }
  }

  private def topYForEdgeDraw(p1y: Double): Double =
    if (p1y == Double.PositiveInfinity) height + INFINITY_MARGIN
    else p1y

  private def drawSweepLine(sweepLoc: Double): Unit =
    drawLine(-INFINITY_MARGIN, sweepLoc, width + INFINITY_MARGIN, sweepLoc)

  def clear(): Unit = synchronized {
    clearUnlocked()
  }

  private def clearUnlocked(): Unit = {
    offlineGraphics.clear()
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
    var lastPoint: Option[Point] = None
    while (px <= max) {
      val y = ((px - par.a) * (px - par.a) + (par.b * par.b) - (par.c * par.c)) / (2 * (par.b - par.c))
      val pt = new Point(px, y)
      lastPoint.foreach(prev => drawLine(px, y, prev.x, prev.y))
      lastPoint = Some(pt)
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
