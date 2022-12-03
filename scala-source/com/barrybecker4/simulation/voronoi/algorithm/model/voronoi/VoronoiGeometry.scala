// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.CircleEvent

import scala.collection.mutable


class VoronoiGeometry {

  private val arcs = new mutable.TreeMap[ArcKey, CircleEvent]()
  private val breakPoints = new mutable.HashSet[BreakPoint]()
  private var edgeList: IndexedSeq[VoronoiEdge] = IndexedSeq()

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

  def addEdge(edge: VoronoiEdge): Unit = edgeList :+= edge

  def findFloorEntry(point: Point): (ArcKey, CircleEvent) = {
    val arcToSearchFor = new ArcQuery(point)
    if (arcs.contains(arcToSearchFor)) arcs.find(p => p._1.compareTo(arcToSearchFor) == 0).get
    else arcs.maxBefore(arcToSearchFor).get
  }
}
