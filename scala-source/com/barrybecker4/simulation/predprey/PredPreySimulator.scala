// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey

import com.barrybecker4.math.function.CountFunction
import com.barrybecker4.math.function.Function
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import com.barrybecker4.simulation.predprey.creatures.Foxes
import com.barrybecker4.simulation.predprey.creatures.Population
import com.barrybecker4.simulation.predprey.creatures.Rabbits
import com.barrybecker4.simulation.predprey.options.DynamicOptions
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer

import javax.swing.JPanel
import java.awt.Color
import java.awt.Graphics

import scala.compiletime.uninitialized


class PredPreySimulator() extends Simulator("Predator Prey Simulation") {

  private var foxes = new Foxes
  private var rabbits = new Rabbits

  private var graph: MultipleFunctionRenderer = uninitialized
  private var iteration = 0L
  private var rabbitFunction: CountFunction = uninitialized
  private var foxFunction: CountFunction = uninitialized
  private var options: Option[DynamicOptions] = None
  initGraph()

  def getCreatures: Seq[Population] = Seq(foxes, rabbits)

  override protected def reset(): Unit = {
    initGraph()
    options.foreach(_.reset())
  }

  override protected def getInitialTimeStep = 1.0

  override def timeStep: Double = {
    iteration += 1
    stepPopulations()
    recordGraphAndUi()
    tStep
  }

  private def stepPopulations(): Unit =
    val f = foxes.getPopulation
    val r = rabbits.getPopulation
    foxes.setPopulation(
      PredPreyDynamics.nextFoxCount(f, r, foxes.birthRate, foxes.deathRate)
    )
    rabbits.setPopulation(
      PredPreyDynamics.nextRabbitCount(r, foxes.getPopulation, rabbits.birthRate, rabbits.deathRate)
    )

  private def recordGraphAndUi(): Unit =
    rabbitFunction.addValue(iteration.toDouble, rabbits.getPopulation.toDouble)
    foxFunction.addValue(iteration.toDouble, foxes.getPopulation.toDouble)
    options.foreach(_.update())

  private def initGraph(): Unit = {
    iteration = 0
    rabbits.reset()
    foxes.reset()
    rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS)
    foxFunction = new CountFunction(Foxes.INITIAL_NUM_FOXES)
    val functions = List[Function](rabbitFunction, foxFunction)
    val lineColors = List[Color](Rabbits.COLOR, Foxes.COLOR)
    graph = new MultipleFunctionRenderer(functions, Some(lineColors))
  }

  override def createDynamicControls: JPanel = {
    val panel = new DynamicOptions(this)
    options = Some(panel)
    panel
  }

  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)

  override def paint(g: Graphics): Unit = {
    graph.setSize(getWidth, getHeight)
    graph.paint(g)
  }
}
