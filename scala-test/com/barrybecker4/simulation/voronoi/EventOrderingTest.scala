// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{Arc, Point}
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event.{CircleEvent, SiteEvent}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class EventOrderingTest extends AnyFunSuite {

  test("distinct site events at the same point do not compare equal") {
    val p = Point(10, 20)
    val a = SiteEvent(p)
    val b = SiteEvent(p)
    // Two distinct event instances must not be treated as equal by TreeSet ordering
    assert(a.compareTo(b) != 0 || (a eq b))
  }

  test("TreeSet retains a site event and a circle event that share the same point") {
    val p = Point(50, 50)
    val site = SiteEvent(p)
    // Minimal Arc for CircleEvent construction (processor only needed for Arc identity)
    val proc = new VoronoiProcessor(IndexedSeq(Point(10, 10), Point(90, 90)))
    val arc = new Arc(Point(30, 30), proc)
    val circle = new CircleEvent(arc, p, Point(50, 40))

    val events = new mutable.TreeSet[SiteEvent]()
    assert(events.add(site))
    assert(events.add(circle), "circle event at same point must not be dropped by TreeSet")
    assert(events.size == 2)
  }

  test("two circle events at the same point with different arcs both stay in TreeSet") {
    val p = Point(70, 70)
    val proc = new VoronoiProcessor(IndexedSeq(Point(10, 10), Point(90, 90)))
    val arc1 = new Arc(Point(20, 20), proc)
    val arc2 = new Arc(Point(40, 40), proc)
    val c1 = new CircleEvent(arc1, p, Point(70, 60))
    val c2 = new CircleEvent(arc2, p, Point(70, 60))

    val events = new mutable.TreeSet[SiteEvent]()
    assert(events.add(c1))
    assert(events.add(c2), "second circle event must not be dropped")
    assert(events.size == 2)
    assert(c1.compareTo(c2) != 0)
  }
}
