// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{Arc, ArcKey, BreakPoint, Point, VoronoiEdge, VoronoiGeometry}
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.SiteEvent
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer

import scala.collection.mutable
import VoronoiRenderer.INFINITY_MARGIN

/**
  * This code and dependent classes were derived from https://github.com/ajwerner/fortune
  * It is an implementation of Fortune's algorithm - https://en.wikipedia.org/wiki/Fortune%27s_algorithm
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
      val event = events.head
      events = events.tail
      sweepLoc = event.p.y
      event.handleEvent(this)
      ct += 1
    }

    if (events.isEmpty) {
      this.sweepLoc = -INFINITY_MARGIN // hack to draw negative infinite points

      for (bp <- geometry.getBreakPoints)
        bp.finish()
      return true
    }
    false
  }


  def getEdgeList: IndexedSeq[VoronoiEdge] = geometry.getEdgeList
  def getSweepLoc: Double = sweepLoc

  private def addEventsForPoints(points: IndexedSeq[Point]): Unit = {
    for (site <- points) {
      if ((site.x < 0) || site.y < 0)
        throw new IllegalArgumentException("Invalid site in input, sites must be greater than 0")
      events.add(SiteEvent(site))
    }
  }

  def handleSiteEvent(point: Point): Unit = {
    // Deal with first point case
    if (!geometry.hasArcs) {
      geometry.addArc(new Arc(point, this))
      return
    }
    val arcEntryAbove = geometry.findFloorEntry(point).getOrElse(
      throw new IllegalStateException("No beach-line arc for site event")
    )
    val arcAbove = arcEntryAbove._1 match {
      case a: Arc => a
      case other  => throw new IllegalStateException(s"Expected Arc, got ${other.getClass.getSimpleName}")
    }
    // Deal with the degenerate case where the first two points are at the same y value
    if (!geometry.hasArcs && arcAbove.site.y == point.y) {
      updateGeometryWhenTwoPointsAtSameY(arcAbove, point)
      return
    }
    // Remove the circle event associated with this arc if there is one
    val falseCE = arcEntryAbove._2
    if (falseCE != null) events.remove(falseCE)

    val arcs = updateGeometry(arcAbove, point)

    checkForCircleEvent(arcs._1)
    checkForCircleEvent(arcs._2)
  }

  private def updateGeometryWhenTwoPointsAtSameY(arcAbove: Arc, point: Point): Unit = {
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
  }

  private def updateGeometry(arcAbove: Arc, point: Point): (Arc, Arc) = {
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
    (arcLeft, arcRight)
  }

  def handleCircleEvent(point: Point, arc: Arc, vert: Point): Unit = {
    removeArcAndFinishBreakpoints(arc, vert)

    val ceArcLeft = arc.getLeft
    val cocircularJunction = arc.getRight == ceArcLeft

    val (arcRight, entryRightOpt) =
      walkCocircularChain(geometry.minAfterArc(arc), cocircularJunction, ceArcLeft, vert, rightSide = true)
    val (arcLeft, entryLeftOpt) =
      walkCocircularChain(geometry.maxBeforeArc(arc), cocircularJunction, ceArcLeft, vert, rightSide = false)

    entryRightOpt.foreach(e => removeEvent(e, arcRight))
    entryLeftOpt.foreach(e => removeEvent(e, arcLeft))

    // Invariants: neighbors exist for a valid circle event
    val edge = new VoronoiEdge(arcLeft.right.s1, arcRight.left.s2)
    geometry.addEdge(edge)
    val isLeftPoint = assignVertexToEdge(edge, point, vert, arcLeft, arcRight)

    val newBP = new BreakPoint(arcLeft.right.s1, arcRight.left.s2, edge, !isLeftPoint, this)
    geometry.addBreakPoint(newBP)
    arcRight.left = newBP
    arcLeft.right = newBP

    checkForCircleEvent(arcLeft)
    checkForCircleEvent(arcRight)
  }

  private def removeArcAndFinishBreakpoints(arc: Arc, vert: Point): Unit = {
    geometry.removeArc(arc)
    arc.left.finish(vert)
    arc.right.finish(vert)
    geometry.removeBreakPoint(arc.left)
    geometry.removeBreakPoint(arc.right)
  }

  /**
    * Advance across duplicate circle events on the cocircular junction.
    * @param rightSide if true, walk so that `getRight` is compared to `ceArcLeft`; else `getLeft`.
    */
  private def walkCocircularChain(
      initial: Option[(ArcKey, CircleEvent)],
      cocircularJunction: Boolean,
      ceArcLeft: Point,
      vert: Point,
      rightSide: Boolean
  ): (Arc, Option[(ArcKey, CircleEvent)]) = {
    initial match {
      case None => (null, None)
      case Some(firstEntry) =>
        var ar = firstEntry._1.asInstanceOf[Arc]
        var entry = firstEntry
        while (
          cocircularJunction && (if (rightSide) ar.getRight == ceArcLeft else ar.getLeft == ceArcLeft)
        ) {
          val next = calcEntry(ar, entry, vert).getOrElse(
            throw new IllegalStateException("Missing successor arc during cocircular walk")
          )
          ar = next._1.asInstanceOf[Arc]
          entry = next
        }
        (ar, Some(entry))
    }
  }

  /** @return whether the vertex was stored as the left (p1) endpoint of the edge */
  private def assignVertexToEdge(
      edge: VoronoiEdge,
      point: Point,
      vert: Point,
      arcLeft: Arc,
      arcRight: Arc
  ): Boolean = {
    // If the edges traced by these two arcs take a right turn, the vertex is above the current point.
    val turnsLeft = Point.ccw(arcLeft.right.edgeBegin, point, arcRight.left.edgeBegin) == 1
    // If it turns left, the next vertex will be below this vertex; combined with slope sign, pick p1 vs p2.
    val isLeftPoint = if (turnsLeft) edge.m < 0 else edge.m > 0
    if (isLeftPoint) edge.p1 = vert
    else edge.p2 = vert
    isLeftPoint
  }

  private def removeEvent(entry: (ArcKey, CircleEvent), arc: Arc): Unit = {
    val falseCe = entry._2
    if (falseCe != null) {
      events.remove(falseCe)
      geometry.addArc(arc)
    }
  }

  private def calcEntry(arc: Arc, entry: (ArcKey, CircleEvent), vert: Point): Option[(ArcKey, CircleEvent)] = {
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
