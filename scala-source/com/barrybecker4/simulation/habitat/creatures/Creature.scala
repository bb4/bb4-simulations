// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.habitat.creatures.Creature._
import com.barrybecker4.simulation.habitat.model.HabitatGrid

import javax.vecmath.Point2d
import javax.vecmath.Vector2d



/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures, but they are all represented by instance of this class.
  *
  * @author Barry Becker
  */
object Creature {
  /** When this close we are considered on top ot the prey */
  private val THRESHOLD_TO_PREY = 0.002

  /** when more hungary than this, pregnancy gets aborted  */
  private val PREGNANCY_HUNGER_THRESH = 0.8

  /** If not at least this hungary, then won't chase prey */
  private val HUNGRY_THRESH = 0.2

  private val DEBUG = false
}

class Creature private[creatures](var cType: CreatureType, var location: Point2d) {

  private var numDaysPregnant = 0

  /** if becomes too large, then starve */
  private var hunger = 0
  private var direction = randomDirection()
  private var speed = cType.normalSpeed
  private var alive = true
  private var prey: Option[Creature] = None

  private var age = 0
  def getVelocity = new Vector2d(Math.cos(direction) * speed, Math.sin(direction) * speed)
  def kill(): Unit = { alive = false }
  def isAlive: Boolean = alive
  def isPursuing: Boolean = prey.isDefined
  def getName: String = cType.name
  def getType: CreatureType = cType
  def getLocation: Point2d = location
  def getDirection: Double = direction
  def getSize: Double = cType.size
  def getPrey: Option[Creature] = prey
  def getHealth: Int = cType.starvationThreshold - hunger

  private def computeNewPosition() = {
    val vel = getVelocity
    new Point2d(absMod(location.x + vel.x), absMod(location.y + vel.y))
  }

  /** @return true if new child spawned*/
  def nextDay(grid: HabitatGrid): Boolean = {
    var spawn = false
    age += 1
    if (age > cType.timeToMaturity)
      numDaysPregnant += 1
    if (hunger > PREGNANCY_HUNGER_THRESH * cType.starvationThreshold) {
      numDaysPregnant = 0 // stillborn
    }
    hunger += 1
    if (hunger >= cType.starvationThreshold || age > cType.maxAge) {
      if (DEBUG) {
        if (age > cType.maxAge)
          println(nameAndId + " died of old age at age=" + age + " with starveThresh=" + cType.starvationThreshold)
        else
          println(nameAndId + " starved to death at age=" + age + " with starveThresh=" + cType.starvationThreshold)
      }
      alive = false
      false
    } else {
      if (numDaysPregnant >= cType.gestationPeriod) {
        // spawn new child.
        spawn = true
        numDaysPregnant = 0
        if (DEBUG)
          println(s"new $nameAndId born")
      }
      if (prey.isDefined) {
        moveTowardPreyAndEatIfPossible(prey.get)
        moveToNewLocation(grid)
      }
      else if (speed > 0) {
        val nbrs = new Neighbors(this, grid)
        if (hunger > HUNGRY_THRESH * cType.starvationThreshold && nbrs.nearestPrey.isDefined && nbrs.nearestPrey.get.isAlive)
          prey = nbrs.nearestPrey
          moveTowardPreyAndEatIfPossible(prey.get)
        else
          flock(nbrs.flockFriends, nbrs.nearestFriend) // move toward friends and swarm
        moveToNewLocation(grid)
      }
      spawn
    }
  }

  private def moveTowardPreyAndEatIfPossible(nearestPrey: Creature): Unit = {
    // Can't run as fast when pregnant
    speed = if (numDaysPregnant > cType.gestationPeriod / 2)
      cType.normalSpeed else cType.maxSpeed
    val distance = Neighbors.distanceTo(getLocation, nearestPrey.getLocation)

    if (distance < THRESHOLD_TO_PREY) {
      eatPrey(nearestPrey)
      direction = randomDirection()
    }
    else {
      direction = Neighbors.getDirectionTo(getLocation, nearestPrey.getLocation)
      if (distance < cType.maxSpeed) speed = distance
      if (DEBUG)
        println(s"$nameAndId pursuing from $getLocation to ${nearestPrey.getLocation} " +
          s"\n  with vel=${getVelocity}")
    }
  }

  /**
    * Flock with nbrs
    * Move toward the center of mass of neighbors and turn in same direction as nearest friend.
    * @param friends nearby friends
    */
  private def flock(friends: List[Creature], nearestFriend: Option[Creature]): Unit = {
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
      val directionToCOM = MathUtil.getDirectionTo(getLocation, centerOfMass)
      val flockDirection = (nearestFriend.get.direction + directionToCOM) / 2.0
      direction = cType.flockTendancy * flockDirection + (1.0 - cType.flockTendancy) * perturbedDirection
    }
  }

  private def moveToNewLocation(grid: HabitatGrid): Unit = {
    val oldLocation = location
    location = computeNewPosition()
    grid.move(oldLocation, location, this)
  }

  /**
    * @param creature the creature we will now eat.
    */
  private def eatPrey(creature: Creature): Unit = {
    if (DEBUG)
      println(" *** " + nameAndId + " ate " + creature.cType.name)
    hunger = Math.max(0, hunger - creature.cType.nutritionalValue)
    creature.kill()
    prey = None
    speed = cType.normalSpeed
  }

  private def nameAndId: String = s"$getName ${Integer.toHexString(hashCode())}"
  override def toString: String =
    s"$getName hunger=$hunger pregnant=$numDaysPregnant alive=$alive"
}
