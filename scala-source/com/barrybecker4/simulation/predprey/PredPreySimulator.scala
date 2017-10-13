// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey

import com.barrybecker4.common.math.function.CountFunction
import com.barrybecker4.common.math.function.Function
import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorApplet
import com.barrybecker4.simulation.graphing1.GraphOptionsDialog
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
import java.util.{ArrayList, LinkedList, List}


/**
  * Simulates foxes (predators) and rabbits (prey) in the wild.
  *
  * @author Barry Becker
  */
object PredPreySimulator {
  def main(args: Array[String]): Unit = {
    val sim = new PredPreySimulator
    sim.setPaused(true)
    GUIUtil.showApplet(new SimulatorApplet(sim))
  }
}

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
    foxes.setPopulation(foxes.getPopulation * foxes.birthRate - foxes.getPopulation * foxes.deathRate / rabbits.getPopulation)
    //                   - lions.getPopulation() * foxes.deathRate * foxes.getPopulation());
    rabbits.setPopulation(rabbits.getPopulation * rabbits.birthRate - foxes.getPopulation * rabbits.deathRate * rabbits.getPopulation)
    //                    - lions.getPopulation() * rabbits.deathRate * rabbits.getPopulation());
    rabbitFunction.addValue(iteration, rabbits.getPopulation)
    foxFunction.addValue(iteration, foxes.getPopulation)
    options.update()
    timeStep_
  }

  private def initGraph(): Unit = {
    iteration = 0
    rabbits.reset()
    foxes.reset()
    rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS)
    foxFunction = new CountFunction(Foxes.INITIAL_NUM_FOXES)
    val functions = new util.LinkedList[Function]
    functions.add(rabbitFunction)
    functions.add(foxFunction)
    //functions.add(lionFunction);
    val lineColors = new util.LinkedList[Color]
    lineColors.add(Rabbits.COLOR)
    lineColors.add(Foxes.COLOR)
    //lineColors.add(Lions.COLOR);
    graph = new MultipleFunctionRenderer(functions, lineColors)
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  override protected def createOptionsDialog = new GraphOptionsDialog(frame, this)

  override def paint(g: Graphics): Unit = {
    graph.setSize(getWidth, getHeight)
    graph.paint(g)
  }
}