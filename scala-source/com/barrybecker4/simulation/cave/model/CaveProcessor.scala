// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model

import com.barrybecker4.common.concurrency.RunnableParallelizer
import com.barrybecker4.common.math.Range
import com.barrybecker4.simulation.cave.model.kernal.BasicKernel
import com.barrybecker4.simulation.cave.model.kernal.Kernel
import com.barrybecker4.simulation.cave.model.kernal.RadialKernel
import com.barrybecker4.simulation.common.rendering.bumps.HeightField
import java.util
import CaveProcessor._
import CaveProcessor.KernelType.KernelType

/**
  * This Cave simulation program is based on work by Michael Cook
  * See https://gamedevelopment.tutsplus.com/tutorials/generate-random-cave-levels-using-cellular-automata--gamedev-9664
  * See http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
  *
  * Using concurrency in 4 core (8 thread) processor increases speed from 3.7 fps to about 6.6 fps.
  * But much of that is because of rendering time. We could use parallel rendering to make that faster too.
  *
  * @author Brian Becker
  * @author Barry Becker
  */
object CaveProcessor {
  /** The density is the chance that a cell starts as part of the cave area (alive) */
  val DEFAULT_FLOOR_THRESH = .2
  val DEFAULT_CEIL_THRESH = .8
  private val DEFAULT_HEIGHT = 32
  private val DEFAULT_WIDTH = 32
  /** cells die if less than this */
  val DEFAULT_LOSS_FACTOR = 0.5
  val DEFAULT_USE_PARALLEL = true
  /** Cells are born if more than this many neighbors */
  val DEFAULT_EFFECT_FACTOR = 0.2

  object KernelType extends Enumeration {
    type KernelType = Value
    val BASIC, RADIAL3, RADIAL5, RADIAL7, RADIAL9, RADIAL11, RADIAL13, RADIAL15, RADIAL17, RADIAL19 = Value
  }

  val DEFAULT_KERNEL_TYPE = KernelType.RADIAL9

  def main(args: Array[String]): Unit = {
    val cave = new CaveProcessor(32, 32, 0.25, 0.8, 3, 2, KernelType.BASIC, DEFAULT_USE_PARALLEL)
    cave.printCave()
    cave.nextPhase()
    cave.printCave()
  }
}

class CaveProcessor(val width: Int, val height: Int, val floorThresh: Double,
                    val ceilThresh: Double, var lossFactor: Double, var effectFactor: Double,
                    val kernelType: KernelType, val useParallel: Boolean) extends HeightField {
  private var cave = new Cave(width, height, floorThresh, ceilThresh)
  setKernelType(kernelType)
  setUseParallel(useParallel)

  private var kernel: Kernel = _
  /** Manages the worker threads. */
  private var parallelizer: RunnableParallelizer = _

  /** Constructor that allows you to specify the dimensions of the cave */
  def this(width: Int, height: Int) {
    this(width, height, DEFAULT_FLOOR_THRESH, DEFAULT_CEIL_THRESH, DEFAULT_LOSS_FACTOR, DEFAULT_EFFECT_FACTOR,
      KernelType.BASIC, DEFAULT_USE_PARALLEL)
  }

  /** Default no argument constructor */
  def this() {
    this(CaveProcessor.DEFAULT_WIDTH, CaveProcessor.DEFAULT_HEIGHT)
  }

  override def getWidth: Int = cave.getWidth
  override def getHeight: Int = cave.getLength

  def setKernelType(`type`: KernelType): Unit = {
    `type` match {
      case KernelType.BASIC => kernel = new BasicKernel(cave)
      case KernelType.RADIAL3 => kernel = new RadialKernel(cave, 3)
      case KernelType.RADIAL5 => kernel = new RadialKernel(cave, 5)
      case KernelType.RADIAL7 => kernel = new RadialKernel(cave, 7)
      case KernelType.RADIAL9 => kernel = new RadialKernel(cave, 9)
      case KernelType.RADIAL11 => kernel = new RadialKernel(cave, 11)
      case KernelType.RADIAL13 => kernel = new RadialKernel(cave, 13)
      case KernelType.RADIAL15 => kernel = new RadialKernel(cave, 15)
      case KernelType.RADIAL17 => kernel = new RadialKernel(cave, 17)
      case KernelType.RADIAL19 => kernel = new RadialKernel(cave, 19)
    }
  }

  def setLossFactor(loss: Double) { lossFactor = loss}
  def setEffectFactor(scale: Double) { effectFactor = scale}
  def setFloorThresh(floor: Double) { cave.setFloorThresh(floor)}
  def setCeilThresh(ceil: Double){ cave.setCeilThresh(ceil) }
  def incrementHeight(x: Int, y: Int, amount: Double) { cave.incrementHeight(x, y, amount) }

  def setUseParallel(parallelized: Boolean) {
    parallelizer = if (parallelized) new RunnableParallelizer
    else new RunnableParallelizer(1)
  }

  /**
    * Compute the next step of the simulation
    * The new value is at each point based on simulation rules:
    * - if a cell is alive but has too few neighbors, kill it.
    * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
    */
  def nextPhase() {
    val newCave = cave.createCopy
    val numThreads = parallelizer.getNumThreads
    val workers = new util.ArrayList[Runnable](numThreads + 1)
    val range = cave.getWidth / numThreads
    for (i <- 0 until numThreads) {
      val offset = i * range
      workers.add(new Worker(offset, offset + range, newCave))
    }
    // blocks until all Callables are done running.
    parallelizer.invokeAllRunnables(workers)
    cave = newCave
  }

  def nextPhase(minX: Int, maxX: Int, newCave: Cave) { // Loop over each row and column of the map
    for (x <- minX until maxX) {
      for (y <- 0 until cave.length) {
        val neibNum = kernel.countNeighbors(x, y)
        val oldValue = cave.getValue(x, y)
        val newValue = oldValue + (neibNum - lossFactor) * effectFactor
        newCave.setValue(x, y, newValue)
      }
    }
    cave = newCave
  }

  /** Runs one of the chunks. */
  private class Worker(var minX: Int, var maxX: Int, var newCave: Cave) extends Runnable {
    override def run() {nextPhase(minX, maxX, newCave) }
  }

  override def getValue(x: Int, y: Int): Double = cave.getValue(x, y)

  def getRange: Range = cave.getRange
  def printCave() {cave.print() }

  override def toString: String = cave.toString
}
