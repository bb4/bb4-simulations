// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid

package object model {

  type TwoDArray = Array[Array[Double]]

  /** Cell boundaries are one of 3 types */
  enum Boundary:
    /** negates on neither, the vertical, or horizontal boundary */
    case NEITHER, HORIZONTAL, VERTICAL

  /**  The fields of a cell */
  enum CellProperty:
    case U, V, DENSITY

}
