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
  //protected var imageExtractor: ImageExtractor = _
  protected var weights: DoubleArray = _
  protected var periodic: Boolean = false
  protected var tCounter: Int = 0
  protected var dimensions: Dimension = _
  private var random: Random = _
  private var ready: Boolean = false


  def getName: String = name

  def onBoundary(x: Int, y: Int): Boolean

  def isReady: Boolean = ready

  def run(seed: Int): Boolean = {
    ready = false
    if (wave == null) {
      wave = new Wave(dimensions.width, dimensions.height)
      wave.init(tCounter, weights)
    }

    clear()
    random = new Random(seed)

    var ct = 0
    val start = System.currentTimeMillis()
    do {
      val result = wave.observe(tCounter, weights, onBoundary, random)
      if (result.isDefined) {
        ready = true
        return result.get
      }
      wave.propagate(onBoundary, weights, propagator)
      ct += 1
      print(s"$ct, ")
    } while (ct < limit || limit == 0)

    val elapsed = (System.currentTimeMillis() - start)/1000.0
    if (ct == limit) println(s"could not find result in #elapsed")
    else println(s"found result in $elapsed")

    ready = true
    true
  }

  def clear(): Unit = wave.clear(tCounter, weights, propagator)
  def graphics(): BufferedImage // = imageExtractor.getImage(wave: Wave)
}
