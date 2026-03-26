// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagation

import org.scalatest.funsuite.AnyFunSuite

class OverlappingPropagatorStateSuite extends AnyFunSuite {

  private def pattern(values: Int*): Array[Byte] = values.map(_.toByte).toArray

  test("agrees identical patterns full overlap") {
    val p = pattern(1, 2, 3, 4)
    assert(OverlappingPropagatorState.agrees(p, p, N = 2, dx = 0, dy = 0))
  }

  test("agrees false when overlapping region differs") {
    val p1 = pattern(1, 1, 1, 1)
    val p2 = pattern(1, 1, 1, 2)
    assert(!OverlappingPropagatorState.agrees(p1, p2, N = 2, dx = 0, dy = 0))
  }

  test("agrees with dx=1 when right column of p1 matches left column of p2") {
    val p1 = pattern(1, 2, 3, 4)
    val p2 = pattern(2, 9, 4, 8)
    assert(OverlappingPropagatorState.agrees(p1, p2, N = 2, dx = 1, dy = 0))
  }

  test("agrees false with dx=1 when overlap column differs") {
    val p1 = pattern(1, 2, 3, 4)
    val p2 = pattern(9, 9, 4, 8)
    assert(!OverlappingPropagatorState.agrees(p1, p2, N = 2, dx = 1, dy = 0))
  }
}
