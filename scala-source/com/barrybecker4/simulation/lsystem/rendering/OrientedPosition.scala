// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.rendering

import com.barrybecker4.common.geometry.IntLocation


/**
  * The current position and orientation used while rendering.
  * @author Barry Becker
  */
class OrientedPosition private[rendering](var x: Double, var y: Double, var angle: Double) {

  /** Copy constructor */
  def this(pos: OrientedPosition) {
    this(pos.x, pos.y, pos.angle)
  }

  private[rendering] def getLocation = new IntLocation(y.toInt, x.toInt)
}
