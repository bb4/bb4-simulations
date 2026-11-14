// Copyright by Barry G. Becker, 2017 - 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.math.Range
import com.barrybecker4.math.function.LinearFunction
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.simulation.common.ui.DistributionSimulator
import com.barrybecker4.ui.renderers.HistogramRenderer

import scala.util.Random


/**
  * Histogram demo of bb4-optimization [[Parameter]] redistribution.
  *
  * When [[showRedistribution]] is true, each sample uses `randomizeValue` then `getValue`,
  * which applies the forward redistribution — the plot shows the skewed redistributed shape.
  * When false, a uniform raw value is passed through `setValue`/`getValue` (inverse then
  * forward), so the plot should be close to uniform.
  */
object ParameterSimulator:

  private val NumDoubleBins = 1000

  def main(args: Array[String]): Unit =
    val sim = new ParameterSimulator
    DistributionSimulator.runSimulation(sim)

  /** Number of histogram bins for continuous (double) parameters. */
  private[parameter] def numDoubleBins: Int = NumDoubleBins

  /**
    * [[LinearFunction]] mapping continuous parameter values in `[minValue, maxValue]`
    * into bin indices `[0, NumDoubleBins]`.
    */
  private[parameter] def doubleHistogramFunction(minValue: Double, maxValue: Double): LinearFunction =
    require(maxValue > minValue, "continuous parameter range must be positive for histogram scaling")
    new LinearFunction(Range(minValue, maxValue), NumDoubleBins)

  /** Scale factor for tests / diagnostics: `NumDoubleBins / range`. */
  private[parameter] def doubleHistogramScale(range: Double): Double =
    require(range > 0.0, "continuous parameter range must be positive for histogram scaling")
    NumDoubleBins / range

  /**
    * Offset for `scale * x + offset` mapping: `-scale * minValue`
    * (not `-minValue`; that only works when scale is 1).
    */
  private[parameter] def doubleHistogramOffset(minValue: Double, scale: Double): Double =
    -scale * minValue

end ParameterSimulator

class ParameterSimulator() extends DistributionSimulator("Parameter Histogram"):

  private var distributionType: ParameterDistributionType = ParameterDistributionType.fromOrdinal(0)
  private var parameter: Parameter = distributionType.param
  private[parameter] var showRedistribution = true
  private val rnd = new Random
  initHistogram()

  private[parameter] def selectedDistributionType: ParameterDistributionType = distributionType

  def setParameter(parameter: Parameter): Unit =
    this.parameter = parameter
    initHistogram()

  /** Select a catalog preset (updates parameter and histogram). */
  def setDistributionType(distributionType: ParameterDistributionType): Unit =
    this.distributionType = distributionType
    setParameter(distributionType.param)

  override protected def initHistogram(): Unit =
    if parameter.isIntegerOnly then initIntegerHistogram()
    else initDoubleHistogram()

  private def initIntegerHistogram(): Unit =
    data = new Array[Int](parameter.range.toInt + 1)
    // Identity scale with offset so non-zero min maps into bin 0.
    histogram = new HistogramRenderer(data, new LinearFunction(1.0, -parameter.minValue), true)

  private def initDoubleHistogram(): Unit =
    data = new Array[Int](ParameterSimulator.numDoubleBins)
    val xFunc = ParameterSimulator.doubleHistogramFunction(parameter.minValue, parameter.maxValue)
    histogram = new HistogramRenderer(data, xFunc)

  override protected def createOptionsDialog = new ParameterOptionsDialog(frame, this)

  /**
    * @see ParameterSimulator object docs for the meaning of [[showRedistribution]].
    */
  override protected def getXPositionToIncrement: Double =
    if showRedistribution then parameter.randomizeValue(rnd).getValue
    else
      val v = parameter.minValue + rnd.nextDouble() * parameter.range
      parameter.setValue(v).getValue

  /** Exposed for seeded sampling tests. */
  private[parameter] def sampleXPosition(rand: Random): Double =
    if showRedistribution then parameter.randomizeValue(rand).getValue
    else
      val v = parameter.minValue + rand.nextDouble() * parameter.range
      parameter.setValue(v).getValue

  private[parameter] def histogramBinCount: Int = data.length

end ParameterSimulator
