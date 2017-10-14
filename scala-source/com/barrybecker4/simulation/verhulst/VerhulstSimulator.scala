// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import com.barrybecker4.common.math.function.CountFunction
import com.barrybecker4.common.math.function.Function
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.graphing.GraphOptionsDialog
import com.barrybecker4.ui.animation.AnimationFrame
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import javax.swing._
import java.awt._
import java.util


/**
  * Simulates foxes (predators) and rabbits (prey) in the wild.
  * @author Barry Becker
  */
object VerhulstSimulator {
  def main(args: Array[String]): Unit = {
    val sim = new VerhulstSimulator
    sim.setPaused(true)
    new AnimationFrame(sim)
  }
}

class VerhulstSimulator() extends Simulator("Verhulst Simulation") {
  private var rabbits = new Rabbits
  initGraph()
  private var graph: MultipleFunctionRenderer = _
  private var iteration = 0L
  private var rabbitFunction: CountFunction = _
  private var options: DynamicOptions = _

  def getCreatures: Seq[Population] = Array[Population](rabbits)

  override protected def reset(): Unit = {
    initGraph()
    options.reset()
  }

  override protected def getInitialTimeStep = 1.0

  override def timeStep: Double = {
    iteration += 1
    val newPop = rabbits.getPopulation * ((1.0 + rabbits.birthRate) - rabbits.birthRate * rabbits.getPopulation)
    //System.out.println("pop="+ newPop + " rate="+ rabbits.birthRate);
    rabbits.setPopulation(newPop)
    rabbitFunction.addValue(iteration, rabbits.getPopulation)
    tStep
  }

  protected def initGraph(): Unit = {
    iteration = 0
    rabbits.reset()
    rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS)
    rabbitFunction.setMaxXValues(200)
    val functions = new util.LinkedList[Function]
    functions.add(rabbitFunction)
    val lineColors = new util.LinkedList[Color]
    lineColors.add(Rabbits.COLOR)
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