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
  val SMELL_NEIGHBOR_DISTANCE = 0.2

  def distanceTo(loc1: Point2d, loc2: Point2d): Double = {
    val rawDist = getRawVector(loc1, loc2)
    Math.sqrt(rawDist.x * rawDist.x + rawDist.y * rawDist.y)
  }

  def getDirectionTo(loc1: Point2d, loc2: Point2d): Double = {
    val rawDist = getRawVector(loc1, loc2)
    Math.atan2(rawDist.y, rawDist.x)
  }

  private def getRawVector(loc1: Point2d, loc2: Point2d): Point2d = {
    val rawXDist = absMod(loc2.x) - absMod(loc1.x)
    val rawYDist = absMod(loc2.y) - absMod(loc1.y)
    new Point2d(modDist(rawXDist), modDist(rawYDist))
  }

  private def modDist(rawDist: Double): Double = {
    if (Math.abs(rawDist) > 0.5)
      if (rawDist > 0) 1.0 - rawDist else 1.0 + rawDist
    else rawDist
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
    val creatureLoc = creature.getLocation

    for (cell <- cells) {
      for (nearbyCreature <- cell.creatures) {
        if (nearbyCreature != creature) {
          val dist = distanceTo(creatureLoc, nearbyCreature.getLocation)
          if (dist < Neighbors.SMELL_NEIGHBOR_DISTANCE) {
            if (nearbyCreature.getType eq cType) {
              flockFriends +:= nearbyCreature
              if (dist < nearestFriendDistance) {
                nearestFriendDistance = dist
                nearestFriend = Some(nearbyCreature)
              }
            }
            else if (cType.getPreys.contains(nearbyCreature.getType)) {
              if (dist < nearestPreyDistance) {
                nearestPreyDistance = dist
                nearestPrey = Some(nearbyCreature)
              }
            }
          }
        }
      }
    }
  }
}
