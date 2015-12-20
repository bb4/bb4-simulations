/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.math.ComplexNumber
import com.barrybecker4.common.math.ComplexNumberRange
import com.barrybecker4.simulation.common.Profiler

/**
  * Abstract implementation common to all fractal algorithms.
  * Uses concurrency when parallelized is set.
  * This will give good speedup on multi-core machines.
  *
  * For core 2 Duo:
  * - not-parallel 32.4 seconds
  * - parallel     19.5 seconds
  *
  * For i7 2600k
  * original                    after rendering optimization.
  * - not-parallel  16.0  seconds         6.1
  * - parallel       5.1 seconds          2.1
  * @author Barry Becker
  */
object FractalAlgorithm {
  val DEFAULT_MAX_ITERATIONS: Int = 2000
}

abstract class FractalAlgorithm(model: FractalModel, initialRange: ComplexNumberRange) {

  /** range of bounding box in complex plane. */
  private var range: ComplexNumberRange = initialRange
  private var parallelize = true
  private var maxIterations_ : Int = FractalAlgorithm.DEFAULT_MAX_ITERATIONS
  private var rowCalculator_ : RowCalculator = null
  private var restartRequested: Boolean = false
  private var wasDone: Boolean = false
  private val history_ : History = new History

  setParallelized(true)
  rowCalculator_ = new RowCalculator(this)

  def setRange(range: ComplexNumberRange) {
    history_.addRangeToHistory(range)
    processRange(range)
  }

  /** Back up one step in the history */
  def goBack() {
    if (history_.hasHistory) {
      var newRange: ComplexNumberRange = history_.popLastRange
      if (range == newRange) {
        newRange = history_.popLastRange
      }
      processRange(newRange)
    }
  }

  private def processRange(rang: ComplexNumberRange) {
    range = rang
    restartRequested = true
  }

  def isParallelized: Boolean = parallelize //parallelizer_.getNumThreads > 1

  def setParallelized(parallelized: Boolean) {
    if (parallelized != parallelize) {
      parallelize = parallelized
      model.setCurrentRow(0)
    }
  }

  def getMaxIterations: Int = maxIterations_

  def setMaxIterations(value: Int) {
    if (value != maxIterations_) {
      maxIterations_ = value
      model.setCurrentRow(0)
    }
  }

  def getUseRunLengthOptimization: Boolean = rowCalculator_.getUseRunLengthOptimization
  def setUseRunLengthOptimization(value: Boolean) = rowCalculator_.setUseRunLengthOptimization(value)
  def getModel: FractalModel = model

  /** @param timeStep number of rows to compute on this timestep.
    * @return true when done computing whole model.
    */
  def timeStep(timeStep: Double): Boolean = {
    if (restartRequested) {
      model.setCurrentRow(0)
      restartRequested = false
    }
    if (model.isDone) {
      showProfileInfoWhenFinished()
      return true
    }
    val numProcs: Int = Runtime.getRuntime.availableProcessors
    var workers: List[Worker] = List()
    var currentRow: Int = model.getCurrentRow
    startProfileTimeIfNeeded(currentRow)
    val height: Int = model.getHeight
    val computeToRow: Int = Math.min(height, currentRow + timeStep.toInt * numProcs)
    val diff: Int = computeToRow - currentRow
    if (diff == 0) return true
    val chunk: Int = Math.max(1, diff / numProcs)

    var i: Int = 0
    while (i < numProcs) {
        val nextRow: Int = Math.min(height, currentRow + chunk)
        workers = new Worker(currentRow, nextRow) :: workers
        currentRow = nextRow
        i += 1
    }

    if (parallelize)
      workers.par.foreach(x => x.run())
    else
      workers.foreach(x => x.run())

    model.setCurrentRow(currentRow)
    false
  }

  /** @return a number between 0 and 1.
    *   Typically corresponds to the number times we had to iterate before the point escaped (or not).
    */
  def getFractalValue(seed: ComplexNumber): Double

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
    new ComplexNumberRange(firstCorner, secondCorner)
  }

  def getRange = range

  private def startProfileTimeIfNeeded(currentRow: Int) {
    if (currentRow == 0) {
      Profiler.getInstance.startCalculationTime()
      wasDone = false
    }
  }

  private def showProfileInfoWhenFinished() {
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
    private val fromRow_ : Int = fromRow
    private val toRow_ : Int = toRow

    def run() = computeChunk(fromRow_, toRow_)

    /** Do a chunk of work (i.e. compute the specified rows) */
    private def computeChunk(fromRow: Int, toRow: Int) {
      val width: Int = model.getWidth

      var y: Int = fromRow
      while (y < toRow) {
          rowCalculator_.calculateRow(width, y)
          y += 1
      }
    }
  }
}
