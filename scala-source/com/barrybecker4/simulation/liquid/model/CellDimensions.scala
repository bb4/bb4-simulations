// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.liquid.model.CellDimensions.CELL_SIZE

/**
  * The cell/s x and y dimensions. Should be square.
  * @author Barry Becker
  */
object CellDimensions {
  val CELL_SIZE = 10.0
  val INVERSE_CELL_SIZE: Double = 1.0 / CellDimensions.CELL_SIZE
}

class CellDimensions() {

  dx = CELL_SIZE
  dy = CELL_SIZE
  dxSq = dx * dx
  dySq = dy * dy

  // size of a cell
  final var dx = .0
  final var dy = .0

  // squares of edge lengths
  final var dxSq = .0
  final var dySq = .0
}