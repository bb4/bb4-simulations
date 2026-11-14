// Copyright by Barry G. Becker, 2018-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import java.awt.Color
import com.barrybecker4.math.function.ArrayFunction
import com.barrybecker4.math.interpolation.{InterpolationMethod, LINEAR}
import com.barrybecker4.simulation.common.ui.FunctionPlotSimulator


/**
  * Compare a function with its inverse.
  * @author Barry Becker
  */
class FunctionInverseSimulator extends FunctionPlotSimulator("Function Inverse Simulator") {

  private val FUNC_COLOR = new Color(0, 50, 180)
  private val INV_FUNC_COLOR = new Color(150, 10, 0)

  private var func: InverseFunctionType = InverseFunctionType.QUADRATIC
  private var interpMethod: InterpolationMethod = LINEAR

  initFuncWithInverse()

  def setFunction(func: InverseFunctionType): Unit = {
    this.func = func
    initFuncWithInverse()
  }

  def setInterpolationMethod(interpMethod: InterpolationMethod): Unit = {
    this.interpMethod = interpMethod
    initFuncWithInverse()
  }

  def setFunctionAndInterpolation(func: InverseFunctionType, method: InterpolationMethod): Unit = {
    this.func = func
    this.interpMethod = method
    initFuncWithInverse()
  }

  def getFunctionType: InverseFunctionType = func

  def getInterpolationMethod: InterpolationMethod = interpMethod

  protected def initFuncWithInverse(): Unit = {
    val theFunction = new ArrayFunction(func.func, interpMethod)
    // Plot the computed inverse samples directly — do not invert again.
    val inverseFunction = ArrayFunction.fromSamples(theFunction.inverseFunctionMap, interpMethod)
    setPlotFunctions(
      Seq(theFunction, inverseFunction),
      Some(Seq(FUNC_COLOR, INV_FUNC_COLOR))
    )
  }

  override protected def createOptionsDialog = new FunctionOptionsDialog(frame, this)

  override protected def reset(): Unit = initFuncWithInverse()
}
