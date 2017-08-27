// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.common.geometry.Location


/**
  * Represents a region of something (like a source sink, or liquid block) in the simulation.
  * @param start starting location for the rectangular region
  * @param stop optional stopping location for the rectangular region
  * @author Barry Becker
  */
class Region(var start: Location, var stop: Location) {
  def getStart: Location = start
  def getStop: Location = { if (stop == null)  start else stop }
}
