/** Copyright by Barry G. Becker, 2015 - 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
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

import scala.compiletime.uninitialized

/**
  * Abstract implementation common to all fractal algorithms.
  * Uses scala parallel collections when parallelized for multicore row computation.
  *
  * For core 2 Duo:
  * - not-parallel 32.4 seconds
  * - parallel     19.5 seconds
  *
  * For i7 2600k
  *                  original            after rendering optimization.
  * - not-parallel  16.0  seconds         6.1
  * - parallel       5.1 seconds          2.1
  *
  * * Initial benchmark
  *                                           calcTime     renderTime
  * Scala with java style parallelization:     7.8         2.4 - 6.8
  * Scala with java style                     18.6         2.0 - 7.09
  *
  * Scala with scala style parallelization      4.0        0.02 - 2.4
  * Scala with scala style                     8.8         0.02 - 2.4
  *
  * scala parallel overview                   0.2 - 0.4        2.0
  * Java parallel overview                    0.5 - 0.9        2.0
  *
  * @author Barry Becker
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
  private var rowCalculator: RowCalculator = uninitialized
  private var restartRequested: Boolean = false
  private var wasDone: Boolean = false
  private val history: History = new History

  setParallelized(true)
  rowCalculator = new RowCalculator(this)

  def setRange(range: ComplexNumberRange): Unit = {
    history.addRangeToHistory(range)
    processRange(range)
  }

  /** Back up one step in the history */
  def goBack(): Unit = {
    if (history.hasHistory) {
      var newRange: ComplexNumberRange = history.popLastRange
      if (range == newRange) {
        newRange = history.popLastRange
      }
      processRange(newRange)
    }
  }

  private def processRange(newRange: ComplexNumberRange): Unit = {
    range = newRange
    restartRequested = true
  }

  def isParallelized: Boolean = parallelized

  def setParallelized(parallelized: Boolean): Unit = {
    if (this.parallelized != parallelized) {
      this.parallelized = parallelized
      model.setCurrentRow(0)
    }
  }

  def getMaxIterations: Int = maxIterations

  def setMaxIterations(value: Int): Unit =  {
    if (value != maxIterations) {
      maxIterations = value
      model.setCurrentRow(0)
    }
  }

  def getUseRunLengthOptimization: Boolean = rowCalculator.getUseRunLengthOptimization

  def setUseRunLengthOptimization(value: Boolean): Unit =  {
    rowCalculator.setUseRunLengthOptimization(value)
  }

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
      var currentRow = model.getCurrentRow
      startProfileTimeIfNeeded(currentRow)
      val height = model.getHeight
      val computeToRow = Math.min(height, currentRow + timeStep.toInt * numProcs)
      val diff = computeToRow - currentRow
      if (diff == 0) true
      else {
        val chunk = Math.max(1, diff / numProcs)
        val (workers, endRow) = buildRowWorkers(currentRow, height, numProcs, chunk)
        runRowWorkers(workers)
        model.setCurrentRow(endRow)
        model.updateImage()
        false
      }
    }
  }

  private def buildRowWorkers(
      startRow: Int,
      height: Int,
      numProcs: Int,
      chunk: Int
  ): (List[Worker], Int) = {
    var workers = List.empty[Worker]
    var row = startRow
    var i = 0
    while (i < numProcs) {
      val nextRow = Math.min(height, row + chunk)
      workers = new Worker(row, nextRow) :: workers
      row = nextRow
      i += 1
    }
    (workers, row)
  }

  private def runRowWorkers(workers: List[Worker]): Unit =
    if (parallelized) workers.par.foreach(_.run())
    else workers.foreach(_.run())

  /** @return a number between 0 and 1.
    *         Typically corresponds to the number times we had to iterate before the point escaped (or not).
    */
  def getFractalValue(seed: ComplexNumber): Double
  def getImage: Image = model.getImage
  def getAspectRatio: Double = model.getAspectRatio
  def getColorMap: ColorMap = model.getColorMap

  /** Converts from screen coordinates to data coordinates.
    * @param x real valued coordinate
    * @param y pure imaginary coordinate
    * @return corresponding position in complex number plane represented by the model.
    */
  def getComplexPosition(x: Int, y: Int): ComplexNumber = {
    range.getInterpolatedPosition(x.toDouble / model.getWidth, y.toDouble / model.getHeight)
  }

  def getComplexPosition(loc: IntLocation): ComplexNumber = getComplexPosition(loc.getX, loc.getY)

  def getRange(box: Box): ComplexNumberRange = {
    val firstCorner: ComplexNumber = getComplexPosition(box.getTopLeftCorner)
    val secondCorner: ComplexNumber = getComplexPosition(box.getBottomRightCorner)
    ComplexNumberRange(firstCorner, secondCorner)
  }

  def getRange: ComplexNumberRange = range

  private def startProfileTimeIfNeeded(currentRow: Int): Unit = {
    if (currentRow == 0) {
      Profiler.getInstance.startCalculationTime()
      wasDone = false
    }
  }

  private def showProfileInfoWhenFinished(): Unit = {
    if (!wasDone) {
      val prof: Profiler = Profiler.getInstance
      prof.stopCalculationTime()
      prof.print()
      prof.resetAll()
      wasDone = true
    }
  }

  /** Runs one of the chunks. */
  private class Worker(fromRow: Int, toRow: Int) extends Runnable {

    /** Do a chunk of work (i.e. compute the specified rows) */
    private def computeChunk(rowsFrom: Int, rowsTo: Int): Unit = {
      val width = model.getWidth
      for (y <- rowsFrom until rowsTo) rowCalculator.calculateRow(width, y)
    }

    def run(): Unit = computeChunk(fromRow, toRow)
  }
}
