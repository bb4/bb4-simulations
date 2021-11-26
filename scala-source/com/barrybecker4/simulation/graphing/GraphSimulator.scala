// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.Function
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.ui.animation.AnimationFrame
import com.barrybecker4.ui.renderers.{AbstractFunctionRenderer, SingleFunctionRenderer}
import FunctionType.DIAGONAL

import java.awt.*
import javax.swing.JPanel


class GraphSimulator extends Simulator("Graph") {

  private var graph: AbstractFunctionRenderer = _
  private var function: Function = _
  initGraph()

  def setFunction(function: Function): Unit = {
    this.function = function
    initGraph()
  }

  override protected def reset(): Unit = { initGraph() }
  override protected def getInitialTimeStep = 1.0
  override def timeStep: Double = tStep
  override protected def createOptionsDialog = new GraphOptionsDialog(frame, this)

  private def initGraph(): Unit = {
    if (function == null)
      function = DIAGONAL.function
    graph = new SingleFunctionRenderer(function)
  }

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createOptionsButton)
    controls
  }

  override def paint(g: Graphics): Unit = {
    graph.setSize(getWidth, getHeight)
    graph.paint(g)
  }
}
