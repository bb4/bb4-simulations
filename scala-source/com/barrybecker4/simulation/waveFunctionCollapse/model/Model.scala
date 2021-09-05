// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.ImageExtractor
import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.Propagator
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave

import java.awt.image.BufferedImage
import scala.util.Random


abstract class Model(name: String, val FMX: Int, val FMY: Int) {

  protected var wave: Wave = _
  protected var propagator: Propagator = _
  protected var imageExtractor: ImageExtractor = _
  protected var weights: DoubleArray = _
  protected var periodic: Boolean = false
  protected var tCounter: Int = 0
  private var random: Random = _

  def getName: String = name


  def onBoundary(x: Int, y: Int): Boolean

  def run(seed: Int, limit: Int): Boolean = {
    if (wave == null) {
      wave = new Wave(FMX, FMY)
      wave.init(tCounter, weights)
    }

    clear()
    random = new Random(seed)

    var l = 0
    do {
      val result = wave.observe(tCounter, weights, onBoundary, random)
      if (result.isDefined) {
        return result.get
      }
      wave.propagate(onBoundary, weights, propagator)
      l += 1
    } while (l < limit || limit == 0)

    true
  }

  def clear(): Unit = wave.clear(tCounter, weights, propagator)
  def graphics(): BufferedImage = imageExtractor.getImage(wave: Wave)
}
