/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumberRange

/**
  * Maintains regions (ranges) in the complex space that were visited, so we can back up.
  * @author Barry Becker
  */
class History {
  /** range of bounding box in complex plane. */
  private var stack: List[ComplexNumberRange] = List()

  def addRangeToHistory(range: ComplexNumberRange) { stack = range :: stack }
  def hasHistory: Boolean = stack.nonEmpty

  /** @return the last range viewed in the history */
  def popLastRange: ComplexNumberRange = {
    val head = stack.head
    stack = stack.tail
    head
  }
}
