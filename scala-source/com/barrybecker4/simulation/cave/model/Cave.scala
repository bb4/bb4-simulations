// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model

import com.barrybecker4.math.Range
import scala.util.Random

/**
  * Data structure for representing an evolving cave system.
  * @author Barry Becker
  */
object Cave {
  private val RAND = new Random
  RAND.setSeed(0)

  /** Reseed shared RNG used by caves that omit an explicit [[scala.util.Random]] (tests, reproducible worlds). */
  def reseedRandom(seed: Long): Unit = RAND.setSeed(seed)
}

/**
  *
  * @param width width of the cave area
  * @param length length of the cave area
  * @param floorThresh values below this are in the floor
  * @param ceilThresh values above this are in the ceiling
  */
class Cave(val width: Int, val length: Int,
           var floorThresh: Double = 0.2, var ceilThresh: Double = 0.9, rnd: Random = Cave.RAND) {

  /** a value representing the height. MAX_HEIGHT is wall, MIN_HEIGHT is floor  */
  private val heightMap = Array.ofDim[Double](width, length)

  def getWidth: Int = heightMap.length
  def getLength: Int = heightMap(0).length

  def getRange: Range = Range(floorThresh, ceilThresh)

  randomInitialization()

  private def clamp(value: Double): Double =
    math.max(floorThresh, math.min(ceilThresh, value))

  def setValue(x: Int, y: Int, value: Double): Unit = {
    heightMap(x)(y) = clamp(value)
  }

  /** @return the initial random 2D typeMap data */
  def randomInitialization(): Unit = {
    for (x <- 0 until width) {
      for (y <- 0 until length) {
        val r = rnd.nextDouble()
        heightMap(x)(y) = clamp(r)
      }
    }
  }

  def createCopy: Cave = {
    val newCave = new Cave(getWidth, getLength, this.floorThresh, this.ceilThresh)
    for (x <- heightMap.indices) {
      System.arraycopy(heightMap(x), 0, newCave.heightMap(x), 0, getLength)
    }
    newCave
  }

  def getValue(x: Int, y: Int): Double = heightMap(x)(y)

  /** @param amount the amount to change the height by. Will never go above 1 or below 0. */
  def incrementHeight(x: Int, y: Int, amount: Double): Unit = {
    val oldVal = heightMap(x)(y)
    heightMap(x)(y) = clamp(oldVal + amount)
  }

  def setFloorThresh(floor: Double): Unit = { this.floorThresh = floor }
  def setCeilThresh(ceil: Double): Unit = { this.ceilThresh = ceil }

  /** @return a character representing the cave type at a specific location. Either wall, floor, or ceiling. */
  private def getChar(x: Int, y: Int): Char = {
    val v = heightMap(x)(y)
    if (v <= floorThresh) ' '
    else if (v >= ceilThresh) 'C'
    else 'W'
  }

  def print(): Unit = println(this.toString)

  override def toString: String =
    (0 until getLength)
      .map { y => (0 until getWidth).map(x => getChar(x, y)).mkString }
      .mkString("", "\n", "\n")
}
