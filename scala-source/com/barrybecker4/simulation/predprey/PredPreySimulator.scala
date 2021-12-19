// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey

import com.barrybecker4.math.function.CountFunction
import com.barrybecker4.math.function.Function
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import com.barrybecker4.simulation.graphing.GraphOptionsDialog
import com.barrybecker4.simulation.predprey.creatures.Foxes
import com.barrybecker4.simulation.predprey.creatures.Population
import com.barrybecker4.simulation.predprey.creatures.Rabbits
import com.barrybecker4.simulation.predprey.options.DynamicOptions
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.JPanel
import java.awt.Color
import java.awt.Graphics
import java.util


class PredPreySimulator() extends Simulator("Predator Prey Simulation") {

  private var foxes = new Foxes
  private var rabbits = new Rabbits

  private var graph: MultipleFunctionRenderer = _
  private var iteration = 0L
  private var rabbitFunction: CountFunction = _
  private var foxFunction: CountFunction = _
  private var options: DynamicOptions = _
  initGraph()

  def getCreatures: Seq[Population] = {
    var creatures = Seq[Population]()
    creatures :+= foxes
    creatures :+= rabbits
    creatures
  }

  override protected def reset(): Unit = {
    initGraph()
    options.reset()
  }

  override protected def getInitialTimeStep = 1.0

  override def timeStep: Double = {
    iteration += 1
    foxes.setPopulation(foxes.getPopulation * foxes.birthRate -
      foxes.getPopulation * foxes.deathRate / rabbits.getPopulation)
    //                   - lions.getPopulation() * foxes.deathRate * foxes.getPopulation());
    rabbits.setPopulation(rabbits.getPopulation * rabbits.birthRate -
      foxes.getPopulation * rabbits.deathRate * rabbits.getPopulation)
    //                    - lions.getPopulation() * rabbits.deathRate * rabbits.getPopulation());
    rabbitFunction.addValue(iteration.toDouble, rabbits.getPopulation.toDouble)
    foxFunction.addValue(iteration.toDouble, foxes.getPopulation.toDouble)
    options.update()
    tStep
  }

  private def initGraph(): Unit = {
    iteration = 0
    rabbits.reset()
    foxes.reset()
    rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS)
    foxFunction = new CountFunction(Foxes.INITIAL_NUM_FOXES)
    val functions = List[Function](rabbitFunction, foxFunction)
    //functions.add(lionFunction);
    val lineColors = List[Color](Rabbits.COLOR, Foxes.COLOR)
    //lineColors.add(Lions.COLOR);
    graph = new MultipleFunctionRenderer(functions, Some(lineColors))
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)

  override def paint(g: Graphics): Unit = {
    graph.setSize(getWidth, getHeight)
    graph.paint(g)
  }
}
