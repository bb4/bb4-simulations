/** Copyright by Barry G. Becker, 2015 - 2027. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import java.awt.Image
import com.barrybecker4.common.geometry.Box
import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.math.complex.ComplexNumberRange
import com.barrybecker4.simulation.common.Profiler
import FractalAlgorithm.DEFAULT_MAX_ITERATIONS
import com.barrybecker4.ui.util.ColorMap
import scala.collection.parallel.CollectionConverters._

/**
  * Abstract implementation common to all fractal algorithms.
  * Uses Scala parallel collections when parallelized for multicore row computation.
  */
object FractalAlgorithm {
  val DEFAULT_MAX_ITERATIONS = 500
}

abstract class FractalAlgorithm(initialRange: ComplexNumberRange) {

  private val model: FractalModel = new FractalModel()
  model.setCurrentRow(0)

  /** range of bounding box in complex plane. */
  private var range: ComplexNumberRange = initialRange
  private var parallelized = true
  private var maxIterations: Int = DEFAULT_MAX_ITERATIONS
  private val rowCalculator = new RowCalculator(this)
  private var restartRequested: Boolean = false
  private var wasDone: Boolean = false
  private val history: History = new History

  /** Zoom to a new range, remembering the previous range for [[goBack]]. */
  def setRange(newRange: ComplexNumberRange): Unit = {
    history.addRangeToHistory(range)
    processRange(newRange)
  }

  /** Restore the previous range from history, or no-op if history is empty. */
  def goBack(): Unit = {
    if (history.hasHistory)
      processRange(history.popLastRange)
  }

  private def processRange(newRange: ComplexNumberRange): Unit = {
    range = newRange
    restartRequested = true
  }

  def isParallelized: Boolean = parallelized

  def setParallelized(value: Boolean): Unit = {
    if (parallelized != value) {
      parallelized = value
      model.setCurrentRow(0)
    }
  }

  def getMaxIterations: Int = maxIterations

  def setMaxIterations(value: Int): Unit = {
    if (value != maxIterations) {
      maxIterations = value
      model.setCurrentRow(0)
    }
  }

  def getUseRunLengthOptimization: Boolean = rowCalculator.getUseRunLengthOptimization

  def setUseRunLengthOptimization(value: Boolean): Unit =
    rowCalculator.setUseRunLengthOptimization(value)

  def getModel: FractalModel = model
  def setSize(width: Int, height: Int): Unit = model.setSize(width, height)

  /** @param timeStep number of rows to compute on this time-step.
    * @return true when done computing whole model.
    */
  def timeStep(timeStep: Double): Boolean = {
    if (restartRequested) {
      model.setCurrentRow(0)
      restartRequested = false
    }
    if (model.isDone) {
      showProfileInfoWhenFinished()
      true
    } else {
      val numProcs = Runtime.getRuntime.availableProcessors
      val currentRow = model.getCurrentRow
      startProfileTimeIfNeeded(currentRow)
      val height = model.getHeight
      val computeToRow = Math.min(height, currentRow + timeStep.toInt * numProcs)
      val diff = computeToRow - currentRow
      if (diff == 0) true
      else {
        val chunk = Math.max(1, diff / numProcs)
        val (rowRanges, endRow) = buildRowRanges(currentRow, height, numProcs, chunk)
        runRowRanges(rowRanges)
        model.setCurrentRow(endRow)
        model.updateImage()
        false
      }
    }
  }

  private def buildRowRanges(
      startRow: Int,
      height: Int,
      numProcs: Int,
      chunk: Int
  ): (List[(Int, Int)], Int) = {
    var ranges = List.empty[(Int, Int)]
    var row = startRow
    var i = 0
    while (i < numProcs) {
      val nextRow = Math.min(height, row + chunk)
      ranges = (row, nextRow) :: ranges
      row = nextRow
      i += 1
    }
    (ranges, row)
  }

  private def runRowRanges(rowRanges: List[(Int, Int)]): Unit = {
    val width = model.getWidth
    def compute(fromRow: Int, toRow: Int): Unit =
      for (y <- fromRow until toRow) rowCalculator.calculateRow(width, y)

    if (parallelized) rowRanges.par.foreach { case (from, to) => compute(from, to) }
    else rowRanges.foreach { case (from, to) => compute(from, to) }
  }

  /** @return a number between 0 and 1.
    *         Typically corresponds to the number times we had to iterate before the point escaped (or not).
    */
  def getFractalValue(seed: ComplexNumber): Double
  def getImage: Image = model.getImage
  def getAspectRatio: Double = model.getAspectRatio
  def getColorMap: ColorMap = model.getColorMap

  /** Converts from screen coordinates to data coordinates. */
  def getComplexPosition(x: Int, y: Int): ComplexNumber =
    range.getInterpolatedPosition(x.toDouble / model.getWidth, y.toDouble / model.getHeight)

  def getComplexPosition(loc: IntLocation): ComplexNumber = getComplexPosition(loc.getX, loc.getY)

  def getRange(box: Box): ComplexNumberRange =
    ComplexNumberRange(getComplexPosition(box.getTopLeftCorner), getComplexPosition(box.getBottomRightCorner))

  def getRange: ComplexNumberRange = range

  private def startProfileTimeIfNeeded(currentRow: Int): Unit = {
    if (currentRow == 0) {
      Profiler.getInstance.startCalculationTime()
      wasDone = false
    }
  }

  private def showProfileInfoWhenFinished(): Unit = {
    if (!wasDone) {
      val prof = Profiler.getInstance
      prof.stopCalculationTime()
      prof.print()
      prof.resetAll()
      wasDone = true
    }
  }
}
