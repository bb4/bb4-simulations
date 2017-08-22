/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.fluid.model

/**
  * The fields of a cell.
  * @author Barry Becker
  */
object CellProperty extends Enumeration {
  type CellProperty = Value
  val U, V, DENSITY = Value
}
