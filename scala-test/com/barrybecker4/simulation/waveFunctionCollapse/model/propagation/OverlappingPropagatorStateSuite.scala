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

  test("agrees with dy=1 when overlap rows of p1 match p2 shifted up") {
    // N=3, dy=1: compare p1 rows 1..2 with p2 rows 0..1
    val p1 = pattern(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val p2 = pattern(4, 5, 6, 7, 8, 9, 0, 0, 0)
    assert(OverlappingPropagatorState.agrees(p1, p2, N = 3, dx = 0, dy = 1))
  }

  test("agrees false with dy=1 when vertical overlap differs") {
    val p1 = pattern(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val p2 = pattern(4, 5, 6, 7, 8, 0, 0, 0, 0)
    assert(!OverlappingPropagatorState.agrees(p1, p2, N = 3, dx = 0, dy = 1))
  }

  test("agrees with dx=-1 when left column of p1 matches right column of p2") {
    // N=2: overlap is x in [0,1) of p1 vs x+1 of p2 → p1[0],p1[2] vs p2[1],p2[3]
    val p1 = pattern(1, 2, 3, 4)
    val p2 = pattern(9, 1, 8, 3)
    assert(OverlappingPropagatorState.agrees(p1, p2, N = 2, dx = -1, dy = 0))
  }
}
