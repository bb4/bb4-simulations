// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.model

import com.barrybecker4.simulation.habitat.creatures.Creature


/**
  * Cell in the grid containing creatuers
  */
class Cell(var xIndex: Int, var yIndex: Int) {

  /** The creatures in this cell. */
  var creatures: Set[Creature] = Set[Creature]()

  def addCreature(c: Creature): Unit = { creatures += c}
  def removeCreature(c: Creature): Unit = { creatures -= c}
  
  override def toString: String = 
    s"Cell($xIndex, $yIndex)"
}
