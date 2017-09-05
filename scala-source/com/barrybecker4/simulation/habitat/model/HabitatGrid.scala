// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.model

import javax.vecmath.Point2d


/** Grid that contains all the creatures/ */
class HabitatGrid(var xDim: Int, var yDim: Int) {

  private var cells = Array.ofDim[Cell](xDim + 1, yDim + 1)

  for (i <- 0 to xDim)
    for (j <- 0 to yDim)
      cells(i)(j) = new Cell(i, j)

  def getCellForPosition(position: Point2d): Cell = {
    val x = (position.x * xDim).toInt
    val y = (position.y * yDim).toInt
    cells(x)(y)
  }

  def getNeighborCells(cell: Cell): Array[Cell] = {
    val nbrCells = new Array[Cell](8)
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
    nbrCells
  }

  private def getSafeX(xIndex: Int) = Math.abs(xIndex % xDim)
  private def getSafeY(yIndex: Int) = Math.abs(yIndex % yDim)
}
