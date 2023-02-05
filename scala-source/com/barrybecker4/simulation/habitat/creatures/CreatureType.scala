// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import java.awt.Color
import com.barrybecker4.simulation.habitat.creatures.CreatureType._


/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures.
  * Add more creatures like sheep, fox, chickens, ants, vulture, etc.
  * This is probably the best pattern to follow for enums with properties and methods.
  * @author Barry Becker
  */
object CreatureType  {

  val GRASS = new CreatureType("grass",   new Color(50, 210, 20),   2.0,   0.0,     0.0,    11,    42,     1)
  val COW = new CreatureType("cow", new Color(140, 60, 170), 15.0,   0.002,   0.0001, 25,    94,    10)
  val RAT = new CreatureType("rat",     new Color(150, 115, 40),    2.0,   0.005,   0.002,   8,    30,     1)
  val CAT = new CreatureType("cat",     new Color(200, 105, 20),     5.0,   0.01,    0.004,  15,    58,     4)
  val LION = new CreatureType("lion",   new Color(210, 210, 0),    9.0,   0.02,    0.008,  21,    83,     6)

  val VALUES = Array(GRASS, COW, RAT, CAT, LION)

  val predatorMap: Map[CreatureType, Set[CreatureType]] = Map(
    GRASS -> Set(COW, RAT),
    COW -> Set(LION),
    RAT -> Set(CAT, LION),
    CAT -> Set(LION),
    LION -> Set[CreatureType]()
  )

  var preyMap: Map[CreatureType, Set[CreatureType]] = Map[CreatureType, Set[CreatureType]]()
  for (creature <- VALUES) {
    var preys = Set[CreatureType]()
    for (potentialPrey <- VALUES) {
      val preds: Set[CreatureType] = predatorMap(potentialPrey)
      if (preds.contains(creature)) preys += potentialPrey
    }
    preyMap += (creature -> preys)
  }
}

class CreatureType(val name: String, val color: Color,
                   var size: Double, var maxSpeed: Double, var normalSpeed: Double,
                   var gestationPeriod: Int, var starvationThreshold: Int, var nutritionalValue: Int,
                   var spawnRate: Int) {

  def this(name: String, color: Color, size: Double, maxSpeed: Double, normalSpeed: Double,
           gestationPeriod: Int, starvationThreshold: Int, nutritionalValue: Int) = {
    this(name, color, size, maxSpeed, normalSpeed, gestationPeriod, starvationThreshold, nutritionalValue, 0)
  }

  def getPredators: Set[CreatureType] = CreatureType.predatorMap(this)
  def getPreys: Set[CreatureType] = CreatureType.preyMap(this)
  override def toString: String = name
}
