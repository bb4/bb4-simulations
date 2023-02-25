// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.math.function.{CountFunction, Function}
import com.barrybecker4.simulation.habitat.creatures.Neighbors
import com.barrybecker4.simulation.habitat.creatures.populations.Population
import com.barrybecker4.simulation.habitat.model.HabitatGrid
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer

import java.awt.Color
import scala.collection.mutable.ArrayBuffer

object Habitat {
  val HABITATS: Array[Habitat] = Array(new SerengetiHabitat, new CatRatHabitat, new SingleCreatureHabitat)
  val DEFAULT_HABITAT_INDEX = 0
}

/**
  * A habitat is a collection of populations for all the creatures in it.
  * @author Barry Becker
  */
abstract class Habitat extends ArrayBuffer[Population] {

  private var functionMap = Map[Population, CountFunction]()
  private var dayCount: Int = 0
  /** associate population with function */
  private var grid: HabitatGrid = _
  initialize()

  def getName: String

  def reset(): Unit = {
    grid = createHabitatGrid()
    for (pop <- this) pop.reset()
    updateGridCellCounts()
  }

  protected def addPopulations(): Unit

  def nextDay(): Unit = {
    for (population <- this) population.nextDay(grid)

    // remove any creatures that might have died by old age, starvation or by being eaten.
    for (pop <- this) pop.removeDead(grid)

    updateFunctions(dayCount)
    
    dayCount += 1
  }

  def createFunctionRenderer: MultipleFunctionRenderer = {
    var functions = List[Function]()
    var lineColors: List[Color] = List[Color]()

    for (pop <- this) {
      val func = new CountFunction(pop.getSize)
      functions +:= func
      lineColors +:= pop.creatureType.color
      functionMap += pop -> func
    }
    val funcRenderer = new MultipleFunctionRenderer(functions, Some(lineColors))
    funcRenderer.setRightNormalizePercent(40)
    funcRenderer
  }

  private def initialize(): Unit = {
    grid = createHabitatGrid()
    this.clear()
    addPopulations()
    updateGridCellCounts()
  }

  private def updateFunctions(iteration: Long): Unit = {
    for (pop <- this) {
      val func = functionMap(pop)
      func.addValue(iteration.toDouble, pop.getSize.toDouble)
    }
  }

  private def updateGridCellCounts(): Unit = {
    for (pop <- this) {
      for (c <- pop.creatures) {
        val cell = grid.getCellForPosition(c.getLocation)
        cell.addCreature(c)
      }
    }
  }
  
  private def createHabitatGrid(): HabitatGrid = {
    HabitatGrid(Neighbors.SMELL_NEIGHBOR_DISTANCE)
  }
}
