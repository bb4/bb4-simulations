// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.liquid.model.CellDimensions.CELL_SIZE

/**
  * The cell/s x and y dimensions. Should be square.
  * @author Barry Becker
  */
object CellDimensions {
  val CELL_SIZE: Double = 10.0
  val INVERSE_CELL_SIZE: Double = 1.0 / CellDimensions.CELL_SIZE
}

class CellDimensions() {

  // size of a cell
  final val dx: Double = CELL_SIZE
  final val dy: Double = CELL_SIZE

  // squares of edge lengths
  final val dxSq: Double = dx * dx
  final val dySq: Double = dy * dy

}
