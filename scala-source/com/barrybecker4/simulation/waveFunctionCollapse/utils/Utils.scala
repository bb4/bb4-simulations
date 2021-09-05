// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.utils

object Utils {

  def randomFromArray(array: Array[Double], r: Double): Int = {
    var sum: Double = array.sum

    if (sum == 0.0) {
      for (j <- array.indices) array(j) = 1.0
      sum = array.sum
    }

    for (j <- array.indices) array(j) /= sum

    var i = 0
    var x = 0.0

    while (i < array.length) {
      x += array(i)
      if (r <= x) return i
      i += 1
    }

    0
  }

  def trimToColor(c: Int): Int = if (c <= 255) c else 255
}
