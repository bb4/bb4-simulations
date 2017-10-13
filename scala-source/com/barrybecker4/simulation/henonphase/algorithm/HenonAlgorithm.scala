// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common1.Profiler
import java.awt.image.BufferedImage
import HenonAlgorithm._


/**
  * Abstract implementation common to all Henon Phase algorithms.
  * Uses concurrency when parallelized is set.
  * This will give good speedup on multi-core machines.
  * @author Barry Becker
  */
object HenonAlgorithm {
  val DEFAULT_MAX_ITERATIONS = 1000
  val DEFAULT_FRAME_ITERATIONS = 10
  val DEFAULT_NUM_TRAVELERS = 400
  private val DEFAULT_SIZE = 300
  private val DEFAULT_ALPHA = 200
  private val DEFAULT_UNIFORM_SEEDS = true
  private val DEFAULT_CONNECT_POINTS = false
}

class HenonAlgorithm() {

  private var model: HenonModel = _
  // should extract these into ModelParams class
  private var numTravelors: Int = 0
  private var maxIterations: Int = 0
  private var numStepsPerFrame: Int = 0
  private var travelerParams: TravelerParams = _
  private var useUniformSeeds = false
  private var connectPoints = false
  private var alpha: Int = 0
  private var cmap: ColorMap = _
  private var restartRequested = false
  private var finished = false
  private var iterations: Int = 0
  reset()

  def setSize(width: Int, height: Int) {
    if (width != model.getWidth || height != model.getHeight) requestRestart(width, height)
  }

  def reset() {
    numTravelors = HenonAlgorithm.DEFAULT_NUM_TRAVELERS
    maxIterations = HenonAlgorithm.DEFAULT_MAX_ITERATIONS
    numStepsPerFrame = HenonAlgorithm.DEFAULT_FRAME_ITERATIONS
    travelerParams = new TravelerParams
    useUniformSeeds = HenonAlgorithm.DEFAULT_UNIFORM_SEEDS
    connectPoints = HenonAlgorithm.DEFAULT_CONNECT_POINTS
    alpha = HenonAlgorithm.DEFAULT_ALPHA
    cmap = new HenonColorMap(alpha)
    model = new HenonModel(DEFAULT_SIZE, DEFAULT_SIZE, travelerParams, useUniformSeeds, connectPoints, numTravelors, cmap)
  }

  def setTravelerParams(newParams: TravelerParams) {
    if (!(newParams == travelerParams)) {
      travelerParams = newParams
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def setAlpha(newAlpha: Int){
    if (newAlpha != alpha) {
      alpha = newAlpha
      cmap = new HenonColorMap(newAlpha)
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def getUseUniformSeeds: Boolean = useUniformSeeds

  def toggleUseUniformSeeds() {
    useUniformSeeds = !useUniformSeeds
    requestRestart(model.getWidth, model.getHeight)
  }

  def getConnectPoints: Boolean = connectPoints
  def getColorMap: ColorMap = cmap
  def getImage: BufferedImage = model.getImage

  def toggleConnectPoints() {
    connectPoints = !connectPoints
    requestRestart(model.getWidth, model.getHeight)
  }

  def setNumTravelors(newNumTravelors: Int) {
    if (newNumTravelors != numTravelors) {
      numTravelors = newNumTravelors
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def setMaxIterations(value: Int) {
    if (value != maxIterations) {
      maxIterations = value
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  def setStepsPerFrame(numSteps: Int) {
    if (numSteps != numStepsPerFrame) {
      numStepsPerFrame = numSteps
      requestRestart(model.getWidth, model.getHeight)
    }
  }

  private def requestRestart(width: Int, height: Int) = {
    model = new HenonModel(width, height, travelerParams, useUniformSeeds, connectPoints, numTravelors, cmap)
    restartRequested = true
  }

  /**
    * @param timeStep number of rows to compute on this timestep.
    * @return true when done computing whole model.
    */
  def timeStep(timeStep: Double): Boolean = {
    if (restartRequested) {
      restartRequested = false
      finished = false
      iterations = 0
      model.reset()
      Profiler.getInstance.startCalculationTime()
    }
    if (iterations > maxIterations) {
      showProfileInfo()
      return true // we are done.

    }
    model.increment(numStepsPerFrame)
    iterations += numStepsPerFrame
    false
  }

  private def showProfileInfo() {
    if (!finished) {
      finished = true
      val prof = Profiler.getInstance
      prof.stopCalculationTime()
      prof.print()
      prof.resetAll()
    }
  }
}
