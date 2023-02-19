// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.habitat.creatures.CreatureAttributes.PREGNANCY_HUNGER_THRESH
import com.barrybecker4.simulation.habitat.creatures.CreatureProcessor.DEBUG
import com.barrybecker4.simulation.habitat.model.HabitatGrid

import javax.vecmath.{Point2d, Vector2d}


/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures, but they are all represented by instance of this class.
  *
  * @author Barry Becker
  */
object CreatureAttributes {
  /** when more hungary than this, pregnancy gets aborted */
  private val PREGNANCY_HUNGER_THRESH = 0.8
}

class CreatureAttributes private[creatures](
  var age: Int, var hunger: Int, var numDaysPregnant: Int, var alive: Boolean,
  var speed: Double, var direction: Double, var location: Point2d,
  var prey: Option[Creature] = None) {
  
  def getVelocity = new Vector2d(Math.cos(direction) * speed, Math.sin(direction) * speed)
  
  def adjustAgeAndHunger(cType: CreatureType): Unit = {
    age += 1

    if (age > cType.timeToMaturity)
      numDaysPregnant += 1
    if (hunger > PREGNANCY_HUNGER_THRESH * cType.starvationThreshold) {
      numDaysPregnant = 0 // stillborn
    }
    hunger += 1
  }
  
  def computeNewPosition() = {
    val vel = getVelocity
    new Point2d(absMod(location.x + vel.x), absMod(location.y + vel.y))
  }

  /**
    * Flock with nbrs
    * Move toward the center of mass of neighbors and turn in same direction as nearest friend.
    * @param friends nearby friends
    */
  def flock(cType: CreatureType, friends: List[Creature], nearestFriend: Option[Creature]): Unit = {
    val perturbedDirection = perturbDirection(direction)
    if (nearestFriend.isEmpty || cType.flockTendancy == 0) {
      direction = perturbedDirection
    }
    else {
      val centerOfMass = new Point2d(0, 0)
      for (c <- friends) {
        centerOfMass.add(c.getLocation)
      }
      centerOfMass.scale(1.0 / friends.size)
      val directionToCOM = MathUtil.getDirectionTo(location, centerOfMass)
      val flockDirection = (nearestFriend.get.getDirection + directionToCOM) / 2.0
      direction = cType.flockTendancy * flockDirection + (1.0 - cType.flockTendancy) * perturbedDirection
    }
  }
  
  def eatPrey(thePrey: Creature, normalSpeed: Double): Unit = {
    hunger = Math.max(0, hunger - thePrey.cType.nutritionalValue)
    thePrey.kill()
    prey = None
    speed = normalSpeed
  }
}
