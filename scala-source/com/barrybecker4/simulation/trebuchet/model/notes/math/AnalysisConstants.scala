// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.notes.math

/**
  * Inputs and constants
  * See See file:///E:/projects/bb4/bb4-simulations/scala-source/com/barrybecker4/simulation/trebuchet/doc/Trebuchet_user_manual.pdf
  * beta is the initial angle of the counterweight cable with the lever arm. Positive radians.
  * Since  beta = theta if the counterweight is suspended, its not included.
  */
object AnalysisConstants {

  // initial angle of the lever arm with the vertical. Negative radians.
  val INITIAL_THETA: Double = -Math.PI / 6.0
  // angle of the sling cable with the lever arm at the time of projectile release. Positive radians. beta = -theta
  //val INITIAL_ALPHA: Double = Math.PI / 2.0 + INITIAL_THETA

  val PROJECTILE_MASS = 0.2 // kg
  val COUNTERWEIGHT_MASS = 10.0 // kg
  val LEVER_MASS = 2.0 // Kg
  val L1 = 1.0 // distance between fulcrum and counterweight end of beam
  val L2 = 3.5 // distance between fulcrum and payload end of beam
  val L3 = 1.0 // payload cable length
  val L4 = 1.0 // counterweight cable length
  val s = 3.4 // distance between payload end of the beam and the beam COMass (point G)
  val STATIC_FRICTION = 0.1 // coefficient of static friction between the ring and finger
  val SLING_RELEASE_ANGLE: Double = -4.0 * Math.PI / 6.0  // theta angle at which point the sling is released
  val GRAVITY = 9.8 // gravity. m/s^2

  val TIME_STEP = 0.001 // time increment in seconds when integrating

}
