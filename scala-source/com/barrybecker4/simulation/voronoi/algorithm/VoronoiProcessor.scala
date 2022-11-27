// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Arc
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.ArcKey
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.ArcQuery
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.BreakPoint
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.SiteEvent
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{Point, VoronoiEdge}
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer

import java.util
import java.util.Map.Entry
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer.MAX_DIM
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer.MIN_DIM

import scala.collection.mutable

/**
  * This code and dependent classes were derived from https://github.com/ajwerner/fortune
  * It is an implementation of Fortune's algorithm - https://en.wikipedia.org/wiki/Fortune%27s_algorithm.
  */
class VoronoiProcessor(val points: IndexedSeq[Point], val renderer: Option[VoronoiRenderer]) {
  private var edgeList: IndexedSeq[VoronoiEdge] = IndexedSeq()
  private var events = new mutable.TreeSet[SiteEvent]
  private val breakPoints = new mutable.HashSet[BreakPoint]()
  private val arcs = new mutable.TreeMap[ArcKey, CircleEvent]
  private var sweepLoc: Double = MAX_DIM

  addEventsForPoints(points)

  while (events.nonEmpty) {
    if (renderer.isDefined)
      renderer.get.draw(points, edgeList, breakPoints, arcs, sweepLoc)
    val cur = events.head
    events = events.tail
    sweepLoc = cur.p.y
    cur.handleEvent(this)
  }
  this.sweepLoc = MIN_DIM // hack to draw negative infinite points

  for (bp <- breakPoints) {
    bp.finish()
  }

  def this(points: IndexedSeq[Point]) = {
    this(points, None)
  }

  def getEdgeList: IndexedSeq[VoronoiEdge] = edgeList
  def getSweepLoc: Double = sweepLoc

  private def addEventsForPoints(points: IndexedSeq[Point]): Unit = {
    for (site <- points) {
      if ((site.x > MAX_DIM || site.x < MIN_DIM) || (site.y > MAX_DIM || site.y < MIN_DIM))
        throw new RuntimeException(String.format("Invalid site in input, sites must be between %f and %f", MIN_DIM, MAX_DIM))
      events.add(SiteEvent(site))
    }
  }

  def handleSiteEvent(point: Point): Unit = {
    // Deal with first point case
    if (arcs.isEmpty) {
      arcs.put(new Arc(point, this), null)
      return
    }
    val arcEntryAbove: (ArcKey, CircleEvent) = findFloorEntry(point)
    val arcAbove: Arc = arcEntryAbove._1.asInstanceOf[Arc]
    // Deal with the degenerate case where the first two points are at the same y value
    if (arcs.isEmpty && arcAbove.site.y == point.y) {
      val newEdge = new VoronoiEdge(arcAbove.site, point)
      newEdge.p1 = new Point((point.x + arcAbove.site.x) / 2.0, Double.PositiveInfinity)
      val newBreak = new BreakPoint(arcAbove.site, point, newEdge, false, this)
      breakPoints.add(newBreak)
      edgeList :+= newEdge
      val arcLeft = new Arc(null, newBreak, this)
      val arcRight = new Arc(newBreak, null, this)
      arcs.remove(arcAbove)
      arcs.put(arcLeft, null)
      arcs.put(arcRight, null)
      return
    }
    // Remove the circle event associated with this arc if there is one
    val falseCE = arcEntryAbove._2
    if (falseCE != null) events.remove(falseCE)

    val breakL = arcAbove.left
    val breakR = arcAbove.right
    val newEdge = new VoronoiEdge(arcAbove.site, point)
    edgeList :+= newEdge
    val newBreakL = new BreakPoint(arcAbove.site, point, newEdge, true, this)
    val newBreakR = new BreakPoint(point, arcAbove.site, newEdge, false, this)
    breakPoints.add(newBreakL)
    breakPoints.add(newBreakR)
    val arcLeft = new Arc(breakL, newBreakL, this)
    val center = new Arc(newBreakL, newBreakR, this)
    val arcRight = new Arc(newBreakR, breakR, this)
    arcs.remove(arcAbove)
    arcs.put(arcLeft, null)
    arcs.put(center, null)
    arcs.put(arcRight, null)

    // Perhaps we can add point param here?
    // then use map<point, edges? later for drawing the cell edges around a site
    checkForCircleEvent(arcLeft)
    checkForCircleEvent(arcRight)
  }

  private def findFloorEntry(point: Point): (ArcKey, CircleEvent) = {
    val arcToSearchFor = new ArcQuery(point)
    if (arcs.contains(arcToSearchFor)) arcs.find(p => p._1.compareTo(arcToSearchFor) == 0).get
    else arcs.maxBefore(arcToSearchFor).get
  }

  def handleCircleEvent(point: Point, arc: Arc, vert: Point): Unit = {
    arcs.remove(arc)
    arc.left.finish(vert)
    arc.right.finish(vert)
    breakPoints.remove(arc.left)
    breakPoints.remove(arc.right)
    var entryRight: (ArcKey, CircleEvent) = arcs.minAfter(arc).get
    var entryLeft: (ArcKey, CircleEvent) = arcs.maxBefore(arc).get
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
    edgeList :+= edge
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
    breakPoints.add(newBP)
    arcRight.left = newBP
    arcLeft.right = newBP
    checkForCircleEvent(arcLeft)
    checkForCircleEvent(arcRight)
  }

  private def removeEvent(entry: (ArcKey, CircleEvent), arc: Arc): Unit = {
    val falseCe = entry._2
    if (falseCe != null) {
      events.remove(falseCe)
      arcs.put(arc, null)
    }
  }

  private def calcEntry(arc: Arc, entry: (ArcKey, CircleEvent), vert: Point): (ArcKey, CircleEvent) = {
    arcs.remove(arc)
    arc.left.finish(vert)
    arc.right.finish(vert)
    breakPoints.remove(arc.left)
    breakPoints.remove(arc.right)
    val falseCe = entry._2
    if (falseCe != null) events.remove(falseCe)
    arcs.minAfter(arc).get
  }

  private def checkForCircleEvent(arc: Arc): Unit = {
    val circleCenter = arc.checkCircle
    if (circleCenter != null) {
      val radius = arc.site.distanceTo(circleCenter)
      val circleEventPoint = new Point(circleCenter.x, circleCenter.y - radius)
      val circleEvent = new CircleEvent(arc, circleEventPoint, circleCenter)
      arcs.put(arc, circleEvent)
      events.add(circleEvent)
    }
  }
}

