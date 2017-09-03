// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.model

import com.barrybecker4.simulation.habitat.creatures.Creature


/**
  * @author Barry Becker
  */
class Cell(var xIndex: Int, var yIndex: Int) {

  /** The creatures in this cell. */
  var creatures: Set[Creature] = Set[Creature]()

  def addCreature(c: Creature) { creatures += c}
  def removeCreature(c: Creature) { creatures -= c}
  def getNumCreatures: Int = creatures.size
}
