// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.habitat.creatures.CreatureProcessor.*
import com.barrybecker4.simulation.habitat.model.HabitatGrid
import javax.vecmath.{Point2d, Vector2d}


/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures, but they are all represented by instance of this class.
  */
object CreatureProcessor {
  /** When this close we are considered on top ot the prey */
  private val THRESHOLD_TO_PREY = 0.002
  /** If not at least this hungary, then won't chase prey */
  private val HUNGRY_THRESH = 0.2
  private val DEBUG = false
}

case class CreatureProcessor(creature: Creature) {

  /** @return true if new child spawned */
  def nextDay(grid: HabitatGrid): Boolean = {
    val a = creature.getAttributes
    val cType = creature.cType
    var spawn = false
    a.adjustAgeAndHunger(cType)

    if (a.hunger >= cType.starvationThreshold || a.age > cType.maxAge || a.hitPoints <= 0) {
      if (DEBUG) {
        val cause =
          if (a.hitPoints <= 0) "was eaten"
          else if (a.age > cType.maxAge) "died of old age"
          else "starved to death"
        println(s"${creature.nameAndId} $cause at age=${a.age} with starveThresh=${cType.starvationThreshold}")
      }
      a.hitPoints = 0
    } else {
      if (a.numDaysPregnant >= cType.gestationPeriod && !a.isBeingEaten) {
        // spawn new child.
        spawn = true
        a.numDaysPregnant = 0
        if (DEBUG)
          println(s"new ${creature.nameAndId} born")
      }
      doMovement(grid)
    }
    spawn
  }

  private def doMovement(grid: HabitatGrid): Unit = {
    val a = creature.getAttributes
    val cType = creature.cType
    if (a.isEating) {
      if (a.prey.isEmpty) {
        a.isEating = false
      }
      else {
        a.prey.get.getAttributes.hitPoints -= cType.eatRate
        a.hitPoints = cType.nutritionalValue
        a.hunger = Math.max(0, a.hunger - cType.eatRate)
        if (a.isEating && a.hunger == 0)
          a.doneEating(cType.normalSpeed)
      }
    }

    if (a.prey.isDefined && !a.isBeingEaten) {
      moveTowardPreyAndEatIfPossible(a.prey.get)
      moveToNewLocation(grid)
    }
    else if (a.pursuedBy.isDefined && !a.isBeingEaten) {
      attemptToFlee(grid)
    }
    else if (a.speed > 0 && !a.isBeingEaten) {
      val nbrs = new Neighbors(creature, grid)
      if (a.hunger > HUNGRY_THRESH * cType.starvationThreshold && nbrs.nearestPrey.isDefined)
        a.prey = nbrs.nearestPrey
        moveTowardPreyAndEatIfPossible(a.prey.get)
      else
        a.flock(cType, nbrs.flockFriends, nbrs.nearestFriend) // move toward friends and swarm
      moveToNewLocation(grid)
    }
  }

  private def attemptToFlee(grid: HabitatGrid): Unit = {
    val a = creature.getAttributes
    val cType = creature.cType
    val predator = a.pursuedBy.get
    a.direction = predator.getDirection
    a.speed = cType.maxSpeed
    moveToNewLocation(grid)
  }

  private def moveTowardPreyAndEatIfPossible(nearestPrey: Creature): Unit = {
    val a = creature.getAttributes
    val cType = creature.cType
    // Can't run as fast when late-term pregnant
    a.speed = if (a.numDaysPregnant > cType.gestationPeriod / 2)
      cType.normalSpeed else cType.maxSpeed
    val distance = Neighbors.distanceTo(a.location, nearestPrey.getLocation)

    if (distance < THRESHOLD_TO_PREY) {
      eatPrey(nearestPrey)
    }
    else {
      a.direction = Neighbors.getDirectionTo(a.location, nearestPrey.getLocation)
      nearestPrey.getAttributes.pursuedBy = Some(creature)
      if (distance < cType.maxSpeed) a.speed = distance
      if (DEBUG)
        println(s"${creature.nameAndId} pursuing ${nearestPrey.cType.name} from ${a.location} to ${nearestPrey.getLocation} " +
          s"\n  with vel=${a.getVelocity}")
    }
  }

  private def moveToNewLocation(grid: HabitatGrid): Unit = {
    val a = creature.getAttributes
    val oldLocation = a.location
    a.location = a.computeNewPosition()
    grid.move(oldLocation, a.location, creature)
  }

  /** @param thePrey the creature we will now eat.
    */
  private def eatPrey(thePrey: Creature): Unit = {
    if (DEBUG)
      println(" *** " + creature.nameAndId + " ate " + thePrey.cType.name)
    creature.getAttributes.eatPrey(thePrey, creature.cType.eatRate)
  }
}
