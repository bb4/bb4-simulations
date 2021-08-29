// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem

import com.barrybecker4.common.geometry.Location


/** Something that can be panned in x and y directions */
trait Panable {

  def incrementOffset(incrementAmount: Location): Unit
}
