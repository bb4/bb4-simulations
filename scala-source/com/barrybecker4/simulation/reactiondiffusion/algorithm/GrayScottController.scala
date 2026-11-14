/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import com.barrybecker4.simulation.reactiondiffusion.RDProfiler
import java.awt.Dimension
import scala.collection.parallel.CollectionConverters._
import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration.Initializer


/**
  * Runs the Gray-Scott step: all cells in parallel x-strips when `setParallelized(true)`,
  * then `commitChanges` on the model. Boundaries are fully periodic (no separate edge pass).
  *
  * @param initialWidth  width of computational space.
  * @param initialHeight height of computational space.
  */
final class GrayScottController(initialWidth: Int, initialHeight: Int) {
  val model: GrayScottModel = new GrayScottModel(initialWidth, initialHeight)
  val algorithm = new GrayScottAlgorithm(model)
  algorithm.setH(GrayScottModel.H0)
  setParallelized(true)

  /** None if no new size has been requested. */
  private var requestedNewSize: Option[Dimension] = None
  private var parallelized = true
  def getModel: GrayScottModel = model

  /** doesn't change the size immediately since running threads may
    * be using the current array. Wait until the current timeStep completes
    * before reinitializing with the new size.
    */
  def setSize(width: Int, height: Int): Unit = {
    requestedNewSize = Some(new Dimension(width, height))
  }

  def reset(): Unit = {
    algorithm.setH(GrayScottModel.H0)
    model.resetState()
  }

  def setH(h: Double): Unit = { algorithm.setH(h) }

  /** Set this to true if you want to run the version
    * that will partition the task of computing the next timeStop
    * into smaller pieces that can be run on different threads.
    * This should speed things up on a multi-core computer.
    */
  def setParallelized(parallelized: Boolean): Unit = { this.parallelized = parallelized }
  def isParallelized: Boolean = this.parallelized
  def setInitializer(initializer: Initializer): Unit = model.setInitializer(initializer)

  /** Advance one time step increment.
    * New u/v are calculated from tmpU/tmpV, then buffers are swapped via commitChanges.
    * @param dt time step in seconds.
    */
  def timeStep(dt: Double): Unit = {
    val numThreads = math.max(1, Runtime.getRuntime.availableProcessors())
    val workers = Array.ofDim[Runnable](numThreads)
    val width = model.getWidth
    val range = width / numThreads
    val prof = RDProfiler.getInstance
    prof.startConcurrentCalculationTime()
    var i = 0
    while (i < numThreads - 1) {
      val offset = i * range
      workers(i) = new Worker(offset, offset + range, dt)
      i += 1
    }
    workers(numThreads - 1) = new Worker(range * (numThreads - 1), width, dt)

    if (parallelized) workers.par.foreach(w => w.run())
    else workers.foreach(w => w.run())

    prof.stopConcurrentCalculationTime()
    model.commitChanges()
    if (requestedNewSize.isDefined) {
      // setSize reallocates u/v in sync with width/height (avoids paint AIOOBE on resize).
      // Preserves current H; F/K preserved by size-only reallocate.
      model.setSize(requestedNewSize.get)
      requestedNewSize = None
    }
  }

  /** Runs one column strip `[minX, maxX)`. */
  private class Worker(minX: Int, maxX: Int, dt: Double) extends Runnable {
    override def run(): Unit = {
      algorithm.computeNextTimeStep(minX, maxX, dt)
    }
  }
}
