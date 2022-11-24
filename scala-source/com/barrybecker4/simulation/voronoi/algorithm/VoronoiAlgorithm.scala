// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiAlgorithm.*
import com.barrybecker4.ui.util.ColorMap

import java.awt.image.BufferedImage
import VoronoiAlgorithm.*
import com.barrybecker4.simulation.voronoi.algorithm.model.PointPlacementModel
import com.barrybecker4.simulation.voronoi.rendering.{VoronoiColorMap, VoronoiRenderer}


object VoronoiAlgorithm {
  val DEFAULT_MAX_POINTS = 200
  val DEFAULT_STEPS_PER_FRAME = 10
  val DEFAULT_USE_POISSON = true
  val DEFAULT_APPLY_DELAUNAY_TRIANGULATION = false
  
  private val DEFAULT_SIZE = 200
  private val DEFAULT_ALPHA = 200
}

/**
  * Builds a voronoi diagram with the following process:
  * - create a set of random points - either using poisson distribution, or uniform distribution
  * - optionally apply delaunay triangulation to those points
  * - optionally generate a Voronoi diagram based on the Delaunay triangulated points.
  * @author Barry Becker
  */
class VoronoiAlgorithm() {

  private var pointModel: PointPlacementModel = _
  private var renderer: VoronoiRenderer = _
  
  private var maxPoints: Int = _
  private var numStepsPerFrame: Int = _
  private var poissonParams: PoissonParams = _
  private var applyDelaunayTriangulation: Boolean = _
  private var alpha: Int = _
  private var cmap: ColorMap = _
  private var restartRequested = false
  private var finished = false
  private var iterations: Int = 0
  reset()

  /** if the size changes from what we have not, then request a restart */
  def setSize(width: Int, height: Int): Unit = {
    if (width != pointModel.getWidth || height != pointModel.getHeight) requestRestart(width, height)
  }
  
  def getColorMap: ColorMap = cmap

  def reset(): Unit = {
    maxPoints = VoronoiAlgorithm.DEFAULT_MAX_POINTS
    numStepsPerFrame = VoronoiAlgorithm.DEFAULT_STEPS_PER_FRAME
    poissonParams = new PoissonParams()
    applyDelaunayTriangulation = VoronoiAlgorithm.DEFAULT_APPLY_DELAUNAY_TRIANGULATION
    alpha = VoronoiAlgorithm.DEFAULT_ALPHA
    cmap = VoronoiColorMap(alpha)
    pointModel = new PointPlacementModel(DEFAULT_SIZE, DEFAULT_SIZE, poissonParams, maxPoints)
    renderer = new VoronoiRenderer(DEFAULT_SIZE, DEFAULT_SIZE)
  }

  def setPoissonParams(newParams: PoissonParams): Unit = {
    if (!(newParams == poissonParams)) {
      poissonParams = newParams
      requestRestart(pointModel.getWidth, pointModel.getHeight)
    }
  }

  def setAlpha(newAlpha: Int): Unit = {
    if (newAlpha != alpha) {
      alpha = newAlpha
      cmap = new VoronoiColorMap(newAlpha)
      requestRestart(pointModel.getWidth, pointModel.getHeight)
    }
  }
  
  def getApplyDelaunayTriangulation: Boolean = applyDelaunayTriangulation

  def toggleApplyDelaunayTriangulation(): Unit = {
    applyDelaunayTriangulation = !applyDelaunayTriangulation
    requestRestart(pointModel.getWidth, pointModel.getHeight)
  }
  
  //def getColorMap: ColorMap = cmap
  def getImage: BufferedImage = renderer.getImage

  def setNumSamplePoints(newNumTravelors: Int): Unit = {
    if (newNumTravelors != maxPoints) {
      maxPoints = newNumTravelors
      requestRestart(pointModel.getWidth, pointModel.getHeight)
    }
  }

  def setStepsPerFrame(numSteps: Int): Unit = {
    if (numSteps != numStepsPerFrame) {
      numStepsPerFrame = numSteps
      requestRestart(pointModel.getWidth, pointModel.getHeight)
    }
  }

  private def requestRestart(width: Int, height: Int): Unit = synchronized {
    pointModel = new PointPlacementModel(width, height, poissonParams, maxPoints)
    renderer = new VoronoiRenderer(width, height)
    restartRequested = true
  }

  /**
    * @return true when done computing whole model.
    */
  def nextStep(): Boolean = synchronized {
    if (restartRequested) {
      println("RESTARTING !!!!!!!!!!!!!!!")
      restartRequested = false
      finished = false
      iterations = 0
      pointModel.reset()
      Profiler.getInstance.startCalculationTime()
    }
    if (iterations >= maxPoints - poissonParams.k) {

      showProfileInfo()
      return true // we are done.
    }
    pointModel.increment(numStepsPerFrame)
    renderer.render(pointModel.getSamples)
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
