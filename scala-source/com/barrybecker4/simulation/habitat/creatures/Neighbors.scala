// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.simulation.habitat.model.HabitatGrid


/**
  * Everything we need to know about a creature.
  * There are many different sorts of creatures, but they are all represented by instance of this class.
  * @author Barry Becker
  */
object Neighbors {
  /** only pursue prey that is this close to us */
  private val SMELL_NEIGHBOR_DISTANCE = 0.1
}

class Neighbors private[creatures](var creature: Creature, var grid: HabitatGrid) {

  private[creatures] var flockFriends = List[Creature]()
  private[creatures] var nearestPrey: Creature = _
  private[creatures] var nearestFriend: Creature = _
  findNeighbors()


  private def findNeighbors() = {
    val cType = creature.getType
    nearestPrey = null
    nearestFriend = null
    var nearestPreyDistance = Double.MaxValue
    var nearestFriendDistance = Double.MaxValue
    val cells = grid.getNeighborCells(grid.getCellForPosition(creature.getLocation))

    for (cell <- cells) {

      for (nearbyCreature <- cell.creatures) {
        val dist = nearbyCreature.getLocation.distance(creature.getLocation)
        if (dist < Neighbors.SMELL_NEIGHBOR_DISTANCE) if (nearbyCreature.getType eq cType) {
          flockFriends +:= nearbyCreature
          if (dist < nearestFriendDistance) {
            nearestFriendDistance = dist
            nearestFriend = nearbyCreature
          }
        }
        else if (cType.getPreys.contains(nearbyCreature.getType)) if (dist < nearestPreyDistance) {
          nearestPreyDistance = dist
          nearestPrey = nearbyCreature
        }
      }
    }
  }
}
