// Copyright by Barry G. Becker, 2005. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

/**
  * @author Barry Becker
  */
object TrebuchetConstants { // scales the size of the trebuchet's geometry
  val SCALE = 1.0
  // all distances are in meters, mass in Kilograms
  val HEIGHT = 2.0
  val RAMP_HEIGHT = 0.0
  // coefficient of dynamic friction on the ramp
  val RAMP_FRICTION = 0.4
  // the allowable ranges for these things will be .2*<default> to 4*<default>
  val MIN_FACTOR = 0.2
  val MAX_FACTOR = 4.0
  // defaults for parameters                                                                                                                                                              _
  val DEFAULT_COUNTER_WEIGHT_MASS = 10.0 // Kg

  val DEFAULT_CW_LEVER_LENGTH = 1.0 // M

  val DEFAULT_SLING_LEVER_LENGTH = 2.4
  val DEFAULT_SLING_LENGTH = 0.8
  val DEFAULT_PROJECTILE_MASS = 1
  val DEFAULT_SLING_RELEASE_ANGLE: Double = Math.PI / 8
  // debug level of 0 means no debug info, 3 is all debug info
  val DEBUG_LEVEL = 0
}

