// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation

import java.lang.Math.{PI, sin}
import scala.collection.immutable.Range

package object funcinverse {

  def sinusoidalFunc(period: Double, numSteps: Int): Array[Double] = {
    val array = Range.BigDecimal(0, period + PI/numSteps, PI/numSteps)
      .map(x => 1.0 * x.toDouble + sin(x.toDouble)).toArray
    val max = array.last
    for (i <- array.indices) {
      array(i) /= max
    }
    array
  }
}
