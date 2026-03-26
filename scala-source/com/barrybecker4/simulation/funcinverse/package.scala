// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation

import java.lang.Math.{PI, sin}
import scala.collection.immutable.Range

package object funcinverse {

  def sinusoidalFunc(period: Double, numSteps: Int): Array[Double] = {
    val raw = Range.BigDecimal(0, period + PI / numSteps, PI / numSteps)
      .map(x => x.toDouble + sin(x.toDouble))
      .toArray
    val max = raw.last
    raw.map(_ / max)
  }

  /** Monotonic sequence from 0 toward 1, with last value exactly 1.0 (for inverse visualization). */
  private[funcinverse] def randomIncreasingArray(length: Int, rng: java.util.Random): Array[Double] = {
    val array = new Array[Double](length)
    var current = 0.0
    for (i <- 0 until length - 1) {
      current += rng.nextDouble() * (1.0 - current) / 10.0
      array(i) = current
    }
    array(length - 1) = 1.0
    array
  }
}
