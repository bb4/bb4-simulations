// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiAlgorithm.*
import com.barrybecker4.ui.util.ColorMap

import java.awt.image.BufferedImage
import VoronoiAlgorithm.*
import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.PointPlacementModel.DistributionType
import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.{PointPlacementModel, PoissonParams}
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer
import com.barrybecker4.simulation.voronoi.ui.VoronoiPanel.MARGIN

import javax.vecmath.Point2d


object VoronoiAlgorithm {
  val DEFAULT_MAX_POINTS = 200
  val DEFAULT_STEPS_PER_FRAME = 10
  val DEFAULT_USE_POISSON = true
  val DEFAULT_SHOW_VORONOI_DIAGRAM = false
  private val DEFAULT_SIZE = 200
}

/**
  * Builds a voronoi diagram with the following process:
  * - create a set of random points - either using poisson distribution, or uniform distribution
  * - optionally generate a Voronoi diagram based on the poisson points.
  * @author Barry Becker
  */
class VoronoiAlgorithm() {

  private var pointModel: PointPlacementModel = _
  private var voronoiRenderer: VoronoiRenderer = _

  private var maxPoints: Int = _
  private var numStepsPerFrame: Int = _
  private var distributionType: PointPlacementModel.DistributionType = PointPlacementModel.DEFAULT_DISTRIBUTION_TYPE
  private var poissonParams: PoissonParams = _
  private var showVoronoiDiagram: Boolean = _
  private var restartRequested = false
  private var finished = false
  private var iterations: Int = 0
  reset()

  /** if the size changes from what we have not, then request a restart */
  def setSize(width: Int, height: Int): Unit = {
    if (width != pointModel.getWidth || height != pointModel.getHeight) requestRestart(width, height)
  }

  def reset(): Unit = {
    maxPoints = VoronoiAlgorithm.DEFAULT_MAX_POINTS
    numStepsPerFrame = VoronoiAlgorithm.DEFAULT_STEPS_PER_FRAME
    poissonParams = new PoissonParams()
    showVoronoiDiagram = VoronoiAlgorithm.DEFAULT_SHOW_VORONOI_DIAGRAM
    pointModel = new PointPlacementModel(DEFAULT_SIZE, DEFAULT_SIZE, poissonParams, maxPoints, distributionType)
    voronoiRenderer = new VoronoiRenderer(pointModel.width, pointModel.height, null)
  }

  def setPoissonParams(newParams: PoissonParams): Unit = {
    if (!(newParams == poissonParams)) {
      poissonParams = newParams
      requestRestart(pointModel.getWidth, pointModel.getHeight)
    }
  }
  
  def getShowVoronoiDiagram: Boolean = showVoronoiDiagram

  def toggleShowVoronoiDiagram(): Unit = {
    showVoronoiDiagram = !showVoronoiDiagram
    requestRestart(pointModel.getWidth, pointModel.getHeight)
  }
  
  def setPointDistribution(distType: PointPlacementModel.DistributionType): Unit = {
    distributionType = distType
    requestRestart(pointModel.getWidth, pointModel.getHeight)
  }

  def getImage: BufferedImage = voronoiRenderer.getImage

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
    pointModel = new PointPlacementModel(width, height, poissonParams, maxPoints, distributionType)
    voronoiRenderer = new VoronoiRenderer(width, height, null)
    restartRequested = true
  }

  /**
    * @return true when done computing whole model.
    */
  def nextStep(): Boolean = synchronized {
    if (restartRequested) {
      println("Restating!")
      restartRequested = false
      finished = false
      iterations = 0
      pointModel.reset()
      Profiler.getInstance.startCalculationTime()
    }
    if (iterations >= maxPoints - poissonParams.k) {

      // all the poisson points generated, now show voronoi diagram based on them
      if (showVoronoiDiagram) {
        val points = convertPoints(pointModel.getSamples)
        val voronoiProcessor = new VoronoiProcessor(points, None)
        voronoiRenderer.show(points, voronoiProcessor.getEdgeList)
      }

      showProfileInfo()
      return true // we are done.
    }
    pointModel.increment(numStepsPerFrame)
    val points = convertPoints(pointModel.getSamples)
    voronoiRenderer.drawPoints(points)
    iterations += numStepsPerFrame
    false
  }

  private def convertPoints(points: IndexedSeq[Point2d]): IndexedSeq[Point] = {
    points.map(pt => new Point(pt.x, pt.y))
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
