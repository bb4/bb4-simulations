// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import java.lang.Math.{PI, sin}
import scala.collection.immutable.Range


/**
  * Different types of functions to find the inverses of
  * @author Barry Becker
  */
enum FunctionType(val name: String, val func: Array[Double]):

  case LINEAR extends FunctionType("Linear", Array(0, 0.5, 1.0))
  case ALMOST_LINEAR extends FunctionType("Almost Linear", Array(0, 0.48, 0.52, 1.0))
  case QUADRATIC extends FunctionType("Quadratic", Array(0, 1.0/16.0, 1/4.0, 9.0/16.0, 1.0))
  case EXPONENTIAL extends FunctionType("Exponential", Array(0, 1/16.0, 2/16.0, 1/4.0, 1/2.0, 1.0))
  case SINE extends FunctionType("Sine", Range.BigDecimal(0, PI/2 + PI/200, PI/200).map(x => sin(x.toDouble)).toArray)

  case INCREASING_SINE_COARSE extends FunctionType("Increasing Coarse Sinusoidal 3PI",
    sinusoidalFunc(3 * PI, 50))
  case INCREASING_SINE_DETAIL extends FunctionType("Increasing Sinusoidal 5PI many steps",
    sinusoidalFunc(5 * PI, 400))

  case RANDOM_UP extends FunctionType("Random Up", randomIncreasingArray(100, new java.util.Random()))

end FunctionType
