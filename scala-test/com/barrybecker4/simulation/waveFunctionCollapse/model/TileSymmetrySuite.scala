// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import org.scalatest.funsuite.AnyFunSuite

class TileSymmetrySuite extends AnyFunSuite {

  test("fromJson maps known symmetry letters") {
    assert(TileSymmetry.fromJson("L") == TileSymmetry.L)
    assert(TileSymmetry.fromJson("T") == TileSymmetry.T)
    assert(TileSymmetry.fromJson("I") == TileSymmetry.I)
    assert(TileSymmetry.fromJson("\\") == TileSymmetry.Slash)
    assert(TileSymmetry.fromJson("X") == TileSymmetry.X)
    assert(TileSymmetry.fromJson("unknown") == TileSymmetry.X)
  }

  test("cardinalities match mxgmn tables") {
    assert(TileSymmetry.L.cardinality == 4)
    assert(TileSymmetry.T.cardinality == 4)
    assert(TileSymmetry.I.cardinality == 2)
    assert(TileSymmetry.Slash.cardinality == 2)
    assert(TileSymmetry.X.cardinality == 1)
  }
}
