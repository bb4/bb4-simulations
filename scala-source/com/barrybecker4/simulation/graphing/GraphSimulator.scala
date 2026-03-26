// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.Function
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import FunctionType.DIAGONAL

import java.awt.{Dimension, Graphics}
import javax.swing.JPanel

import scala.compiletime.uninitialized


object GraphSimulator {
  /** Must leave room for AbstractFunctionRenderer margins (see bb4-ui); small width yields ~1 sample → one dot. */
  private val PreferredSize = new Dimension(900, 600)
  private val MinimumSize = new Dimension(500, 350)
}

class GraphSimulator extends Simulator("Graph") {

  private var graph: MultipleFunctionRenderer = uninitialized
  private var function: Function = uninitialized
  initGraph()
  initCommonUI()
  setPreferredSize(GraphSimulator.PreferredSize)
  setMinimumSize(GraphSimulator.MinimumSize)

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
    graph = new MultipleFunctionRenderer(Seq(function))
    graph.setRightNormalizePercent(100)
  }

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createOptionsButton)
    controls
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    graph.setSize(getWidth, getHeight)
    graph.paint(g)
  }
}
