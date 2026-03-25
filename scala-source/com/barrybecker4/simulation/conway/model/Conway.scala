// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location

/**
  * Sparse storage for Conway's Game of Life: only live cells are stored.
  * @author Barry Becker
  */
object Conway {
  private val NbrOffsets: Array[Location] = Array(
    IntLocation(-1, -1),
    IntLocation(-1, 0),
    IntLocation(-1, 1),
    IntLocation(0, -1),
    IntLocation(0, 1),
    IntLocation(1, -1),
    IntLocation(1, 0),
    IntLocation(1, 1)
  )
}

/** Since it is on an infinite grid, only store locations where there is life. */
class Conway private[model]() {

  private var points = Map.empty[Location, Int]
  private var wrap = false
  private var width = -1
  private var height = -1

  private[model] def setWrapping(wrap: Boolean, width: Int, height: Int): Unit = {
    this.wrap = wrap
    this.width = width
    this.height = height
  }

  def initialize(): Unit = addGlider()

  def getCandidates: Set[Location] =
    points.keysIterator.flatMap { c =>
      Iterator(keepInBounds(c)) ++
        Conway.NbrOffsets.iterator.map(o => keepInBounds(c.incrementOnCopy(o)))
    }.toSet

  /** @return the point constrained to the torus when wrap is true and dimensions are valid. */
  private def keepInBounds(c: Location): Location =
    if wrap && width > 0 && height > 0 then
      IntLocation(
        Math.floorMod(c.row, height),
        Math.floorMod(c.col, width)
      )
    else c

  private[model] def getPoints: Set[Location] = points.keySet

  /** Clears live cells (for tests or resetting an initial pattern). */
  private[model] def clearForTesting(): Unit =
    points = Map.empty

  def isAlive(coord: Location): Boolean = points.contains(coord)

  def getNumNeighbors(c: Location): Int =
    Conway.NbrOffsets.count(o => isAlive(keepInBounds(c.incrementOnCopy(o))))

  /**
    * Sets the cell age (positive) or clears the cell (value <= 0 removes the key).
    * Synchronized so parallel rule application does not lose updates on the new generation grid.
    */
  def setValue(coord: Location, value: Int): Unit = synchronized {
    if value <= 0 then points -= coord
    else points += coord -> value
  }

  def getValue(coord: Location): Int =
    points.getOrElse(coord, 0)

  private def addGlider(): Unit = {
    setValue(IntLocation(10, 10), 1)
    setValue(IntLocation(11, 11), 1)
    setValue(IntLocation(11, 12), 1)
    setValue(IntLocation(10, 12), 1)
    setValue(IntLocation(9, 12), 1)
  }

  override def toString: String = "points = " + points.toString
}
