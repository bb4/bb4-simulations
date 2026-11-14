/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

class ConcentrationFieldSuite extends AnyFunSuite {

  test("wrap matches floorMod semantics") {
    assert(ConcentrationField.wrap(10, 100) == 10)
    assert(ConcentrationField.wrap(-7, 100) == 93)
    assert(ConcentrationField.wrap(107, 100) == 7)
    assert(ConcentrationField.wrap(100, 100) == 0)
    assert(ConcentrationField.wrap(0, 1) == 0)
    assert(ConcentrationField.wrap(-1, 1) == 0)
  }

  test("apply and update round-trip") {
    val f = new ConcentrationField(4, 3)
    f.update(2, 1, 0.42)
    assert(f(2, 1) == 0.42)
    assert(f(0, 0) == 0.0)
  }

  test("laplacian is fully periodic on a 3x3 spike") {
    val f = new ConcentrationField(3, 3)
    f.update(0, 0, 1.0)
    // Neighbors of (0,0): (1,0), (2,0) via wrap -1, (0,1), (0,2) via wrap -1 — all 0
    // laplacian = 0+0+0+0 - 4*1 = -4
    assert(f.laplacian(0, 0) == -4.0)
    // Neighbor (1,0) sees (0,0)=1 among four neighbors → 1 - 0 = 1
    assert(f.laplacian(1, 0) == 1.0)
    assert(f.laplacian(0, 1) == 1.0)
    assert(f.laplacian(2, 0) == 1.0) // wrap neighbor of (0,0)
    assert(f.laplacian(0, 2) == 1.0)
  }
}
