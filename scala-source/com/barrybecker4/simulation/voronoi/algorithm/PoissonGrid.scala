// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import javax.vecmath.Point2d
import scala.util.Random
import PoissonGrid.RND

object PoissonGrid {
  private val RND = new Random(0)
}

/**
  * The cell size of the Poison grid will be rad / sqrt(2). Two because its in 2 dimensions.
  * See https://www.cs.ubc.ca/~rbridson/docs/bridson-siggraph07-poissondisk.pdf
  */
case class PoissonGrid(width: Double, height: Double, radius: Double, rnd: Random = RND) {
  private val cellSize: Double = radius / Math.sqrt(2)
  private val xBins: Int = (width / cellSize + 1).toInt
  private val yBins: Int = (height / cellSize + 1).toInt
  private val grid: Array[Array[Int]] = Array.fill(xBins, yBins)(-1)
  var samples: IndexedSeq[Point2d] = IndexedSeq()
  println("created grid of size " + xBins + " " + yBins + " r=" + radius + " cellSize=" + cellSize)

  def getSampleIndex(x: Double, y:Double): Int = grid(getIdx(x))(getIdx(y))
  def getPoint(index: Int): Point2d = samples(index)
  def getNumSamples: Int = samples.length

  def addSample(point: Point2d): Int = {
    samples :+= point
    val xIdx = getIdx(point.x)
    val yIdx = getIdx(point.y)
    assert(grid(xIdx)(yIdx) == -1, "There is already a value at " + xIdx + " "  + yIdx)
    grid(xIdx)(yIdx) = samples.length - 1
    samples.length - 1
  }

  def addNewElementIfPossible(index: Int, k: Int): Int = {
    val point = samples(index)

    // try k random neighboring points, if none accepted, return -1
    for (i <- 0 until k) {
      val nbrPoint = getRandomNeighborOf(point)
      val distantEnough = isDistantFromAllNeighbors(nbrPoint)
      if (distantEnough) {
        //println("found dist nbr " + nbrPoint + " after " + i + " tries")
        return addSample(nbrPoint)
      }
    }
    //println("no nbr found")
    -1
  }

  private def getIdx(d: Double): Int = (d / cellSize).toInt

  private def getRandomNeighborOf(point: Point2d): Point2d = {
    var x: Double = -1
    var y: Double = -1
    var ct = 0
    while (x < 0 || y < 0 || x >= width - 1.0 || y >= height - 1.0) {
      val randomRadius = radius + rnd.nextDouble() * radius
      val randomAngle = rnd.nextDouble() * Math.PI * 2
      x = point.x + randomRadius * Math.cos(randomAngle)
      y = point.y + randomRadius * Math.sin(randomAngle)
      ct += 1
    }
    if (ct > 3) println("Warning ct = " + ct)
    new Point2d(x, y)
  }

  // Look in all neighboring grid cells to see if anything within rad distance
  private def isDistantFromAllNeighbors(point: Point2d): Boolean = {
    val xIdx = getIdx(point.x)
    val yIdx = getIdx(point.y)
    val xMin = Math.max(xIdx - 2, 0)
    val xMax = Math.min(xIdx + 2, xBins - 1)
    val yMin = Math.max(yIdx - 2, 0)
    val yMax = Math.min(yIdx + 2, yBins - 1)
    for (xi <- xMin to xMax)
      for (yi <- yMin to yMax)
        val sampleIndex = grid(xi)(yi)
        if (xi == xIdx && yi == yIdx && sampleIndex >= 0) {
          return false
        }
        else {
          if (sampleIndex >= 0) {
            val samplePoint = samples(sampleIndex)
            if (point.distance(samplePoint) < radius) {
              return false
            }
          }
        }
    true
  }
}
