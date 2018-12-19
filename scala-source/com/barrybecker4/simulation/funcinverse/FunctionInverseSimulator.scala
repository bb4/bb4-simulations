// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.common.math.function.LinearFunction
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.simulation.common.ui.{DistributionSimulator, Simulator}
import com.barrybecker4.ui.renderers.HistogramRenderer


/**
  * To see what kind of distribution of numbers you get.
  * If showRedistribution is true, then the plot should show uniform because
  * the redistribution of a function applied to that function should be uniform.
  * @author Barry Becker
  */
object FunctionInverseSimulator extends App{
    val sim = new FunctionInverseSimulator
    //DistributionSimulator.runSimulation(sim)
}

class FunctionInverseSimulator extends Simulator("Function Inverse Simulator")  {

  private var func: Array[Double] = _

  initHistogram()

  def setFunction(func: Array[Double]): Unit = {
    this.func = func
    initHistogram()
  }

  protected def initHistogram(): Unit = {
  }

  override protected def createOptionsDialog = new FunctionOptionsDialog(frame, this)

  override protected def getInitialTimeStep: Double = ???

  /** @return to the initial state. */
  override protected def reset(): Unit = ???

  override def timeStep: Double = ???
}
