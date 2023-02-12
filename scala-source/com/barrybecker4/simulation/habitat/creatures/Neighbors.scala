// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.simulation.habitat.creatures.Neighbors.distanceTo
import com.barrybecker4.simulation.habitat.model.HabitatGrid

import javax.vecmath.Point2d


/**
  * Neigoring creatures.
  */
object Neighbors {
  /** only pursue prey that is this close to us */
  private val SMELL_NEIGHBOR_DISTANCE = 0.2
  private val DOUBLE_SMELL_DISTANCE = 1.5 * SMELL_NEIGHBOR_DISTANCE

  def distanceTo(loc1: Point2d, loc2: Point2d): Double = {
    val rawDist = getRawDist(loc1, loc2)
    Math.sqrt(rawDist.x * rawDist.x + rawDist.y * rawDist.y)
  }

  def getDirectionTo(loc1: Point2d, loc2: Point2d): Double = {
    val rawDist = getRawDist(loc1, loc2)
    Math.atan2(rawDist.y, rawDist.x)
  }

  private def getRawDist(loc1: Point2d, loc2: Point2d): Point2d = {
    val rawXDist = Math.abs(loc1.x - loc2.x)
    val rawYDist = Math.abs(loc1.y - loc2.y)
    new Point2d(
      if (rawXDist > DOUBLE_SMELL_DISTANCE) 1.0 - rawXDist else rawXDist,
      if (rawYDist > DOUBLE_SMELL_DISTANCE) 1.0 - rawYDist else rawYDist
    )
  }
}

class Neighbors private[creatures](var creature: Creature, var grid: HabitatGrid) {

  private[creatures] var flockFriends = List[Creature]()
  private[creatures] var nearestPrey: Option[Creature] = None
  private[creatures] var nearestFriend: Option[Creature] = None
  findNeighbors()


  private def findNeighbors(): Unit = {
    val cType = creature.getType
    nearestPrey = None
    nearestFriend = None
    var nearestPreyDistance = Double.MaxValue
    var nearestFriendDistance = Double.MaxValue
    val cells = grid.getNeighborCells(grid.getCellForPosition(creature.getLocation))

    for (cell <- cells) {
      for (nearbyCreature <- cell.creatures) {
        if (nearbyCreature != creature) {
          val dist = distanceTo(nearbyCreature.getLocation, creature.getLocation)
          if (dist < Neighbors.SMELL_NEIGHBOR_DISTANCE) if (nearbyCreature.getType eq cType) {
            flockFriends +:= nearbyCreature
            if (dist < nearestFriendDistance) {
              nearestFriendDistance = dist
              nearestFriend = Some(nearbyCreature)
            }
          }
          else if (cType.getPreys.contains(nearbyCreature.getType)) if (dist < nearestPreyDistance) {
            nearestPreyDistance = dist
            nearestPrey = Some(nearbyCreature)
          }
        }
      }
    }
  }
}
