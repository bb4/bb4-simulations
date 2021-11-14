// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import scala.collection.immutable.Range
import Math._
import com.barrybecker4.simulation.funcinverse._


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

  case RANDOM_UP extends FunctionType("Random Up", {
    var current: Double = 0
    val array = Range(0, 100).map(x => {
      current += (Math.random() * (1.0 - current) / 10.0)
      current
    }).toArray
    array(array.length - 1) = 1.0
    array
  })

end FunctionType
