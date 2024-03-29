// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import java.awt.Color
import com.barrybecker4.simulation.habitat.creatures.CreatureType._


/**
  * Everything we need to know about all the different sorts of creatures.
  * Add more creatures like sheep, fox, chickens, ants, vulture, etc.
  * This is probably the best pattern to follow for enums with properties and methods.
  * @author Barry Becker
  */
object CreatureType  {

  val GRASS = new CreatureType("grass", new Color(120, 180, 10), 6.0,
    0.0,    0.0,    15, 5,  280,
    47,     70, 0)
  val COW = new CreatureType("cow", new Color(140, 140, 240),    20.0,
    0.007,  0.002,  39,  40,  390,
    67,     110, 2,  0.5)
  val RAT = new CreatureType("rat", new Color(150, 125, 100),    3.0,
    0.007,  0.002,   14, 11, 120,
    27,     12, 1, 0.7)
  val CAT = new CreatureType("cat", new Color(190, 135, 70),     6.0,
    0.024,  0.005,  21,  30,  310,
    33,     25, 2,0.2)
  val LION = new CreatureType("lion", new Color(180, 160, 30),   11.0,
    0.03, 0.01,  29, 40,  330,
    36,     50, 4,0.3)

  val VALUES = Array(GRASS, COW, RAT, CAT, LION)

  private val preyToPredatorsMap: Map[CreatureType, Set[CreatureType]] = Map(
    GRASS -> Set(COW, RAT),
    COW -> Set(LION),
    RAT -> Set(CAT, LION),
    CAT -> Set(LION),
    LION -> Set[CreatureType]() // king of the jungle, top of the food chain
  )

  private var predatorToPreysMap: Map[CreatureType, Set[CreatureType]] = Map[CreatureType, Set[CreatureType]]()
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
                   var gestationPeriod: Int, var timeToMaturity: Int, var maxAge: Int,
                   var starvationThreshold: Int, var nutritionalValue: Int, var eatRate: Int,
                   var flockTendancy: Double = 0, var spawnRate: Int = 0) {

  def getPreys: Set[CreatureType] = CreatureType.predatorToPreysMap(this)
  override def toString: String = name
}
