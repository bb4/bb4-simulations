/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.simulation.complexmapping.algorithm.model.Box
import javax.vecmath.Point2d
import org.scalatest.funsuite.AnyFunSuite


class KnownNonTrivialZerosSuite extends AnyFunSuite {

  test("first zero imag part is about 14.13") {
    assert(Math.abs(KnownNonTrivialZeros.All.head.imag - 14.134725) < 1e-5)
    assert(KnownNonTrivialZeros.All.head.index == 1)
  }

  test("inViewport filters by critical-strip box") {
    val viewport = Box(new Point2d(0.0, 52.0), new Point2d(1.0, 0.0))
    val zeros = KnownNonTrivialZeros.inViewport(viewport)
    assert(zeros.size >= 10)
    assert(zeros.forall(z => z.real == 0.5))
    assert(zeros.forall(z => z.imag >= 0.0 && z.imag <= 52.0))
    assert(zeros.last.index <= 11)
  }

  test("inViewport excludes zeros outside imag range") {
    val viewport = Box(new Point2d(0.0, 20.0), new Point2d(1.0, 0.0))
    val zeros = KnownNonTrivialZeros.inViewport(viewport)
    // ρ1 ≈ 14.13 is inside; ρ2 ≈ 21.02 is above topY=20
    assert(zeros.map(_.index) == Seq(1))
  }

  test("labels include index and imag height") {
    val label = KnownNonTrivialZeros.All.head.label
    assert(label.startsWith("ρ1 "))
    assert(label.contains("14.13"))
  }
}
