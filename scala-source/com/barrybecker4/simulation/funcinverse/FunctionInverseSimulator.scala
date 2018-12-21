// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import java.awt.{Color, Graphics}

import com.barrybecker4.common.math.function.{ArrayFunction, Function, LinearFunction}
import com.barrybecker4.common.math.interpolation.InterpolationMethod
import com.barrybecker4.simulation.common.ui.{DistributionSimulator, Simulator}
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import javax.swing.JPanel
import com.barrybecker4.common.math.interpolation.LINEAR


/**
  * To compare a function with its inverse.
  * @author Barry Becker
  */
class FunctionInverseSimulator extends Simulator("Function Inverse Simulator")  {

  private val FUNC_COLOR = new Color(0, 50, 180)
  private val INV_FUNC_COLOR = new Color(150, 10, 0)

  private var func: FunctionType.Val = FunctionType.QUADRATIC
  private var interpMethod: InterpolationMethod = LINEAR
  private var funcRenderer: MultipleFunctionRenderer = _

  initFuncWithInverse()

  def setFunction(func: FunctionType.Val): Unit = {
    this.func = func
    initFuncWithInverse()
  }

  def setInterpolationMethod(interpMethod: InterpolationMethod): Unit = {
    this.interpMethod = interpMethod
    initFuncWithInverse()
  }

  protected def initFuncWithInverse(): Unit = {
    val theFunction = new ArrayFunction(func.func, interpMethod)
    val inverseFunction = new ArrayFunction(theFunction.inverseFunctionMap, interpMethod)
    funcRenderer = new MultipleFunctionRenderer(
      Seq(theFunction, inverseFunction),
      Some(Seq(FUNC_COLOR, INV_FUNC_COLOR))
    )
    funcRenderer.setRightNormalizePercent(100)
  }

  override protected def createOptionsDialog = new FunctionOptionsDialog(frame, this)

  override protected def getInitialTimeStep: Double = 1.0

  /** @return to the initial state. */
  override protected def reset(): Unit = initFuncWithInverse()

  override def timeStep: Double = tStep

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createOptionsButton)
    controls
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    funcRenderer.setSize(getWidth, getHeight)
    funcRenderer.paint(g)
  }
}
