// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.common.math.{SINE_WAVE, WaveType}
import LocomotionParameters._

/**
  * Tweakable snake parameters that define locomotion.
  * @author Barry Becker
  */
object LocomotionParameters { // I used simulated annealing to come up with these optimal parameter values
  // When I originally started the snake's speed was about .21 using my best guess.
  // After optimization the snake's speed is about .33
  private[snake] val WAVE_SPEED = 0.00478 // .04  before optimization
  private[snake] val WAVE_AMPLITUDE = 0.026877 // .04
  private[snake] val WAVE_PERIOD = 3.6346 // 3.0

  private val USE_FRICTION = true
  private val MASS_SCALE = 1.5
  private val SPRING_K = 0.6
  private val SPRING_DAMPING = 1.2
  private val STATIC_FRICTION = 0.1
  private val DYNAMIC_FRICTION = 0.1
}

/**
  * @param direction  hard left = 1.0; hard right = -1.0; straight = 0
  * @param waveType genarl shape of the wave pattern
  * @param useFriction if true, then friction is used
  * @param waveSpeed the speed at which the muscular contraction wave travels down the body of the snake
  * @param waveAmplitude scale factor for the force function (must be greater than 0 and less than 1.0)
  * @param wavePeriod The period of the sinusoidal force function
  * @param massScale scales the overall mass of the snake up or down
  * @param springK the stiffness of the springs that make up the snakes body
  * @param springDamping The amount of spring damping present in the springs.
  *       This corresponds to how quickly the amplitude of the spring goes to 0.
  */
class LocomotionParameters(
   var direction: Double = 0,
   var waveType: WaveType = SINE_WAVE,
   var useFriction: Boolean = USE_FRICTION,
   var waveSpeed: Double = LocomotionParameters.WAVE_SPEED,
   var waveAmplitude: Double = LocomotionParameters.WAVE_AMPLITUDE,
   var wavePeriod: Double = LocomotionParameters.WAVE_PERIOD,
   var massScale: Double = LocomotionParameters.MASS_SCALE,
   var springK: Double = LocomotionParameters.SPRING_K,
   var springDamping: Double = LocomotionParameters.SPRING_DAMPING,
   var staticFriction: Double = LocomotionParameters.STATIC_FRICTION,
   var dynamicFriction: Double = LocomotionParameters.DYNAMIC_FRICTION)
