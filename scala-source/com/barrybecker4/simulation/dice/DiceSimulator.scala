/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.common.format.IntegerFormatter
import com.barrybecker4.math.MathUtil
import com.barrybecker4.math.function.LinearFunction
import com.barrybecker4.simulation.common.ui.DistributionSimulator
import com.barrybecker4.ui.renderers.HistogramRenderer
import com.barrybecker4.ui.util.Log


/**
  * Simulates the rolling of N number of M sided dice lots of times
  * to see what kind of distribution of numbers you get.
  * @author Barry Becker
  */
object DiceSimulator {
  def main(args: Array[String]): Unit = {
    val sim = new DiceSimulator
    sim.setNumDice(3)
    sim.setNumSides(6)
    DistributionSimulator.runSimulation(sim)
  }
}

class DiceSimulator() extends DistributionSimulator("Dice Histogram") {

  private val options = new DiceOptions
  AppContext.initialize("ENGLISH", List("com.barrybecker4.ui.message"), new Log)
  initHistogram()

  def setNumDice(numDice: Int): Unit = {
    require(numDice >= 1, "numDice must be >= 1")
    options.numDice = numDice
    initHistogram()
  }

  def setNumSides(numSides: Int): Unit = {
    require(numSides >= 1, "numSides must be >= 1")
    options.numSides = numSides
    initHistogram()
  }

  override protected def initHistogram(): Unit = {
    data = new Array[Int](DiceRollLogic.histogramBinCount(options.numDice, options.numSides))
    histogram =
      new HistogramRenderer(data, new LinearFunction(1.0, -options.numDice), true)
    histogram.setXFormatter(new IntegerFormatter)
  }

  override protected def createOptionsDialog = new DiceOptionsDialog(frame, this)

  override protected def getXPositionToIncrement: Double =
    DiceRollLogic.rollSum(options.numDice, options.numSides, MathUtil.RANDOM)
}
