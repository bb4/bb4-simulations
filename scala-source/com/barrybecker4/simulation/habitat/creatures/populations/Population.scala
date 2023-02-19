// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.habitat.creatures.{Creature, CreatureType, absMod}
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
  def reset(): Unit = {create()}

  private def create(): Unit = {
    creatures = Set[Creature]()
    for (i <- 0 until initialSize)
      val pos = new Point2d(MathUtil.RANDOM.nextDouble(), MathUtil.RANDOM.nextDouble())
      creatures += new Creature(creatureType, pos)
  }

  /**
    * Increment to the next day.
    * Move the creatures around and see if they are close to something to eat.
    * Have children if gestation period is complete and they have spawned.
    * @param grid the habitat grid
    */
  def nextDay(grid: HabitatGrid): Unit = {

    val spawnLocations = creaturesLiveForTheDay(grid)

    for (newLocation <- spawnLocations) {
      val newCreature = new Creature(creatureType, newLocation)
      creatures += newCreature
      grid.getCellForPosition(newLocation).addCreature(newCreature)
    }
  }

  /** Figure out if anything edible nearby.
    * Eat prey if there are things that we eat nearby.
    * @return offspring at spawn locations
    */
  private def creaturesLiveForTheDay(grid: HabitatGrid): List[Point2d] = {

    var spawnLocations = calculateNaturalSpawnLocations(grid)

    val spawnRate = creatureType.spawnRate
    if (spawnRate > 0) {
      for (i <- 0 until spawnRate) {
        val loc = new Point2d(
          absMod(MathUtil.RANDOM.nextDouble() * grid.getXDim),
          absMod(MathUtil.RANDOM.nextDouble() * grid.getYDim)
        )
        spawnLocations +:= loc
      }
    }
    else if (spawnRate < 0) {
      val numToKill = -spawnRate
      val numToRetain = Math.max(spawnLocations.size - numToKill, 0)
      val numToStillKill = numToKill - (spawnLocations.size - numToRetain)
      spawnLocations = spawnLocations.take(numToRetain)
      creatures = creatures.drop(numToStillKill)
    }

    spawnLocations
  }

  private def calculateNaturalSpawnLocations(grid: HabitatGrid): List[Point2d] = {
    var spawnLocations = List[Point2d]()
    for (creature <- creatures) {
      val spawn = creature.nextDay(grid)
      if (spawn) {
        val loc = creature.getLocation
        spawnLocations +:= new Point2d(
          absMod(loc.x + Population.SPAWN_RADIUS * MathUtil.RANDOM.nextDouble()),
          absMod(loc.y + Population.SPAWN_RADIUS * MathUtil.RANDOM.nextDouble())
        )
      }
    }
    spawnLocations
  }

  /** Remove dead after next day is done. */
  private[creatures] def removeDead(): Unit =
    val origAlive: Int = creatures.size
    creatures = creatures.filter(_.isAlive)
    if (creatures.size > origAlive)
      println("starved: " + (origAlive - creatures.size) + " " + creatureType.name)

  def getSize: Int = creatures.size
  def getName: String = "Population of " + creatureType.name
}
