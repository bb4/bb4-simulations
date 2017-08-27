// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import javax.vecmath.Vector2d
import java.text.DecimalFormat


/**
  * This is the global space containing all the cells, walls, and particles
  * Assumes an M*N grid of cells.
  * X axis increases to the left
  * Y axis increases downwards to be consistent with java graphics.
  * adapted from work by Nick Foster.
  * See
  * http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
  * @author Barry Becker
  */
class Grid(var xDim: Int, var yDim: Int) {

  /** the grid of cells that make up the environment in x,y (col, row) order */
  private var grid = Array.ofDim[Cell](xDim, yDim)

  for (j <- 0 until yDim)
    for (i <- 0 until xDim)
      grid(i)(j) = new Cell

  private val formatter = new DecimalFormat("###0.###")

  def getCell(i: Int, j: Int): Cell = grid(i)(j)
  def getNeighbors(i: Int, j: Int) = new CellNeighbors(grid(i + 1)(j), grid(i - 1)(j), grid(i)(j + 1), grid(i)(j - 1))
  def getXDimension: Int = xDim
  def getYDimension: Int = yDim

  def setVelocity(i: Int, j: Int, velocity: Vector2d): Unit = {
    grid(i)(j).initializeU(velocity.x)
    grid(i)(j).initializeV(velocity.y)
  }

  /**
    * Update the cell status for all the cells in the grid.
    */
  def updateCellStatus(): Unit = {
    for (j <- 1 until yDim - 1)
      for (i <- 1 until xDim - 1)
        grid(i)(j).updateStatus(getNeighbors(i, j))
  }

  /**
    * setup the obstacles.
    */
  def setBoundaries(): Unit = { // right and left
    for (j <- 0 until yDim) {
      grid(0)(j).setStatus(CellStatus.OBSTACLE)
      grid(xDim - 1)(j).setStatus(CellStatus.OBSTACLE)
    }
    // top and bottom
    for (i <- 0 until xDim) {
      grid(i)(0).setStatus(CellStatus.OBSTACLE)
      grid(i)(yDim - 1).setStatus(CellStatus.OBSTACLE)
    }
  }

  /**
    * Set OBSTACLE condition of stationary objects, inflow/outflow.
    */
  def setBoundaryConstraints(): Unit = {
    for (j <- 0 until yDim) {
      var n = grid(1)(j)
      grid(0)(j).setPressure(n.getPressure)
      grid(0)(j).initializeVelocity(0, n.getV) // -n.getV ???

      // right
      n = grid(xDim - 2)(j)
      grid(xDim - 1)(j).setPressure(n.getPressure)
      grid(xDim - 1)(j).initializeVelocity(0, n.getV) // -n.getV()

      grid(xDim - 2)(j).initializeU(0)
    }
    for (i <- 0 until xDim) { // bottom
      var n = grid(i)(1)
      grid(i)(0).setPressure(n.getPressure)
      grid(i)(0).initializeVelocity(n.getU, 0) // -n.getU() ???

      // top
      n = grid(i)(yDim - 2)
      grid(i)(yDim - 1).setPressure(n.getPressure)
      grid(i)(yDim - 1).initializeVelocity(n.getU, 0) // -n.getU()

      grid(i)(yDim - 2).initializeV(0)
    }
  }

  override def toString: String = {
    val bldr = new StringBuilder
    for (j <- yDim - 1 to 0 by -1) {
      for (i <- 0 until xDim) {
        val cell = getCell(i, j)
        bldr.append("    V=" + format(cell.getV))
        bldr.append("    |")
      }
      bldr.append("\n")
      for (i <- 0 until xDim) {
        val cell = getCell(i, j)
        bldr.append("P=" + format(cell.getPressure))
        bldr.append(" U=" + format(cell.getU))
        bldr.append("|")
      }
      bldr.append("\n")
      for (i <- 0 until xDim)
        bldr.append("----------------")
      bldr.append("\n")
    }
    bldr.toString
  }

  private def format(num: Double) = {
    val fmtNum = new StringBuilder(formatter.format(num))
    while ( {
      fmtNum.length < 5
    }) fmtNum.append(' ')
    fmtNum.toString
  }
}
