// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.math.function.Function
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer

import java.awt.{Color, Dimension, Graphics}
import javax.swing.JPanel

import scala.compiletime.uninitialized


object FunctionPlotSimulator {
  /** Must leave room for AbstractFunctionRenderer margins (see bb4-ui); small width yields ~1 sample → one dot. */
  val PreferredSize = new Dimension(900, 600)
  val MinimumSize = new Dimension(500, 350)
}

/**
  * Shared static function-plot panel used by graphing and funcinverse demos.
  */
abstract class FunctionPlotSimulator(title: String) extends Simulator(title) {

  protected var plotRenderer: MultipleFunctionRenderer = uninitialized

  initCommonUI()
  setPreferredSize(FunctionPlotSimulator.PreferredSize)
  setMinimumSize(FunctionPlotSimulator.MinimumSize)

  protected def setPlotFunctions(
      functions: Seq[Function],
      colors: Option[Seq[Color]] = None): Unit = {
    plotRenderer = new MultipleFunctionRenderer(functions, colors)
    plotRenderer.setRightNormalizePercent(100)
  }

  override protected def getInitialTimeStep: Double = 1.0

  override def timeStep: Double = tStep

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createOptionsButton)
    controls
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    plotRenderer.setSize(getWidth, getHeight)
    plotRenderer.paint(g)
  }
}
