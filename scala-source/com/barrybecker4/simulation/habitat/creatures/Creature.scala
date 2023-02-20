// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.habitat.model.HabitatGrid

import javax.vecmath.Point2d
import javax.vecmath.Vector2d


/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures, but they are all represented by instance of this class.
  */
class Creature private[creatures](var cType: CreatureType, var location: Point2d) {

  private val attributes =
    new CreatureAttributes(0, 0, 0,
      cType.normalSpeed, randomDirection(), location, cType.nutritionalValue, false, false, None)

  def kill(): Unit = { attributes.isBeingEaten = true }
  def isAlive: Boolean = attributes.hitPoints > 0
  def isPursuing: Boolean = attributes.prey.isDefined
  def isBeingEaten: Boolean = attributes.isBeingEaten
  def getName: String = cType.name
  def getType: CreatureType = cType
  def getLocation: Point2d = attributes.location
  def getDirection: Double = attributes.direction
  def getSize: Double = cType.size
  def getPrey: Option[Creature] = attributes.prey
  def getHealth: Int = cType.starvationThreshold - attributes.hunger
  def getVelocity: Vector2d = attributes.getVelocity
  def getAttributes: CreatureAttributes = attributes
  private val processor = CreatureProcessor(this)


  /** @return true if new child spawned*/
  def nextDay(grid: HabitatGrid): Boolean = processor.nextDay(grid)

  def nameAndId: String = s"$getName ${Integer.toHexString(hashCode())}"

  override def toString: String =
    s"$getName hunger=${attributes.hunger} pregnant=${attributes.numDaysPregnant} hitPoints=${attributes.hitPoints}"
}
