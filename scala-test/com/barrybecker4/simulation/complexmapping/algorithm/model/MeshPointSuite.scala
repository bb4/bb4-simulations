/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.simulation.complexmapping.algorithm.functions.IntPowerFunction
import org.scalatest.funsuite.AnyFunSuite

class MeshPointSuite extends AnyFunSuite {

  test("transform applies function and keeps value") {
    val pt = new MeshPoint(2.0, 0.0, 0.42)
    val out = pt.transform(IntPowerFunction(), 2)
    assert(out.x == 4.0)
    assert(out.y == 0.0)
    assert(out.value == 0.42)
  }

  test("addition keeps left-hand value") {
    val a = new MeshPoint(1.0, 2.0, 0.1)
    val b = new MeshPoint(3.0, 4.0, 0.9)
    val out = a + b
    assert(out.x == 4.0)
    assert(out.y == 6.0)
    assert(out.value == 0.1)
  }

  test("subtraction keeps left-hand value") {
    val a = new MeshPoint(5.0, 7.0, 0.3)
    val b = new MeshPoint(2.0, 1.0, 0.8)
    val out = a - b
    assert(out.x == 3.0)
    assert(out.y == 6.0)
    assert(out.value == 0.3)
  }

  test("scale keeps value") {
    val pt = new MeshPoint(2.0, -4.0, 0.55)
    val out = pt.scale(0.5)
    assert(out.x == 1.0)
    assert(out.y == -2.0)
    assert(out.value == 0.55)
  }
}
