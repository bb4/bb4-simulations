/** Copyright by Barry G. Becker, 2015-26. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumberRange

/**
  * Stack of previous complex-plane ranges so the user can undo zooms.
  */
class History {
  private var stack: List[ComplexNumberRange] = Nil

  def addRangeToHistory(range: ComplexNumberRange): Unit =
    stack = range :: stack

  def hasHistory: Boolean = stack.nonEmpty

  /** @return the most recently pushed range; caller must ensure [[hasHistory]]. */
  def popLastRange: ComplexNumberRange = {
    val head = stack.head
    stack = stack.tail
    head
  }
}
