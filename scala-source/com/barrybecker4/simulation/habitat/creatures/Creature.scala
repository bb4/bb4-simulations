// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

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
  private val THRESHOLD_TO_PREY = 0.001
}

class Creature private[creatures](var cType: CreatureType, var location: Point2d) {

  private var numDaysPregnant = 0

  /** if becomes too large, then starve */
  private var hunger  = 0
  private var direction = randomDirection()
  private var speed = cType.normalSpeed
  private var alive = true
  private var pursuing = false // chasing prey

  /** not mature (capable of having children) until eaten at least once (and can eat). */
  private var mature = speed == 0
  private def randomDirection() = 2.0 * Math.PI * MathUtil.RANDOM.nextDouble()
  def getVelocity = new Vector2d(Math.sin(direction) * speed, Math.cos(direction) * speed)
  def kill(): Unit = { alive = false}
  def isAlive: Boolean = alive
  def isPursuing: Boolean = pursuing
  def getName: String = cType.name
  def getType: CreatureType = cType
  def getLocation: Point2d = location
  def getDirection: Double = direction
  def getSize: Double = cType.size

  private def computeNewPosition = {
    val vel = getVelocity
    new Point2d(absMod(location.x + vel.x), absMod(location.y + vel.y))
  }

  private def absMod(value: Double) = {
    val newValue = value % 1.0
    if (newValue < 0) 1 - newValue
    else newValue
  }

  /** @return true if new child spawned*/
  def nextDay(grid: HabitatGrid): Boolean = {
    var spawn = false
    numDaysPregnant += 1
    hunger += 1
    if (hunger >= cType.starvationThreshold) alive = false
    if (numDaysPregnant >= cType.gestationPeriod) { // if very hungary, abort the fetus
      if (hunger > cType.starvationThreshold / 2) numDaysPregnant = 0
      else { // spawn new child.
        spawn = true
        numDaysPregnant = 0
      }
    }
    if (speed > 0) {
      val nbrs = new Neighbors(this, grid)
      if (nbrs.nearestPrey != null && nbrs.nearestPrey.isAlive) moveTowardPreyAndEatIfPossible(nbrs.nearestPrey)
      else flock(nbrs.flockFriends, nbrs.nearestFriend)
      moveToNewLocation(grid)
    }
    // else move toward friends and swarm
    spawn
  }

  private def moveTowardPreyAndEatIfPossible(nearestPrey: Creature): Unit = {
    pursuing = true
    speed = cType.maxSpeed
    val distance = nearestPrey.getLocation.distance(location)
    if (distance < Creature.THRESHOLD_TO_PREY) { //println( this + " about to eat " + nearestPrey);
      eat(nearestPrey)
      direction = randomDirection()
    }
    else {
      direction = MathUtil.getDirectionTo(getLocation, nearestPrey.getLocation)
      if (distance < cType.maxSpeed) speed = distance
    }
  }

  /**
    * Flock with nbrs
    * Move toward the center of mass of neighbors and turn in same direction as nearest friend.
    * @param friends nearby friends
    */
  private def flock(friends: List[Creature], nearestFriend: Creature): Unit = {
    if (nearestFriend == null) return
    val centerOfMass = new Point2d(0, 0)
    for (c <- friends) {
      centerOfMass.add(c.getLocation)
    }
    centerOfMass.scale(1.0 / friends.size)
    val directionToCOM = MathUtil.getDirectionTo(getLocation, centerOfMass)
    direction = (nearestFriend.direction + directionToCOM) / 2.0
  }

  private def moveToNewLocation(grid: HabitatGrid): Unit = {
    location = computeNewPosition
    val oldCell = grid.getCellForPosition(location)
    val newCell = grid.getCellForPosition(location)
    if (newCell ne oldCell) {
      newCell.addCreature(this)
      oldCell.removeCreature(this)
    }
  }

  /**
    * @param creature the creature we will now eat.
    */
  private def eat(creature: Creature): Unit = {
    hunger -= creature.cType.nutritionalValue
    creature.kill()
    hunger = Math.max(0, hunger)
    pursuing = false
    speed = cType.normalSpeed
    mature = true
  }

  override def toString: String = getName + " hunger=" + hunger + " pregnant=" + numDaysPregnant + " alive=" + alive
}
