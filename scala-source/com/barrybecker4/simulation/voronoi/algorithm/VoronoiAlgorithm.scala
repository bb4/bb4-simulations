// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiAlgorithm.*
import com.barrybecker4.ui.util.ColorMap

import java.awt.image.BufferedImage
import VoronoiAlgorithm._


object VoronoiAlgorithm {
  val DEFAULT_MAX_POINTS = 200
  val DEFAULT_STEPS_PER_FRAME = 10
  val DEFAULT_USE_POISSON = true
  val DEFAULT_CONNECT_POINTS = false
  private val DEFAULT_SIZE = 200
  private val DEFAULT_ALPHA = 200
}

/**
  * Abstract implementation common to all Henon Phase algorithms.
  * Uses concurrency when parallelized is set.
  * This will give good speedup on multi-core machines.
  * @author Barry Becker
  */
class VoronoiAlgorithm() {

  private var model: VoronoiModel = _
  // should extract these into ModelParams class
  private var maxPoints: Int = _
  private var numStepsPerFrame: Int = _
  private var poissonParams: PoissonParams = _
  private var usePoissonDistribution = true
  private var connectPoints = false
  private var alpha: Int = 0
  private var cmap: ColorMap = _
  private var restartRequested = false
  private var finished = false
  private var iterations: Int = 0
  reset()

  /** if the size changes from what we have not, then request a restart */
  def setSize(width: Int, height: Int): Unit = {
    if (width != model.getWidth || height != model.getHeight) requestRestart(width, height)
  }

  def reset(): Unit = {
    maxPoints = VoronoiAlgorithm.DEFAULT_MAX_POINTS
    numStepsPerFrame = VoronoiAlgorithm.DEFAULT_STEPS_PER_FRAME
    poissonParams = new PoissonParams()
    usePoissonDistribution = VoronoiAlgorithm.DEFAULT_USE_POISSON
    connectPoints = VoronoiAlgorithm.DEFAULT_CONNECT_POINTS
    alpha = VoronoiAlgorithm.DEFAULT_ALPHA
    cmap = new VoronoiColorMap(alpha)
    model = new VoronoiModel(DEFAULT_SIZE, DEFAULT_SIZE, poissonParams, usePoissonDistribution, connectPoints, maxPoints, cmap)
  }

  def setPoissonParams(newParams: PoissonParams): Unit = {
    if (!(newParams == poissonParams)) {
      poissonParams = newParams
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def setAlpha(newAlpha: Int): Unit = {
    if (newAlpha != alpha) {
      alpha = newAlpha
      cmap = new VoronoiColorMap(newAlpha)
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def getUsePoissonDistribution: Boolean = usePoissonDistribution

  def toggleUsePoissonDistribution(): Unit = {
    usePoissonDistribution = !usePoissonDistribution
    requestRestart(model.getWidth, model.getHeight)
  }

  def getConnectPoints: Boolean = connectPoints
  def getColorMap: ColorMap = cmap
  def getImage: BufferedImage = model.getImage

  def toggleConnectPoints(): Unit = {
    connectPoints = !connectPoints
    requestRestart(model.getWidth, model.getHeight)
  }

  def setNumSamplePoints(newNumTravelors: Int): Unit = {
    if (newNumTravelors != maxPoints) {
      maxPoints = newNumTravelors
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def setStepsPerFrame(numSteps: Int): Unit = {
    if (numSteps != numStepsPerFrame) {
      numStepsPerFrame = numSteps
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  private def requestRestart(width: Int, height: Int): Unit = {
    model = new VoronoiModel(width, height, poissonParams, usePoissonDistribution, connectPoints, maxPoints, cmap)
    restartRequested = true
  }

  /**
    * @return true when done computing whole model.
    */
  def nextStep(): Boolean = {
    if (restartRequested) {
      println("RESTARTING !!!!!!!!!!!!!!!")
      restartRequested = false
      finished = false
      iterations = 0
      model.reset()
      Profiler.getInstance.startCalculationTime()
    }
    if (iterations >= maxPoints - poissonParams.k) {
      showProfileInfo()
      return true // we are done.
    }
    model.increment(numStepsPerFrame)
    iterations += numStepsPerFrame
    false
  }

  private def showProfileInfo(): Unit = {
    if (!finished) {
      finished = true
      val prof = Profiler.getInstance
      prof.stopCalculationTime()
      prof.print()
      prof.resetAll()
    }
  }
}
