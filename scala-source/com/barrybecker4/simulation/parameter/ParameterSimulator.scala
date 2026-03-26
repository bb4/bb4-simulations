// Copyright by Barry G. Becker, 2017 - 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.math.MathUtil
import com.barrybecker4.math.function.LinearFunction
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.simulation.common.ui.DistributionSimulator
import com.barrybecker4.ui.renderers.HistogramRenderer


/**
  * To see what kind of distribution of numbers you get.
  * If showRedistribution is true, then the plot should show uniform because
  * the redistribution of a function applied to that function should be uniform.
  * @author Barry Becker
  */
object ParameterSimulator:

  private val NumDoubleBins = 1000

  def main(args: Array[String]): Unit =
    val sim = new ParameterSimulator
    DistributionSimulator.runSimulation(sim)

  /** Number of histogram bins for continuous (double) parameters. */
  private[parameter] def numDoubleBins: Int = NumDoubleBins

  /**
    * Scale factor for [[LinearFunction]] mapping raw values into bin indices: `NumDoubleBins / range`.
    * @throws IllegalArgumentException if range is not positive
    */
  private[parameter] def doubleHistogramScale(range: Double): Double =
    require(range > 0.0, "continuous parameter range must be positive for histogram scaling")
    NumDoubleBins / range

  /** Second coefficient for [[LinearFunction]]: `-minValue` so bin index uses normalized position. */
  private[parameter] def doubleHistogramOffset(minValue: Double): Double = -minValue

end ParameterSimulator

class ParameterSimulator() extends DistributionSimulator("Parameter Histogram"):

  /** initialize with some default */
  private var parameter = ParameterDistributionType.fromOrdinal(0).param
  private[parameter] var showRedistribution = true
  initHistogram()

  def setParameter(parameter: Parameter): Unit =
    this.parameter = parameter
    initHistogram()

  override protected def initHistogram(): Unit =
    if parameter.isIntegerOnly then initIntegerHistogram()
    else initDoubleHistogram()

  private def initIntegerHistogram(): Unit =
    data = new Array[Int](parameter.range.toInt + 1)
    histogram = new HistogramRenderer(data)

  private def initDoubleHistogram(): Unit =
    data = new Array[Int](ParameterSimulator.numDoubleBins)
    val scale = ParameterSimulator.doubleHistogramScale(parameter.range)
    val offset = ParameterSimulator.doubleHistogramOffset(parameter.minValue)
    val xFunc = new LinearFunction(scale, offset)
    histogram = new HistogramRenderer(data, xFunc)

  override protected def createOptionsDialog = new ParameterOptionsDialog(frame, this)

  /**
    * When `showRedistribution` is true, samples via the parameter's redistribution (expected uniform after mapping).
    * Otherwise, samples uniformly on the raw numeric range.
    */
  override protected def getXPositionToIncrement: Double =
    if showRedistribution then parameter.randomizeValue(MathUtil.RANDOM).getValue
    else
      val v = parameter.minValue + MathUtil.RANDOM.nextDouble() * parameter.range
      parameter.setValue(v).getValue

end ParameterSimulator
