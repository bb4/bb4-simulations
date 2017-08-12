// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model

import com.barrybecker4.common.math.Range
import java.util.Random


/**
  * Ideas for future work:
  *   - support mouse interaction to raise or lower the height field
  *
  * @author Barry Becker
  */
object Cave {
  private val SEED = 0
  private val RAND = new Random
}

class Cave(val width: Int, val length: Int,
                  var floorThresh: Double = 0.2, var ceilThresh: Double = 0.9) {

  /** a value representing the height. MAX_HEIGHT is wall, MIN_HEIGHT is floor  */
  private var heightMap =  genMap(width, length)

  def getWidth: Int = heightMap.length
  def getLength: Int = heightMap(0).length

  def getRange = new Range(floorThresh, ceilThresh)

  def setValue(x: Int, y: Int, value: Double) {
    heightMap(x)(y) = Math.min(Math.max(value, floorThresh), ceilThresh)
  }

  def createCopy: Cave = {
    val newCave = new Cave(getWidth, getLength, this.floorThresh, this.ceilThresh)
    for (x <- 0 until getWidth) {
      System.arraycopy(heightMap(x), 0, newCave.heightMap(x), 0, getLength)
    }
    newCave
  }

  def getValue(x: Int, y: Int): Double = heightMap(x)(y)

  /**
    * @param amount the amount to change the height by. Will never go above 1 or below 0.
    */
  def incrementHeight(x: Int, y: Int, amount: Double): Unit = {
    val oldVal = heightMap(x)(y)
    heightMap(x)(y) = Math.max(floorThresh, Math.min(ceilThresh, oldVal + amount))
  }

  def setFloorThresh(floor: Double) { this.floorThresh = floor }
  def setCeilThresh(ceil: Double) { this.ceilThresh = ceil }

  private def getChar(x: Int, y: Int) = {
    val v = heightMap(x)(y)
    if (v < floorThresh) ' '
    else if (v < ceilThresh) 'C'
    else 'W'
  }

  /** @return the initial random 2D typeMap data */
  private def genMap(width: Int, length: Int) = {
    Cave.RAND.setSeed(Cave.SEED)
    val map = Array.ofDim[Double](width, length)
    for (x <- 0 until width) {
      for(y <- 0 until length) {
        val r = Cave.RAND.nextDouble
        map(x)(y) = Math.min(Math.max(r, floorThresh), ceilThresh)
      }
    }
    map
  }

  def print() {System.out.println(this.toString)}

  override def toString: String = {
    val bldr = new StringBuilder
    for (y <- 0 until getLength) {
      for (x <- 0 until getWidth) {
        bldr.append(getChar(x, y))
      }
      bldr.append("\n")
    }
    bldr.toString
  }
}
