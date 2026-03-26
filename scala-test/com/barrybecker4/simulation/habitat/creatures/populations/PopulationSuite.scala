// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType
import com.barrybecker4.simulation.habitat.model.HabitatGrid
import org.scalatest.funsuite.AnyFunSuite

class PopulationSuite extends AnyFunSuite {

  test("removeDead removes dead creatures from grid cells") {
    val grid = HabitatGrid(0.2)
    val pop = Population.createPopulation(CreatureType.RAT, 2)
    for (c <- pop.creatures) {
      grid.getCellForPosition(c.getLocation).addCreature(c)
    }
    val creatures = pop.creatures.toVector
    assert(creatures.size == 2)
    val dead = creatures(0)
    val alive = creatures(1)
    val deadCell = grid.getCellForPosition(dead.getLocation)
    val aliveCell = grid.getCellForPosition(alive.getLocation)

    assert(deadCell.creatures.contains(dead))
    assert(aliveCell.creatures.contains(alive))

    dead.getAttributes.hitPoints = 0
    pop.removeDead(grid)

    assert(pop.creatures.size == 1)
    assert(pop.creatures.contains(alive))
    assert(!deadCell.creatures.contains(dead))
  }
}
