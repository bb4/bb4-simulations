// Copyright by Barry G. Becker, 2016-2027. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.interpolation.InterpolationMethod
import com.barrybecker4.simulation.common.ui.{FunctionPlotOptionsDialog, Simulator}

import java.awt.Component


/**
  * Options for the graphing demo.
  */
class GraphOptionsDialog(parent: Component, simulator: Simulator)
  extends FunctionPlotOptionsDialog(parent, simulator) {

  private def graphSim: GraphSimulator = getSimulator.asInstanceOf[GraphSimulator]

  override def getTitle = "Graph Simulation Configuration"

  override protected def functionNames: Array[String] =
    GraphFunctionType.values.map(_.name)

  override protected def selectedFunctionIndex: Int =
    graphSim.getFunctionType.ordinal

  override protected def selectedInterpolationMethod: InterpolationMethod =
    graphSim.getInterpolationMethod

  override protected def applySelections(functionIndex: Int, method: InterpolationMethod): Unit =
    graphSim.setFunction(GraphFunctionType.fromOrdinal(functionIndex), method)
}
