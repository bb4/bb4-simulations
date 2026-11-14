// Copyright by Barry G. Becker, 2016-2027. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.Function
import com.barrybecker4.math.interpolation.{InterpolationMethod, LINEAR}
import com.barrybecker4.simulation.common.ui.FunctionPlotSimulator
import GraphFunctionType.DIAGONAL


/**
  * Demo that plots a selected sample function with a chosen interpolation method.
  */
class GraphSimulator extends FunctionPlotSimulator("Graph") {

  private var functionType: GraphFunctionType = DIAGONAL
  private var interpMethod: InterpolationMethod = LINEAR
  private var function: Function = GraphInterpolation(DIAGONAL.function, LINEAR)

  rebuildPlot()

  def getFunctionType: GraphFunctionType = functionType

  def getInterpolationMethod: InterpolationMethod = interpMethod

  def setFunction(functionType: GraphFunctionType, method: InterpolationMethod): Unit = {
    this.functionType = functionType
    this.interpMethod = method
    this.function = GraphInterpolation(functionType.function, method)
    rebuildPlot()
  }

  /** Apply an already-built function (used by tests / legacy callers). */
  def setFunction(function: Function): Unit = {
    this.function = function
    rebuildPlot()
  }

  override protected def reset(): Unit = rebuildPlot()

  override protected def createOptionsDialog = new GraphOptionsDialog(frame, this)

  private def rebuildPlot(): Unit =
    setPlotFunctions(Seq(function))
}
