// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.utils

object Utils {

  /**
    * Picks an index from a weighted distribution.
    * Mutates `array` in place to a normalized CDF-friendly distribution
    * (caller should treat contents as scratch after the call).
    * If the sum was 0, assigns uniform weights first.
    *
    * @return sampled index for random `r` in [0, 1]
    */
  def randomFromArray(array: Array[Double], r: Double): Int = {
    var sum: Double = 0.0
    var j = 0
    while (j < array.length) {
      sum += array(j)
      j += 1
    }

    if (sum == 0.0) {
      j = 0
      while (j < array.length) {
        array(j) = 1.0
        j += 1
      }
      sum = array.length.toDouble
    }

    j = 0
    while (j < array.length) {
      array(j) /= sum
      j += 1
    }

    var i = 0
    var x = 0.0
    while (i < array.length) {
      x += array(i)
      if (r <= x) return i
      i += 1
    }

    0
  }
}
