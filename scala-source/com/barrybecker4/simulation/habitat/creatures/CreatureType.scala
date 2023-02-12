// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
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

  val GRASS = new CreatureType("grass", new Color(90, 160, 0),   6.0,   0.0,     0.0,    11, 3,    44,     60)
  val COW = new CreatureType("cow", new Color(140, 60, 170), 18.0,   0.008,   0.001, 21,  26,   93,    90, 0.5)
  val RAT = new CreatureType("rat", new Color(150, 115, 40),    2.0,   0.011,   0.002,   5, 8,   28,     10, 1.0)
  val CAT = new CreatureType("cat", new Color(200, 105, 20),     5.0,   0.03,    0.01,  10,  18,  58,     40, 0.2)
  val LION = new CreatureType("lion", new Color(210, 210, 0),    9.0,   0.05,    0.01,  20, 27,   74,     60, 0.4)

  val VALUES = Array(GRASS, COW, RAT, CAT, LION)

  val preyToPredatorsMap: Map[CreatureType, Set[CreatureType]] = Map(
    GRASS -> Set(COW, RAT),
    COW -> Set(LION),
    RAT -> Set(CAT, LION),
    CAT -> Set(LION),
    LION -> Set[CreatureType]() // king of the jungle, top of the food chain
  )

  var predatorToPreysMap: Map[CreatureType, Set[CreatureType]] = Map[CreatureType, Set[CreatureType]]()
  for (creature <- VALUES) {
    var preys = Set[CreatureType]()
    for (potentialPrey <- VALUES) {
      val preds: Set[CreatureType] = preyToPredatorsMap(potentialPrey)
      if (preds.contains(creature)) preys += potentialPrey
    }
    predatorToPreysMap += (creature -> preys)
  }
}

class CreatureType(val name: String, val color: Color,
                   var size: Double, var maxSpeed: Double, var normalSpeed: Double,
                   var gestationPeriod: Int, var timeToMaturity: Int,
                   var starvationThreshold: Int, var nutritionalValue: Int,
                   var flockTendancy: Double = 0, var spawnRate: Int = 0) {

  def getPreyToPredators: Set[CreatureType] = CreatureType.preyToPredatorsMap(this)
  def getPreys: Set[CreatureType] = CreatureType.predatorToPreysMap(this)
  override def toString: String = name
}
