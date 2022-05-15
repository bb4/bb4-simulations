// Copyright by Barry G. Becker, 2005. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

/**
  * All distances are in meters, mass in Kilograms
  */
object TrebuchetConstants { // scales the size of the trebuchet's geometry
  val INITIAL_SCALE = 11.0
  // all distances are in meters, mass in Kilograms
  val HEIGHT = 14.0
  // coefficient of dynamic friction on the ramp
  val RAMP_FRICTION = 0.4
  // the allowable ranges for these things will be .2*<default> to 4*<default>
  val MIN_FACTOR = 0.2
  val MAX_FACTOR = 4.0
  // defaults for parameters                                                                                                                                                              _
  val DEFAULT_COUNTER_WEIGHT_MASS = 5.0 // Kg

  val DEFAULT_CW_LEVER_LENGTH = 7.0 // M

  // move these to sling
  val DEFAULT_SLING_LEVER_LENGTH = 16.8
  val DEFAULT_SLING_LENGTH = 5.6
  val DEFAULT_PROJECTILE_MASS = 0.2
  val DEFAULT_SLING_RELEASE_ANGLE: Double = Math.PI / 16

  // debug level of 0 means no debug info, 3 is all debug info
  val DEBUG_LEVEL = 0
}

