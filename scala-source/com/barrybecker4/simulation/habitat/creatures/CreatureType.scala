// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import java.awt._


/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures.
  * Add more creatures like sheep, fox, chickens, ants, vulture,
  * @author Barry Becker
  */
object CreatureType extends Enumeration {

  case class Val(name: String, color: Color, size: Double, normalSpeed: Double, maxSpeed: Double,
                 gestationPeriod: Int, starvationThreshold: Int, nutritionalValue: Int) extends super.Val {
    def getPredators: Set[Val] = predatorMap(this)
    def getPreys: Set[Val] = preyMap(this)
    override def toString: String = name
  }

  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  val GRASS = Val("grass",   new Color(40, 255, 20),   2.0,   0.0,     0.0,    11,    42,     1)
  val WILDEBEEST = Val("cow", new Color(70, 60, 100), 15.0,   0.002,   0.0001, 25,    94,    10)
  val RAT = Val("rat",     new Color(140, 105, 20),    2.0,   0.005,   0.002,   8,    30,     1)
  val CAT = Val("cat",     new Color(0, 195, 220),     5.0,   0.01,    0.004,  15,    58,     4)
  val LION = Val("lion",   new Color(240, 200, 20),    9.0,   0.02,    0.008,  21,    83,     6)

  val VALUES = Array(GRASS, WILDEBEEST, RAT, CAT, LION)

  private val predatorMap = Map(
    GRASS -> Set(WILDEBEEST, RAT),
    WILDEBEEST -> Set(LION),
    RAT -> Set(CAT, LION),
    CAT -> Set(LION),
    LION -> Set[Val]()
  )

  private var preyMap = Map[Val, Set[Val]]()
  for (creature <- VALUES) {
    var preys = Set[Val]()
    for (potentialprey <- VALUES) {
      val preds: Set[Val] = predatorMap(potentialprey)
      if (preds.contains(creature)) preys += potentialprey
    }
    preyMap += (creature -> preys)
  }
}
