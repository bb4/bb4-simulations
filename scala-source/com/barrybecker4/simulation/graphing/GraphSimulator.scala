// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.common.math.function.Function
import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import com.barrybecker4.ui.animation.AnimationFrame
import com.barrybecker4.ui.renderers.{AbstractFunctionRenderer, SingleFunctionRenderer}
import FunctionType.DIAGONAL
import java.awt._


/**
  * Simulates graphing a function
  * @author Barry Becker
  */
object GraphSimulator {
  def main(args: Array[String]): Unit = {
    val sim = new GraphSimulator
    sim.setPaused(true)
    new AnimationFrame(sim)
  }
}

class GraphSimulator private() extends Simulator("Graph") {
  initGraph()
  private var graph: AbstractFunctionRenderer = _
  private var function: Function = _

  def setFunction(function: Function): Unit = {
    this.function = function
    initGraph()
  }

  override protected def reset(){initGraph() }
  override protected def getInitialTimeStep = 1.0
  override def timeStep: Double = timeStep_
  override protected def createOptionsDialog = new GraphOptionsDialog(frame, this)

  private def initGraph() {
    if (function == null) function = DIAGONAL.function
    graph = new SingleFunctionRenderer(function)
  }

  override def paint(g: Graphics) {
    graph.setSize(getWidth, getHeight)
    graph.paint(g)
  }
}