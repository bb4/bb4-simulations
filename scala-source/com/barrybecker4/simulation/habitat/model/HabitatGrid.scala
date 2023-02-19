// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.model

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.habitat.creatures.Creature

import javax.vecmath.Point2d


/** Grid that contains all the creatures.
  * Good performance is achieved by dividing the space up into a grid of cells 
  * where there are a subset of creatures in each cell.
  * @param smellDistance the distance that a creature can smell another creature (0, 1).
  */
case class HabitatGrid(smellDistance: Double) {

  private val xDim = Math.ceil(1.0 / smellDistance).toInt
  private val yDim = xDim
  private val cells = Array.ofDim[Cell](xDim + 1, yDim + 1)


  for (i <- 0 to xDim)
    for (j <- 0 to yDim)
      cells(i)(j) = new Cell(i, j)

  def getXDim: Int = xDim
  def getYDim: Int = yDim

  def getCellForPosition(position: Point2d): Cell = {
    assert(position.x >= 0 && position.x < 1)
    assert(position.y >= 0 && position.y < 1)
    val x = (position.x * xDim).toInt
    val y = (position.y * yDim).toInt
    cells(x)(y)
  }

  def getNeighborCells(cell: Cell): Array[Cell] = {
    val nbrCells = new Array[Cell](9)
    val xm1 = getSafeX(cell.xIndex - 1)
    val xp1 = getSafeX(cell.xIndex + 1)
    val ym1 = getSafeY(cell.yIndex - 1)
    val yp1 = getSafeY(cell.yIndex + 1)
    nbrCells(0) = cells(xm1)(ym1)
    nbrCells(1) = cells(xm1)(cell.yIndex)
    nbrCells(2) = cells(xm1)(yp1)
    nbrCells(3) = cells(cell.xIndex)(ym1)
    nbrCells(4) = cells(cell.xIndex)(yp1)
    nbrCells(5) = cells(xp1)(ym1)
    nbrCells(6) = cells(xp1)(cell.yIndex)
    nbrCells(7) = cells(xp1)(yp1)
    nbrCells(8) = cell
    nbrCells
  }

  def move(oldLocation: Point2d, newLocation: Point2d, creature: Creature): Unit = {
    val oldCell = getCellForPosition(oldLocation)
    val newCell = getCellForPosition(newLocation)
    if (newCell != oldCell) {
      newCell.addCreature(creature)
      oldCell.removeCreature(creature)
    }
  }

  private def getSafeX(xIndex: Int) = (xIndex + xDim) % xDim

  private def getSafeY(yIndex: Int) = (yIndex + yDim) % yDim

}
