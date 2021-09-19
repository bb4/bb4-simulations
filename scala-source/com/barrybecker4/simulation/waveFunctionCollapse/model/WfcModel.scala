// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.Propagator
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave

import java.awt.Dimension
import java.awt.image.BufferedImage
import scala.util.Random


abstract class WfcModel(name: String, val FMX: Int, val FMY: Int, limit: Int) {

  protected var wave: Wave = _
  protected var propagator: Propagator = _
  protected var weights: DoubleArray = _
  protected var periodic: Boolean = false
  protected var tCounter: Int = 0
  protected var dimensions: Dimension = _
  private var random: Random = _
  private var ready: Boolean = false
  private var iterationCt = 0
  private var result: Option[Boolean] = None


  def getName: String = name
  def onBoundary(x: Int, y: Int): Boolean
  def isReady: Boolean = ready

  def runWithLimit(seed: Int, limit: Int = this.limit): Boolean = {
    ready = false
    if (wave == null) {
      wave = new Wave(dimensions.width, dimensions.height)
      wave.init(tCounter, weights)
    }

    clear()
    random = new Random(seed)
    iterationCt = 0
    val steps = if (limit == 0) 10000000 else limit
    advance(steps)
    ready = true
    true
  }

  def startRun(seed: Int): Boolean = {
    runWithLimit(seed, 1)
  }

  /** return None if not done computing, return true if done successfully; flale if done unsuccessfully */
  def advance(steps: Int): Option[Boolean] = {
    if (random == null) {
      println("no advance. Result found")
      return None
    }

    for (i <- 0 until steps) {
      result = wave.observe(tCounter, weights, onBoundary, random)
      if (result.isDefined) {
        ready = true
        return result
      }
      wave.propagate(onBoundary, weights, propagator)
      iterationCt += 1
      if (iterationCt % 100 == 0) print(s"$iterationCt, ")
    }
    println(s"iteration=$iterationCt")
    None
  }

  def clear(): Unit = wave.clear(tCounter, weights, propagator)
  def graphics(): BufferedImage
}
