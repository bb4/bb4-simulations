// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.habitat.model.HabitatGrid
import javax.vecmath.Point2d


/** Everything we need to know about a population of a certain kind of creature. */
object Population {
  private val SPAWN_RADIUS = 0.05

  /** Factory method to create an initial population of randomly distributed members. */
  private[creatures] def createPopulation(creatureType: CreatureType, initialSize: Int) = {
    val pop = new Population(creatureType)
    pop.createInitialSet(initialSize)
    pop
  }
}

class Population(var creatureType: CreatureType) {
  var creatures: Set[Creature] = Set[Creature]()
  private var initialSize = 0

  private def createInitialSet(num: Int): Unit = {
    this.initialSize = num
    create()
  }

  /** Reset the population to its original size */
  def reset() {create()}

  private def create(): Unit = {
    creatures = Set[Creature]()
    for (i <- 0 until initialSize)
      creatures += new Creature(creatureType, new Point2d(MathUtil.RANDOM.nextDouble, MathUtil.RANDOM.nextDouble))
  }

  /**
    * Increment to the next day.
    * Move the creatures around and see if they are close to something to eat.
    * Have children if gestation period is complete and they have spawned.
    * @param grid the habitat grid
    */
  def nextDay(grid: HabitatGrid): Unit = {
    var spawnLocations = List[Point2d]()
    // Figure out if anything edible nearby.
    // Eat prey if there are things that we eat nearby.
    for (creature <- creatures) {
      val spawn = creature.nextDay(grid)
      if (spawn) {
        val loc = creature.getLocation
        spawnLocations +:= new Point2d(
          absMod(loc.x + Population.SPAWN_RADIUS * MathUtil.RANDOM.nextDouble),
          absMod(loc.y + Population.SPAWN_RADIUS * MathUtil.RANDOM.nextDouble))
      }
    }

    for (newLocation <- spawnLocations) {
      val newCrtr = new Creature(creatureType, newLocation)
      creatures += newCrtr
      grid.getCellForPosition(newLocation).addCreature(newCrtr)
    }
  }

  /** Remove dead after next day is done. */
  private[creatures] def removeDead(grid: HabitatGrid): Unit = creatures = creatures.filter(_.isAlive)

  def getSize: Int = creatures.size
  def getName: String = "Population of " + creatureType.name
  private def absMod(value: Double) = Math.abs(value % 1.0)
}
