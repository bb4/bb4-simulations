/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.simulation.common.Profiler


/**
  * Singleton for RD profiling.
  *
  * @author Barry Becker
  */
object RDProfiler {
  private val CONCURRENT_CALCULATION = "concurrent_calculation"
  private lazy val instance: RDProfiler = new RDProfiler()

  /** @return singleton instance. */
  def getInstance: RDProfiler = instance
}

/**
  * Private constructor. Use getInstance instead.
  */
class RDProfiler private() extends Profiler {

  //add(COMMIT_CHANGES, CALCULATION);
  add(RDProfiler.CONCURRENT_CALCULATION, Profiler.CALCULATION)
  private var numFrames = 0

  override def print(): Unit = {
    super.print()
    val calcTime = getCalcTime
    val renderingTime = getRenderingTime
    val totalTime = calcTime + renderingTime
    printMessage("Number of Frames: " + FormatUtil.formatNumber(numFrames))
    if (numFrames > 0) {
      printMessage("Calculation time per frame (sec):" + FormatUtil.formatNumber(calcTime / numFrames))
      printMessage("Rendering time per frame   (sec):" + FormatUtil.formatNumber(renderingTime / numFrames))
      if (totalTime > 0)
        printMessage("FPS: " + FormatUtil.formatNumber(numFrames / totalTime))
      else
        printMessage("FPS: n/a (zero total time)")
    }
  }

  override def resetAll(): Unit = {
    super.resetAll()
    numFrames = 0
  }

  def startConcurrentCalculationTime(): Unit  = start(RDProfiler.CONCURRENT_CALCULATION)
  def stopConcurrentCalculationTime(): Unit = this.stop(RDProfiler.CONCURRENT_CALCULATION)

  override def stopRenderingTime(): Unit = {
    super.stopRenderingTime()
    numFrames += 1
  }
}
