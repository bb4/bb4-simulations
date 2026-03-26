// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

/**
  * Possible status of the cell. determined by what's in it.
  * @author Barry Becker
  */
enum CellStatus(val symbol: String) {
  case EMPTY extends CellStatus(".")
  case SURFACE extends CellStatus("S")
  case FULL extends CellStatus("F")
  case OBSTACLE extends CellStatus("o")
  case ISOLATED extends CellStatus("I")

  /** Single-character label used for rendering and debugging (not the enum case name). */
  override def toString: String = symbol
}
