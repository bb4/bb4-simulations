// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.simulation.dungeon.model.Room

// todo: no need for generic T, just use Room always
abstract class BspNode[T]  {

  def getChildren: Set[T]

}
