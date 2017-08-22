/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.fluid.model

/**
  * @author Barry Becker
  */
object Boundary extends Enumeration {
  type Boundary = Value
  val

  /** negates on neither the vertical or horizontal boundary */
  NEITHER, HORIZONTAL, VERTICAL = Value
}
