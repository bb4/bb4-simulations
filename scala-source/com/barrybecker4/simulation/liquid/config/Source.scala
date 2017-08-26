// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.common.geometry.Location
import javax.vecmath.Vector2d


/**
  * Represents a source of liquid in the simulation.
  * Like a spigot.
  * @param velocity       speed at which the liquid will flow in the
  *                       direction that all the cells in the source region are flowing.
  * @param startTime      time when the flow will start.
  * @param duration       magnitude of time that the liquid will flow. If negative, it will flow forever.
  * @param repeatInterval time from when the liquid starts flowing to when it should start flowing again.
  *                       if negative, then it will only flow once.
  * @author Barry Becker
  */
class Source(start: Location, stop: Location,
             var velocity: Vector2d, var startTime: Double, var duration: Double, var repeatInterval: Double)
  extends Region(start, stop) {

  assert(startTime >= 0)
  if (repeatInterval > 0) assert(duration < repeatInterval, " The duration cannot be longer than the repeatInterval")

  def this(start: Location, stop: Location, velocity: Vector2d) { this(start, stop, velocity, 0, -1, -1) }
  def this(start: Location, velocity: Vector2d) { this(start, null, velocity) }
  def getVelocity: Vector2d = velocity

  /**
    * @param time the current time in the simulation
    * @return true if the source is currently flowing.
    */
  def isOn(time: Double): Boolean = {
    var on = false
    if (time >= startTime) {
      val offsetTime = time - startTime
      on = true
      if (repeatInterval > 0 && duration > 0) {
        val timeWithinInterval = offsetTime - (offsetTime / repeatInterval).toInt * repeatInterval
        on = timeWithinInterval < duration
      }
      else if (duration > 0) on = time < startTime + duration
    }
    on
  }
}