// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import org.scalatest.funsuite.AnyFunSuite

class EdgePhysicsTest extends AnyFunSuite {

  test("getForce returns finite vector at equilibrium") {
    val p1 = new Particle(0, 0, 1.0)
    val p2 = new Particle(1, 0, 1.0)
    val edge = new Edge(p1, p2)
    val f = edge.getForce(Edge.DefaultSpringK, Edge.DefaultSpringDamping)
    assert(!f.x.isNaN && !f.y.isNaN)
  }

  test("getForce responds when edge is stretched beyond effective length") {
    val p1 = new Particle(0, 0, 1.0)
    val p2 = new Particle(1, 0, 1.0)
    val edge = new Edge(p1, p2)
    p2.set(3.0, 0.0)
    val f = edge.getForce(Edge.DefaultSpringK, Edge.DefaultSpringDamping)
    assert(f.length > 0.0)
    assert(!f.x.isNaN && !f.y.isNaN)
  }
}
