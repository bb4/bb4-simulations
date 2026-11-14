// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config.json

/**
  * Gson DTO for a liquid source.
  * [[start]] / [[stop]] are `[x, y]`; [[velocity]] is `[vx, vy]`.
  * Timing fields use boxed Doubles so missing JSON keys stay null (defaults applied in Conditions).
  */
case class SourceJson(
  start: Array[Int],
  stop: Array[Int],
  velocity: Array[Double],
  startTime: java.lang.Double,
  duration: java.lang.Double,
  repeatInterval: java.lang.Double
)
