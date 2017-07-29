/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import com.barrybecker4.common.concurrency.RunnableParallelizer
import com.barrybecker4.simulation.reactiondiffusion1.RDProfiler
import java.awt._
import java.util


/**
  * Makes the GrayScott algorithm run concurrently if setParallelized is set to true.
  * Primary purpose of this class is to handle breaking the algorithm up into concurrent worker threads.
  *
  * Here are some parallelism results using my Core2Duo 6400 (and later i7 2600k) using fixed size.
  * Without parallelism  8.62 fps
  * With parallelism (but not borders) 10.16 fps
  * With parallelism (and borders in sep thread) 10.36 fps
  * After more tuning 18 fps (num steps per frame = 10)
  *
  * Using offscreen rendering slowed things by about 10%
  * These numbers are with Hyper-threading off. The difference compared to hyper-threading off is barely 10%.
  *
  * pr/ns  pr/sync  npr/ns  npr/synch
  * ------- -------  -------  -------
  * parallel calc       | 23.8     21.1    20.9    20.5
  * n-par calc          | 19.0     17.1            17.0
  * n-par calc/offscreen|                  12.8    12.9
  * par calc/offscreen  | 17.2     14.2    14.3    14.1
  *
  * pr/ns : parallel rendering/ no synchronized
  * npr/ns : no parallel rendering no synchronization.
  * Parallel rendering without synchronization is fast, but has bad rendering artifacts.
  *
  * Made some more improvements
  *   - upgraded to ci7 2600k with 4 cores and 8 threads (hyper-threaded).
  *   - fixed a bug in Model.commit where I was using arrayCopy instead of just a pointer swap.
  *   - Modified parallel rendering code so that I compute images and write them quickly
  * rather than drawing individual points which needed to be synchronized (set color, then draw point)
  * Notes
  *   - The difference between onscreen and offscreen rendering seems negligible.
  *   - Getting really great CPU utilization of cores - somewhere around 85%.
  *   - The temperature of the CPU really heats up. Saw max temp of 76C.
  *
  * par rend          non-par rendering
  * ------------       --------------
  * parallel calc       |   180 fps             78 fps
  * n-par calc          |   102 fps             66 fps
  *
  * For larger rectangle than fixed the performance increases seem even better
  *
  * par rend          non-par rendering
  * ------------       --------------
  * parallel calc       |   19.5 fps            8.1 fps
  * n-par calc          |   13.2 fps            6.8 fps
  *
  * @author Barry Becker
  */
object GrayScottController {
  /** default values for constants. */
  val H0 = 0.01
}

/**
  * Constructor
  *
  * @param width  width of computational space.
  * @param height height of computational space.
  */
final class GrayScottController(val width: Int, val height: Int) {
  val model: GrayScottModel = new GrayScottModel(width, height)
  val algorithm = new GrayScottAlgorithm(model)
  setParallelized(true)
  /** Manages the worker threads. */
  private var parallelizer: RunnableParallelizer = _

  /** null if no new size has been requested. */
  private var requestedNewSize: Dimension = _

  def getModel: GrayScottModel = model

  /**
    * doesn't change the size immediately since running threads may
    * be using the current array. We wait until the current timeStep completes
    * before reinitializing with the new size.
    */
  def setSize(width: Int, height: Int) {
    requestedNewSize = new Dimension(width, height)
  }

  def reset() {
    algorithm.setH(GrayScottController.H0)
    model.resetState()
  }

  def setH(h: Double) { algorithm.setH(h) }

  /**
    * Set this to true if you want to run the version
    * that will partition the task of computing the next timeStop
    * into smaller pieces that can be run on different threads.
    * This should speed things up on a multi-core computer.
    */
  def setParallelized(parallelized: Boolean): Unit = {
    parallelizer = if (parallelized) new RunnableParallelizer
    else new RunnableParallelizer(1)
  }

  def isParallelized: Boolean = parallelizer.getNumThreads > 1

  /**
    * Advance one time step increment.
    * u and v are calculated based on tmpU and tmpV, then the result is committed to tmpU and tmpV.
    *
    * @param dt time step in seconds.
    */
  def timeStep(dt: Double): Unit = {
    val numThreads = parallelizer.getNumThreads
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
    val edgeWorker = new Runnable() {
      override def run(): Unit = {
        algorithm.computeNewEdgeValues(dt)
      }
    }
    workers(numThreads) = edgeWorker
    // blocks until all Callables are done running.
    //parallelizer.invokeAllRunnables(workers)
    workers.par.foreach(w => w.run())

    prof.stopConcurrentCalculationTime()
    model.commitChanges()
    if (requestedNewSize != null) {
      model.setSize(requestedNewSize)
      requestedNewSize = null
      reset()
    }
  }

  /**  Runs one of the chunks. */
  private class Worker(var minX: Int, var maxX: Int, var dt: Double) extends Runnable {
    override def run() {
      algorithm.computeNextTimeStep(minX, maxX, dt)
    }
  }
}
