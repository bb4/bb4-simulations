package com.barrybecker4.simulation.water.ui

import org.scalatest.funsuite.AnyFunSuite

class EnvSizeSuite extends AnyFunSuite {

  test("envDims returns None for non-positive panel size") {
    assert(EnvSize.envDims(0, 400, 10).isEmpty)
    assert(EnvSize.envDims(400, 0, 10).isEmpty)
    assert(EnvSize.envDims(-1, 200, 10).isEmpty)
  }

  test("envDims subtracts 2*offset from each dimension") {
    assert(EnvSize.envDims(820, 420, 10).contains((800, 400)))
  }

  test("envDims clamps to at least 1 when panel is smaller than inset") {
    assert(EnvSize.envDims(15, 15, 10).contains((1, 1)))
    assert(EnvSize.envDims(5, 100, 10).contains((1, 80)))
  }
}
