// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.common.math.function.LinearFunction
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.simulation.common.ui.DistributionSimulator
import com.barrybecker4.ui.renderers.HistogramRenderer


/**
  * To see what kind of distribution of numbers you get.
  * If showRedistribution is true, then the plot should show uniform because
  * the redistribution of a function applied to that function should be uniform.
  *
  * @author Barry Becker
  */
object ParameterSimulator {
  private val NUM_DOUBLE_BINS = 1000

  def main(args: Array[String]): Unit = {
    val sim = new ParameterSimulator
    DistributionSimulator.runSimulation(sim)
  }
}

class ParameterSimulator() extends DistributionSimulator("Parameter Histogram") {

  /** initialize with some default */
  private var parameter = ParameterDistributionType.VALUES(0).param
  private[parameter] var showRedistribution = true
  initHistogram()

  def setParameter(parameter: Parameter): Unit = {
    this.parameter = parameter
    initHistogram()
  }

  override protected def initHistogram(): Unit = {
    if (parameter.isIntegerOnly) {
      data = new Array[Int](parameter.range.toInt + 1)
      histogram = new HistogramRenderer(data)
    }
    else {
      data = new Array[Int](ParameterSimulator.NUM_DOUBLE_BINS)
      val scale = ParameterSimulator.NUM_DOUBLE_BINS / parameter.range
      val offset = -parameter.minValue
      println("new Lin scale = " +scale + " off=" + offset)
      val xFunc = new LinearFunction(scale, offset)
      histogram = new HistogramRenderer(data, xFunc)
    }
  }

  override protected def createOptionsDialog = new ParameterOptionsDialog(frame, this)

  override protected def getXPositionToIncrement: Double = {
    if (showRedistribution) parameter.randomizeValue(MathUtil.RANDOM)
    else { //println("parameter.getRange()="+parameter.getRange());
      //double scale = parameter.isIntegerOnly()?  parameter.getRange() +1.0 : parameter.getRange();
      val scale = parameter.range
      val v = parameter.minValue + MathUtil.RANDOM.nextDouble * scale
      parameter.setValue(v)
    }
    parameter.getValue
  }
}