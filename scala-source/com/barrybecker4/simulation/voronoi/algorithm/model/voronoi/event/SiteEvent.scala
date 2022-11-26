// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor


class SiteEvent(p: Point) extends Event(p) with Comparable[SiteEvent] {

  def handleEvent(v: VoronoiProcessor): Unit = {
    v.handleSiteEvent(this.p)
  }

  override def compareTo(o: SiteEvent): Int = Point.minYOrderedCompareTo(this.p, o.p)
}
