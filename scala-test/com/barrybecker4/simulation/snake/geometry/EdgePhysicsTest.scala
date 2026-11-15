// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import org.scalatest.funsuite.AnyFunSuite

class EdgePhysicsTest extends AnyFunSuite {

  private def edgeAt(x1: Double, y1: Double, x2: Double, y2: Double): Edge =
    new Edge(new Particle(x1, y1, 1.0), new Particle(x2, y2, 1.0))

  test("getForce returns finite near-zero vector at equilibrium") {
    val edge = edgeAt(0, 0, 1, 0)
    val f = edge.getForce(Edge.DefaultSpringK, Edge.DefaultSpringDamping)
    assert(!f.x.isNaN && !f.y.isNaN)
    assert(f.length < 1e-9)
  }

  test("getForce restores when edge is moderately stretched") {
    val edge = edgeAt(0, 0, 1, 0)
    edge.secondParticle.set(1.5, 0.0)
    val f = edge.getForce(Edge.DefaultSpringK, 0.0) // no damping; isolate spring term
    assert(f.length > 0.0)
    assert(!f.x.isNaN && !f.y.isNaN)
    // length > effective ⇒ (effective - length) < 0 ⇒ scaled force pulls back along p2-p1
    assert(f.x < 0.0)
  }

  test("getForce responds in over-stretch branch (length > 2 * effectiveLength)") {
    val edge = edgeAt(0, 0, 1, 0)
    edge.secondParticle.set(3.0, 0.0) // length 3 > 2 * 1
    val f = edge.getForce(Edge.DefaultSpringK, 0.0)
    assert(f.length > 0.0)
    assert(!f.x.isNaN && !f.y.isNaN)
  }

  test("getForce stays finite for coincident particles (near-zero length)") {
    val edge = edgeAt(0, 0, 1, 0)
    edge.secondParticle.set(0.0, 0.0)
    val f = edge.getForce(Edge.DefaultSpringK, Edge.DefaultSpringDamping)
    assert(!f.x.isNaN && !f.y.isNaN)
    assert(!f.x.isInfinite && !f.y.isInfinite)
  }

  test("setContraction rejects non-positive contraction") {
    val edge = edgeAt(0, 0, 1, 0)
    intercept[IllegalArgumentException] {
      edge.setContraction(0.0)
    }
    intercept[IllegalArgumentException] {
      edge.setContraction(-0.5)
    }
  }

  test("setContraction updates effectiveLength") {
    val edge = edgeAt(0, 0, 2, 0)
    assert(math.abs(edge.restingLength - 2.0) < 1e-12)
    edge.setContraction(0.5)
    assert(math.abs(edge.effectiveLength - 1.0) < 1e-12)
  }
}
