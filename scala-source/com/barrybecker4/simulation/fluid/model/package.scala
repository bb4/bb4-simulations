// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid

package object model {

  type TwoDArray = Array[Array[Double]]

  /** Cell boundaries are one of 3 types */
  object Boundary extends Enumeration {
    type Boundary = Value

    /** negates on neither, the vertical, or horizontal boundary */
    val NEITHER, HORIZONTAL, VERTICAL = Value
  }

  /**  The fields of a cell */
  object CellProperty extends Enumeration {
    type CellProperty = Value
    val U, V, DENSITY = Value
  }

}
