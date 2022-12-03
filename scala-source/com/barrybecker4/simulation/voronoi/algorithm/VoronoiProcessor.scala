// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{Arc, ArcKey, ArcQuery, BreakPoint, Point, VoronoiEdge, VoronoiGeometry}
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.SiteEvent
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer

import java.util
import java.util.Map.Entry
import scala.collection.mutable
import VoronoiRenderer.INFINITY_MARGIN

/**
  * This code and dependent classes were derived from https://github.com/ajwerner/fortune
  * It is an implementation of Fortune's algorithm - https://en.wikipedia.org/wiki/Fortune%27s_algorithm.
  */
class VoronoiProcessor(val points: IndexedSeq[Point], val renderer: Option[VoronoiRenderer]) {

  private val geometry: VoronoiGeometry = new VoronoiGeometry()
  private var events = new mutable.TreeSet[SiteEvent]
  private var sweepLoc: Double = INFINITY_MARGIN

  addEventsForPoints(points)

  def this(points: IndexedSeq[Point]) = {
    this(points, None)
  }

  // we can do all processing at once
  def processAll(): Unit = {
    processNext(100000000)
  }

  // or some steps at a time
  def processNext(numSteps: Int): Boolean = {

    var ct = 0
    while (events.nonEmpty && ct < numSteps) {
      if (renderer.isDefined)
        renderer.get.draw(points, geometry, sweepLoc)
      val cur = events.head
      events = events.tail
      sweepLoc = cur.p.y
      cur.handleEvent(this)
      ct += 1
    }

    if (events.isEmpty) {
      this.sweepLoc = -INFINITY_MARGIN // hack to draw negative infinite points

      for (bp <- geometry.getBreakPoints) {
        bp.finish()
      }
      return true
    }
    false
  }


  def getEdgeList: IndexedSeq[VoronoiEdge] = geometry.getEdgeList
  def getSweepLoc: Double = sweepLoc

  private def addEventsForPoints(points: IndexedSeq[Point]): Unit = {
    for (site <- points) {
      if ((site.x < 0) || site.y < 0)
        throw new RuntimeException(String.format("Invalid site in input, sites must be greater than 0"))
      events.add(SiteEvent(site))
    }
  }

  def handleSiteEvent(point: Point): Unit = {
    // Deal with first point case
    if (!geometry.hasArcs) {
      geometry.addArc(new Arc(point, this))
      return
    }
    val arcEntryAbove: (ArcKey, CircleEvent) = geometry.findFloorEntry(point)
    val arcAbove: Arc = arcEntryAbove._1.asInstanceOf[Arc]
    // Deal with the degenerate case where the first two points are at the same y value
    if (!geometry.hasArcs && arcAbove.site.y == point.y) {
      val newEdge = new VoronoiEdge(arcAbove.site, point)
      newEdge.p1 = new Point((point.x + arcAbove.site.x) / 2.0, Double.PositiveInfinity)
      val newBreak = new BreakPoint(arcAbove.site, point, newEdge, false, this)
      geometry.addBreakPoint(newBreak)
      geometry.addEdge(newEdge)
      val arcLeft = new Arc(null, newBreak, this)
      val arcRight = new Arc(newBreak, null, this)
      geometry.removeArc(arcAbove)
      geometry.addArc(arcLeft)
      geometry.addArc(arcRight)
      return
    }
    // Remove the circle event associated with this arc if there is one
    val falseCE = arcEntryAbove._2
    if (falseCE != null) events.remove(falseCE)

    val breakL = arcAbove.left
    val breakR = arcAbove.right
    val newEdge = new VoronoiEdge(arcAbove.site, point)
    geometry.addEdge(newEdge)
    val newBreakL = new BreakPoint(arcAbove.site, point, newEdge, true, this)
    val newBreakR = new BreakPoint(point, arcAbove.site, newEdge, false, this)
    geometry.addBreakPoint(newBreakL)
    geometry.addBreakPoint(newBreakR)
    val arcLeft = new Arc(breakL, newBreakL, this)
    val center = new Arc(newBreakL, newBreakR, this)
    val arcRight = new Arc(newBreakR, breakR, this)
    geometry.removeArc(arcAbove)
    geometry.addArc(arcLeft)
    geometry.addArc(center)
    geometry.addArc(arcRight)

    // Perhaps we can add point param here?
    // then use map<point, edges? later for drawing the cell edges around a site
    checkForCircleEvent(arcLeft)
    checkForCircleEvent(arcRight)
  }

  def handleCircleEvent(point: Point, arc: Arc, vert: Point): Unit = {
    geometry.removeArc(arc)
    arc.left.finish(vert)
    arc.right.finish(vert)
    geometry.removeBreakPoint(arc.left)
    geometry.removeBreakPoint(arc.right)
    var entryRight: (ArcKey, CircleEvent) = geometry.minAfterArc(arc)
    var entryLeft: (ArcKey, CircleEvent) = geometry.maxBeforeArc(arc)
    var arcRight: Arc = null
    var arcLeft: Arc = null
    val ceArcLeft = arc.getLeft
    val cocircularJunction = arc.getRight == ceArcLeft
    if (entryRight != null) {
      arcRight = entryRight._1.asInstanceOf[Arc]
      while (cocircularJunction && arcRight.getRight == ceArcLeft) {
        entryRight = calcEntry(arcRight, entryRight, vert)
        arcRight = entryRight._1.asInstanceOf[Arc]
      }
      removeEvent(entryRight, arcRight)
    }
    if (entryLeft != null) {
      arcLeft = entryLeft._1.asInstanceOf[Arc]
      while (cocircularJunction && arcLeft.getLeft == ceArcLeft) {
        entryLeft = calcEntry(arcLeft, entryLeft, vert)
        arcLeft = entryLeft._1.asInstanceOf[Arc]
      }
      removeEvent(entryLeft, arcLeft)
    }
    val edge = new VoronoiEdge(arcLeft.right.s1, arcRight.left.s2)
    geometry.addEdge(edge)
    // Here we're trying to figure out if the VoronoiProcessor vertex
    // we've found is the left or right point of the new edge.
    // If the edges being traces out by these two arcs take a right turn then we know
    // that the vertex is going to be above the current point
    val turnsLeft = Point.ccw(arcLeft.right.edgeBegin, point, arcRight.left.edgeBegin) == 1
    // So if it turns left, we know the next vertex will be below this vertex
    // so if it's below and the slow is negative then this vertex is the left point
    val isLeftPoint = if (turnsLeft) edge.m < 0
    else edge.m > 0
    if (isLeftPoint) edge.p1 = vert
    else edge.p2 = vert
    val newBP = new BreakPoint(arcLeft.right.s1, arcRight.left.s2, edge, !isLeftPoint, this)
    geometry.addBreakPoint(newBP)
    arcRight.left = newBP
    arcLeft.right = newBP
    checkForCircleEvent(arcLeft)
    checkForCircleEvent(arcRight)
  }

  private def removeEvent(entry: (ArcKey, CircleEvent), arc: Arc): Unit = {
    val falseCe = entry._2
    if (falseCe != null) {
      events.remove(falseCe)
      geometry.addArc(arc)
    }
  }

  private def calcEntry(arc: Arc, entry: (ArcKey, CircleEvent), vert: Point): (ArcKey, CircleEvent) = {
    geometry.removeArc(arc)
    arc.left.finish(vert)
    arc.right.finish(vert)
    geometry.removeBreakPoint(arc.left)
    geometry.removeBreakPoint(arc.right)
    val falseCe = entry._2
    if (falseCe != null) events.remove(falseCe)
    geometry.minAfterArc(arc)
  }

  private def checkForCircleEvent(arc: Arc): Unit = {
    val circleCenter = arc.checkCircle
    if (circleCenter != null) {
      val radius = arc.site.distanceTo(circleCenter)
      val circleEventPoint = new Point(circleCenter.x, circleCenter.y - radius)
      val circleEvent = new CircleEvent(arc, circleEventPoint, circleCenter)
      geometry.addArc(arc, circleEvent)
      events.add(circleEvent)
    }
  }
}

