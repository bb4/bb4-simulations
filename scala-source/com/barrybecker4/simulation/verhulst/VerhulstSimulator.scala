// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import java.awt.{Color, Graphics}

import com.barrybecker4.math.function.CountFunction
import com.barrybecker4.math.function.Function
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.graphing.GraphOptionsDialog
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import javax.swing._

import scala.compiletime.uninitialized


class VerhulstSimulator extends Simulator("Verhulst Simulation") {
  private var rabbits = new Rabbits
  initGraph()
  private var graph: MultipleFunctionRenderer = uninitialized
  private var iteration = 0L
  private var rabbitFunction: CountFunction = uninitialized
  private var options: DynamicOptions = uninitialized

  def getCreatures: Seq[Population] = Seq(rabbits)

  override protected def reset(): Unit = {
    initGraph()
    options.reset()
  }

  override protected def getInitialTimeStep = 1.0

  override def timeStep: Double = {
    iteration += 1
    val newPop = VerhulstMap.nextPopulation(rabbits.getPopulation, rabbits.birthRate)
    rabbits.setPopulation(newPop)
    rabbitFunction.addValue(iteration.toDouble, rabbits.getPopulation)
    tStep
  }

  protected def initGraph(): Unit = {
    iteration = 0
    rabbits.reset()
    rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS)
    rabbitFunction.setMaxXValues(200)
    val functions: Seq[Function] = Seq(rabbitFunction)
    val lineColors = Seq(Rabbits.COLOR)
    graph = new MultipleFunctionRenderer(functions, Some(lineColors))
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
