// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model

import com.barrybecker4.math.Range
import com.barrybecker4.simulation.cave.model.kernel.AbstractKernel
import com.barrybecker4.simulation.cave.model.kernel.BasicKernel
import com.barrybecker4.simulation.cave.model.kernel.Kernel
import com.barrybecker4.simulation.cave.model.kernel.RadialKernel
import com.barrybecker4.simulation.common.rendering.bumps.HeightField
import CaveProcessor._
import CaveProcessor.KernelType
import scala.collection.parallel.CollectionConverters.*
import scala.compiletime.uninitialized

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

  enum KernelType:
    case BASIC, RADIAL3, RADIAL5, RADIAL7, RADIAL9, RADIAL11, RADIAL13, RADIAL15, RADIAL17, RADIAL19

  val DEFAULT_KERNEL_TYPE: KernelType = KernelType.RADIAL9

  private def radialSize(kt: KernelType): Int =
    kt.toString.stripPrefix("RADIAL").toInt
}

class CaveProcessor(val width: Int, val height: Int, val floorThresh: Double,
                    val ceilThresh: Double, var lossFactor: Double, var effectFactor: Double,
                    val initialKernelType: KernelType, var useParallel: Boolean = true)
    extends HeightField {

  private var cave = new Cave(width, height, floorThresh, ceilThresh)
  private var kernel: Kernel = uninitialized

  setKernelType(initialKernelType)

  /** Constructor that allows you to specify the dimensions of the cave */
  def this(width: Int, height: Int) = {
    this(width, height, DEFAULT_FLOOR_THRESH, DEFAULT_CEIL_THRESH, DEFAULT_LOSS_FACTOR, DEFAULT_EFFECT_FACTOR,
      KernelType.BASIC, DEFAULT_USE_PARALLEL)
  }

  /** Default no argument constructor */
  def this() = {
    this(CaveProcessor.DEFAULT_WIDTH, CaveProcessor.DEFAULT_HEIGHT)
  }

  override def getWidth: Int = cave.getWidth
  override def getHeight: Int = cave.getLength

  def setKernelType(kernelType: KernelType): Unit =
    kernel = kernelType match
      case KernelType.BASIC => new BasicKernel(cave)
      case radial => new RadialKernel(cave, CaveProcessor.radialSize(radial))

  def setLossFactor(loss: Double): Unit = { lossFactor = loss }
  def setEffectFactor(scale: Double): Unit = { effectFactor = scale }
  def setFloorThresh(floor: Double): Unit = { cave.setFloorThresh(floor) }
  def setCeilThresh(ceil: Double): Unit = { cave.setCeilThresh(ceil) }
  def incrementHeight(x: Int, y: Int, amount: Double): Unit = cave.incrementHeight(x, y, amount)

  override def getValue(x: Int, y: Int): Double = cave.getValue(x, y)
  def getRange: Range = cave.getRange
  def printCave(): Unit = cave.print()

  override def toString: String = cave.toString

  def setUseParallel(parallelized: Boolean): Unit = {
    useParallel = parallelized
  }

  /**
    * Compute the next step of the simulation
    * The new value is at each point based on simulation rules:
    * - if a cell is alive but has too few neighbors, kill it.
    * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
    */
  def nextPhase(): Unit = {
    val newCave = cave.createCopy
    val xs = 0 until cave.getWidth
    val cols = if useParallel then xs.par else xs
    cols.iterator.foreach { x =>
      for y <- 0 until cave.getLength do
        val neibNum = kernel.countNeighbors(x, y)
        val oldValue = cave.getValue(x, y)
        val newValue = oldValue + (neibNum - lossFactor) * effectFactor
        newCave.setValue(x, y, newValue)
    }
    cave = newCave
    kernel match {
      case k: AbstractKernel => k.cave = cave
    }
  }
}
