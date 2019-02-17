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
  private var instance: RDProfiler = _

  /**
    * @return singleton instance.
    */
  def getInstance: RDProfiler = {
    if (instance == null) instance = new RDProfiler
    instance
  }
}

/**
  * Private constructor. Use getInstance instead.
  */
class RDProfiler private() extends Profiler {

  //add(COMMIT_CHANGES, CALCULATION);
  add(RDProfiler.CONCURRENT_CALCULATION, Profiler.CALCULATION)
  private var numFrames = 0

  override def print() {
    super.print()
    val calcTime = getCalcTime
    val renderingTime = getRenderingTime
    printMessage("Number of Frames: " + FormatUtil.formatNumber(numFrames))
    printMessage("Calculation time per frame (sec):" + FormatUtil.formatNumber(calcTime / numFrames))
    printMessage("Rendering time per frame   (sec):" + FormatUtil.formatNumber(renderingTime / numFrames))
    printMessage("FPS: " + FormatUtil.formatNumber((calcTime + renderingTime) / numFrames))
  }

  override def resetAll() {
    super.resetAll()
    numFrames = 0
  }

  def startConcurrentCalculationTime() {
    this.start(RDProfiler.CONCURRENT_CALCULATION)
  }

  def stopConcurrentCalculationTime() {
    this.stop(RDProfiler.CONCURRENT_CALCULATION)
  }

  override def stopRenderingTime() {
    super.stopRenderingTime()
    numFrames += 1
  }
}
