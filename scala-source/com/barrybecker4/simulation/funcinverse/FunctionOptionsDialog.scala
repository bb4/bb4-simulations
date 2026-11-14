// Copyright by Barry G. Becker, 2018-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import com.barrybecker4.math.interpolation.InterpolationMethod
import com.barrybecker4.simulation.common.ui.{FunctionPlotOptionsDialog, Simulator}

import java.awt.Component


/**
  * Configuration for the function-inverse plotter.
  */
class FunctionOptionsDialog(parent: Component, simulator: Simulator)
  extends FunctionPlotOptionsDialog(parent, simulator) {

  private def funcInverseSim: FunctionInverseSimulator =
    getSimulator.asInstanceOf[FunctionInverseSimulator]

  override def getTitle = "Function Inverse Configuration"

  override protected def functionNames: Array[String] =
    InverseFunctionType.values.map(_.name)

  override protected def selectedFunctionIndex: Int =
    funcInverseSim.getFunctionType.ordinal

  override protected def selectedInterpolationMethod: InterpolationMethod =
    funcInverseSim.getInterpolationMethod

  override protected def applySelections(functionIndex: Int, method: InterpolationMethod): Unit =
    funcInverseSim.setFunctionAndInterpolation(
      InverseFunctionType.fromOrdinal(functionIndex),
      method
    )
}
