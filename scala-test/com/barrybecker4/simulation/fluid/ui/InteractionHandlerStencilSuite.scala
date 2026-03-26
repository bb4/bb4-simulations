// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import org.scalatest.funsuite.AnyFunSuite

class InteractionHandlerStencilSuite extends AnyFunSuite {

  test("dragStencilIndices includes i+1 and j+1 when away from borders") {
    val cells = InteractionHandler.dragStencilIndices(5, 5, 10, 10).toSet
    assert(cells.contains((5, 5)))
    assert(cells.contains((6, 5)))
    assert(cells.contains((4, 5)))
    assert(cells.contains((5, 6)))
    assert(cells.contains((5, 4)))
    assert(cells.size == 9)
  }

  test("dragStencilIndices clips at grid edges") {
    val cells = InteractionHandler.dragStencilIndices(1, 1, 3, 3)
    assert(cells.map(_._1).min == 1)
    assert(cells.map(_._2).min == 1)
    assert(cells.size == 4) // 2x2 corner stencil
  }
}
