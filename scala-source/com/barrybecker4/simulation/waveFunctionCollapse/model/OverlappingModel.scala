// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.OverlappingImageExtractor
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.Overlapping
import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.OverlappingPropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.patterns.PatternExtractor
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.readImage

import java.awt.Dimension
import java.awt.image.BufferedImage


class OverlappingModel(val name: String, width: Int, height: Int,
  periodicOutput: Boolean, imageParams: OverlappingImageParams, val limit: Int = 0, val scale: Int =2
) extends WfcModel(name, width, height, limit) {

  periodic = periodicOutput
  private var ground: Int = 0
  private val bitmap: BufferedImage = readImage(s"samples/$name.png")
  private val patternExtractor = PatternExtractor(bitmap, imageParams)
  dimensions = new Dimension(width / scale, height / scale)
  private val N = imageParams.N

  tCounter = patternExtractor.tCounter
  ground = patternExtractor.ground
  weights = patternExtractor.weights
  propagator = new OverlappingPropagatorState(tCounter, patternExtractor.patterns, N)

  def this(overlapping: Overlapping) = {
    this(
      overlapping.getName,
      overlapping.getWidth,
      overlapping.getHeight,
      overlapping.getPeriodic,
      overlapping.getImageParams,
      overlapping.getLimit,
      1)
  }

  override def onBoundary(x: Int, y: Int): Boolean = {
    !periodic && (x + N > FMX || y + N > FMY || x < 0 || y < 0)
  }

  override def clear(): Unit = {
    super.clear()

    synchronized {
      if (ground != 0) {
        for (x <- 0 until FMX) {
          for (t <- 0 until tCounter)
            if (t != ground)
              wave.ban(x + (FMY - 1) * FMX, t, weights)
          for (y <- 0 until FMY - 1)
            wave.ban(x + y * FMX, ground, weights)
        }

        wave.propagate(onBoundary, weights, propagator)
      }
    }
  }

  def graphics(): BufferedImage  = {
    assert(this.isReady)
    val imageExtractor = new OverlappingImageExtractor(dimensions, N, tCounter,
      patternExtractor.patterns, patternExtractor.colors, scale, onBoundary)
    imageExtractor.getImage(wave)
  }

  override def toString: String = {
    s"OverlappingModel($getName periodicOutput=$periodicOutput ${imageParams.toString} limit=$limit)"
  }
}
