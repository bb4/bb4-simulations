/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.common.format.IntegerFormatter
import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.common.math.function.LinearFunction
import com.barrybecker4.simulation.common.ui.DistributionSimulator
import com.barrybecker4.ui.renderers.HistogramRenderer
import com.barrybecker4.ui.util.Log
import java.util


/**
  * Simulates the rolling of N number of M sided dice lots of times
  * to see what kind of distribution of numbers you get.
  *
  * @author Barry Becker
  */
object DiceSimulator {
  def main(args: Array[String]) {
    val sim = new DiceSimulator
    sim.setNumDice(3)
    sim.setNumSides(6)
    DistributionSimulator.runSimulation(sim)
  }
}

class DiceSimulator() extends DistributionSimulator("Dice Histogram") {

  private val options = new DiceOptions
  AppContext.initialize("ENGLISH", util.Arrays.asList("com.barrybecker4.ui.message"), new Log)
  initHistogram()

  def setNumDice(numDice: Int) {
    options.numDice = numDice
    initHistogram()
  }

  def setNumSides(numSides: Int) {
    options.numSides = numSides
    initHistogram()
  }

  override protected def initHistogram() {
    data = new Array[Int](options.numDice * (options.numSides - 1) + 1)
    histogram = new HistogramRenderer(data, new LinearFunction(1.0, -options.numDice))
    histogram.setXFormatter(new IntegerFormatter)
  }

  override protected def createOptionsDialog = new DiceOptionsDialog(frame, this)

  override protected def getXPositionToIncrement: Double = {
    var total = 0
    for (i <- 0 until options.numDice) {
      total += MathUtil.RANDOM.nextInt(options.numSides) + 1
    }
    total
  }
}