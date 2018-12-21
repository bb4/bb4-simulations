// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import scala.collection.immutable.Range
import Math._

/**
  * Different types of functions to find the inverses of
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  case class Val(name: String, func: Array[Double]) extends super.Val

  private def sinusoidalFunc(period: Double, numSteps: Int): Array[Double] = {
    val array = Range.BigDecimal(0, period + PI/numSteps, PI/numSteps)
      .map(x => 1.0 * x.toDouble + sin(x.toDouble)).toArray
    val max = array.last
    for (i <- array.indices) {
      array(i) /= max
    }
    array
  }

  val LINEAR = Val("Linear", Array(0, 0.5, 1.0))
  val ALMOST_LINEAR = Val("Almost Linear", Array(0, 0.48, 0.52, 1.0))
  val QUADRATIC = Val("Quadratic", Array(0, 1.0/16.0, 1/4.0, 9.0/16.0, 1.0))
  val EXPONENTIAL = Val("Exponential", Array(0, 1/16.0, 2/16.0, 1/4.0, 1/2.0, 1.0))
  val SINE = Val("Sine", Range.BigDecimal(0, PI/2 + PI/200, PI/200).map(x => sin(x.toDouble)).toArray)

  val INCREASING_SINE_COARSE = Val("Increasing Coarse Sinusoidal 3PI", sinusoidalFunc(3 * PI, 50))
  val INCREASING_SINE_DETAIL = Val("Increasing Sinusoidal 5PI many steps", sinusoidalFunc(5 * PI, 400))

  val RANDOM_UP = Val("Random Up", {
    var current: Double = 0
    val array = Range(0, 100).map(x => {
      current += (Math.random() * (1.0 - current) / 10.0)
      current
    }).toArray
    array(array.length - 1) = 1.0
    array
  })

  val VALUES: Array[Val] = Array(
    ALMOST_LINEAR, LINEAR, QUADRATIC, EXPONENTIAL, SINE,
    INCREASING_SINE_COARSE, INCREASING_SINE_DETAIL, RANDOM_UP)
}
