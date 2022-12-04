// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.VoronoiGeometry.POLYGON_PCT
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent

import scala.collection.mutable


object VoronoiGeometry {
  private val POLYGON_PCT = 0.8
}

class VoronoiGeometry {

  private val arcs = new mutable.TreeMap[ArcKey, CircleEvent]()
  private val breakPoints = new mutable.HashSet[BreakPoint]()
  private var edgeList: IndexedSeq[VoronoiEdge] = IndexedSeq()
  private var pointToEdgesMap: Map[Point, Set[VoronoiEdge]] = Map()

  def hasArcs: Boolean = arcs.nonEmpty
  def getArcs: mutable.TreeMap[ArcKey, CircleEvent] = arcs
  def getEdgeList: IndexedSeq[VoronoiEdge] = edgeList
  def getBreakPoints: mutable.HashSet[BreakPoint] = breakPoints

  def addArc(arc: Arc, circleEvent: CircleEvent): Unit = arcs.put(arc, circleEvent)
  def addArc(arc: Arc): Unit = arcs.put(arc, null)
  def removeArc(arc: Arc): Unit = arcs.remove(arc)
  def minAfterArc(arc: Arc): (ArcKey, CircleEvent) = arcs.minAfter(arc).get
  def maxBeforeArc(arc: Arc): (ArcKey, CircleEvent) = arcs.maxBefore(arc).get

  def addBreakPoint(bp: BreakPoint): Unit = breakPoints.add(bp)
  def removeBreakPoint(bp: BreakPoint): Unit = breakPoints.remove(bp)

  def addEdge(edge: VoronoiEdge): Unit = {
    addEdgeForPoint(edge.site1, edge)
    addEdgeForPoint(edge.site2, edge)
    edgeList :+= edge
  }

  private def addEdgeForPoint(pt: Point, edge: VoronoiEdge): Unit = {
    if (pt != null) {
      if (!pointToEdgesMap.contains(pt)) {
        pointToEdgesMap += pt -> Set(edge)
      } else {
        pointToEdgesMap += pt -> (pointToEdgesMap(pt) + edge)
      }
    }
  }

  def findFloorEntry(point: Point): (ArcKey, CircleEvent) = {
    val arcToSearchFor = new ArcQuery(point)
    if (arcs.contains(arcToSearchFor)) arcs.find(p => p._1.compareTo(arcToSearchFor) == 0).get
    else arcs.maxBefore(arcToSearchFor).get
  }

  def getPolygonsForPoints: Iterable[Seq[Point]] = {
    pointToEdgesMap.keys.map(getPolygonPointsForSite).filter(_.size > 2)
  }

  private def getPolygonPointsForSite(site: Point): Seq[Point] = {
    val edges: Set[VoronoiEdge] = pointToEdgesMap(site)
    var points: Set[Point] = Set()

    edges.foreach(edge => {
      if (edge.p1 != null && !edge.p1.y.isInfinite && edge.p2 != null && !edge.p2.y.isInfinite) {
        points += edge.p1
        points += edge.p2
      }
    })

    def angleOf(pt: Point): Double = {
      val deltaX = pt.x - site.x
      val deltaY = pt.y - site.y
      Math.atan2(deltaY, deltaX)
    }

    points.toSeq.sortBy(angleOf)map(p => interpolate(site, p, POLYGON_PCT))
  }

  /**
    * @param t the linear interpolation parameter in [0, 1]
    * @return a point that is t of the way from pt1 to pt2
    */
  private def interpolate(pt1: Point, pt2: Point, t: Double): Point = {
    val deltaX = pt2.x - pt1.x
    val deltaY = pt2.y - pt1.y
    new Point(pt1.x + t * deltaX, pt1.y + t * deltaY)
  }
}
