/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import com.barrybecker4.simulation.reactiondiffusion.RDProfiler
import java.awt.Dimension
import scala.collection.parallel.CollectionConverters._
import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration.Initializer


/**
  * Runs the Gray-Scott step: interior cells in parallel strips when `setParallelized(true)`,
  * periodic edge updates in a separate worker, then `commitChanges` on the model.
  *
  * @param initialWidth  width of computational space.
  * @param initialHeight height of computational space.
  * @author Barry Becker
  */
final class GrayScottController(initialWidth: Int, initialHeight: Int) {
  val model: GrayScottModel = new GrayScottModel(initialWidth, initialHeight)
  val algorithm = new GrayScottAlgorithm(model)
  setParallelized(true)

  /** null if no new size has been requested. */
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
    * u and v are calculated based on tmpU and tmpV, then the result is committed to tmpU and tmpV.
    * @param dt time step in seconds.
    */
  def timeStep(dt: Double): Unit = {
    val numThreads = Runtime.getRuntime.availableProcessors()
    val workers = Array.ofDim[Runnable](numThreads + 1)
    val range = model.getWidth / numThreads
    val prof = RDProfiler.getInstance
    prof.startConcurrentCalculationTime()
    for (i <- 0 until numThreads - 1) {
      val offset = i * range
      workers(i) = new Worker(1 + offset, offset + range, dt)
    }
    val minXEdge = range * (numThreads - 1) + 1
    val maxXEdge = model.getWidth - 2
    workers(numThreads - 1) = new Worker(minXEdge, maxXEdge, dt)
    // also add the border calculations in a separate thread.
    workers(numThreads) = new EdgeWorker(dt)

    if (parallelized) workers.par.foreach(w => w.run())
    else workers.foreach(w => w.run())

    prof.stopConcurrentCalculationTime()
    model.commitChanges()
    if (requestedNewSize.isDefined) {
      model.setSize(requestedNewSize.get)
      requestedNewSize = None
      reset()
    }
  }

  /**  Runs one of the chunks. */
  private class Worker(minX: Int, maxX: Int,  dt: Double) extends Runnable {
    override def run(): Unit = {
      algorithm.computeNextTimeStep(minX, maxX, dt)
    }
  }

  private class EdgeWorker(dt: Double) extends Runnable {
    override def run(): Unit = {
      algorithm.computeNewEdgeValues(dt)
    }
  }
}
