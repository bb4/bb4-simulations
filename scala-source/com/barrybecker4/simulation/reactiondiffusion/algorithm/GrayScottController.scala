/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import com.barrybecker4.simulation.reactiondiffusion.RDProfiler
import java.awt.Dimension
import scala.collection.parallel.CollectionConverters._
import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration.Initializer


/**
  * Runs the Gray-Scott step: all cells in parallel x-strips when `setParallelized(true)`,
  * then `commitChanges` on the model. Boundaries are fully periodic (no separate edge pass).
  * Oversized `dt` is clamped to [[GrayScottAlgorithm.maxStableDt]].
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
  private var workers: Array[Worker] = Array.empty

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
    * that will partition the task of computing the next time step
    * into smaller pieces that can be run on different threads.
    * This should speed things up on a multi-core computer.
    */
  def setParallelized(parallelized: Boolean): Unit = { this.parallelized = parallelized }
  def isParallelized: Boolean = this.parallelized
  def setInitializer(initializer: Initializer): Unit = model.setInitializer(initializer)

  /** Advance one time step increment.
    * New u/v are calculated from tmpU/tmpV, then buffers are swapped via commitChanges.
    * @param dt time step in seconds (clamped to the explicit Euler stability limit).
    */
  def timeStep(dt: Double): Unit = {
    val safeDt = math.min(dt, GrayScottAlgorithm.maxStableDt(algorithm.getH))
    val numThreads = math.max(1, Runtime.getRuntime.availableProcessors())
    ensureWorkers(numThreads)
    val ranges = StripPartition.ranges(model.getWidth, numThreads)
    val prof = RDProfiler.getInstance
    prof.startConcurrentCalculationTime()
    var i = 0
    while (i < ranges.length) {
      val (minX, maxX) = ranges(i)
      workers(i).setRange(minX, maxX, safeDt)
      i += 1
    }
    val active = if (workers.length == ranges.length) workers else workers.take(ranges.length)
    if (parallelized) active.par.foreach(_.run())
    else active.foreach(_.run())

    prof.stopConcurrentCalculationTime()
    model.commitChanges()
    if (requestedNewSize.isDefined) {
      // setSize reallocates u/v in sync with width/height (avoids paint AIOOBE on resize).
      // Preserves current H; F/K preserved by size-only reallocate.
      model.setSize(requestedNewSize.get)
      requestedNewSize = None
    }
  }

  private def ensureWorkers(n: Int): Unit = {
    if (workers.length != n) {
      workers = Array.tabulate(n)(_ => new Worker)
    }
  }

  /** Runs one column strip `[minX, maxX)`. */
  private final class Worker extends Runnable {
    private var minX = 0
    private var maxX = 0
    private var dt = 0.0

    def setRange(minX: Int, maxX: Int, dt: Double): Unit = {
      this.minX = minX
      this.maxX = maxX
      this.dt = dt
    }

    override def run(): Unit = {
      algorithm.computeNextTimeStep(minX, maxX, dt)
    }
  }
}
