// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common

import com.barrybecker4.common.format.FormatUtil


object Profiler {
  protected val RENDERING = "rendering"
  val CALCULATION = "calculation"
  private var instance: Profiler = _

  /**
    * @return singleton instance.
    */
  def getInstance: Profiler = {
    if (instance == null) instance = new Profiler
    instance
  }
}

/**
  * Singleton for simulation profiling.
  * For all simulation we would like to know calculation and rendering times.
  * @author Barry Becker
  */
class Profiler protected() extends com.barrybecker4.common.profile.Profiler {
  add(Profiler.CALCULATION)
  add(Profiler.RENDERING)

  def initialize(): Unit = {resetAll()}

  override def print(): Unit = {
    if (!isEnabled) return
    val ratio = getCalcTime / getRenderingTime
    printMessage("-----------------")
    printMessage("Total time = " + (getCalcTime + getRenderingTime))
    printMessage("Ratio of calculation to rendering time:" + FormatUtil.formatNumber(ratio))
    super.print()
  }

  protected def getCalcTime: Double = getEntry(Profiler.CALCULATION).getTimeInSeconds
  protected def getRenderingTime: Double = getEntry(Profiler.RENDERING).getTimeInSeconds

  def startCalculationTime(): Unit = {start(Profiler.CALCULATION)}
  def stopCalculationTime(): Unit = {stop(Profiler.CALCULATION)}
  def startRenderingTime(): Unit = {this.start(Profiler.RENDERING)}
  def stopRenderingTime(): Unit = {this.stop(Profiler.RENDERING)}
}
