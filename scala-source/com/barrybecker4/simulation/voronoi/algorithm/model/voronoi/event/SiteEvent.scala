// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor

import java.util.concurrent.atomic.AtomicLong


object SiteEvent {
  private val nextId = new AtomicLong(0L)

  private[event] def allocId(): Long = nextId.getAndIncrement()
}

/**
  * Site event for Fortune's sweep. `eventId` breaks ties so distinct events never
  * compare equal in a `TreeSet` (which would silently drop one).
  */
case class SiteEvent(p: Point, eventId: Long = SiteEvent.allocId()) extends Event with Comparable[SiteEvent] {

  def handleEvent(v: VoronoiProcessor): Unit = {
    v.handleSiteEvent(this.p)
  }

  /** 0 = site, 1 = circle — site events process before circle events at the same point. */
  protected def eventKind: Int = 0

  override def compareTo(o: SiteEvent): Int = {
    val byPoint = Point.minYOrderedCompareTo(this.p, o.p)
    if (byPoint != 0) return byPoint
    val byKind = Integer.compare(this.eventKind, o.eventKind)
    if (byKind != 0) return byKind
    java.lang.Long.compare(this.eventId, o.eventId)
  }
}
