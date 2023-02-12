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
  *
  * @author Barry Becker
  */
object Creature {
  /** When this close we are considered on top ot the prey */
  private val THRESHOLD_TO_PREY = 0.005
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
    if (hunger < 0.5 * cType.starvationThreshold && age > cType.timeToMaturity)
      numDaysPregnant += 1
    hunger += 1
    if (hunger >= cType.starvationThreshold) {
      //println(" --- " + this.cType.name + " starved to death at age=" + age + " with starveThresh=" + cType.starvationThreshold)
      alive = false
      false
    } else {
      if (numDaysPregnant >= cType.gestationPeriod) { // if very hungary, abort the fetus
        if (hunger > cType.starvationThreshold / 2) numDaysPregnant = 0
        else { // spawn new child.
          spawn = true
          numDaysPregnant = 0
        }
      }
      if (prey.isDefined) {
        moveTowardPreyAndEatIfPossible(prey.get)
        moveToNewLocation(grid)
      }
      else if (speed > 0) {
        val nbrs = new Neighbors(this, grid)
        if (hunger > cType.starvationThreshold / 5 && nbrs.nearestPrey.isDefined && nbrs.nearestPrey.get.isAlive)
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
    speed = if (numDaysPregnant > 0) cType.normalSpeed else cType.maxSpeed
    val distance = Neighbors.distanceTo(getLocation, nearestPrey.getLocation)
    // println(this.getName + " is pursuing " + nearestPrey.getName + " with speed=" + speed + " dist=" + distance)

    if (distance < Creature.THRESHOLD_TO_PREY) {
      eatPrey(nearestPrey)
      direction = randomDirection()
    }
    else {
      direction = Neighbors.getDirectionTo(getLocation, nearestPrey.getLocation)
      if (distance < cType.maxSpeed) speed = distance
      //println(s"pursuing from $getLocation to ${nearestPrey.getLocation} " +
      //  s"\n  with vel=${getVelocity}")
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
    location = computeNewPosition()
    val oldCell = grid.getCellForPosition(location)
    val newCell = grid.getCellForPosition(location)
    if (newCell != oldCell) {
      newCell.addCreature(this)
      oldCell.removeCreature(this)
    }
  }

  /**
    * @param creature the creature we will now eat.
    */
  private def eatPrey(creature: Creature): Unit = {
    println(" *** " + this.cType.name + " ate " + creature.cType.name)
    hunger = Math.max(0, hunger - creature.cType.nutritionalValue)
    creature.kill()
    prey = None
    speed = cType.normalSpeed
  }

  override def toString: String = getName + " hunger=" + hunger + " pregnant=" + numDaysPregnant + " alive=" + alive
}
