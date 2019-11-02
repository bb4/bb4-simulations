// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import CellBlock._


/**
  * A 3x3 block of cells for testing purposes.
  * There is a layer of obstacle cells surrounding the 9 in the middle.
  *
  * %98;  positive y direction.
  * |
  * c(-1, -1)  |    c(0, -1) | c(0, -1)
  * -------------------------------------
  * c(-1, 0)  |    c(0, 0)   | c(0, 0)   --->  Positive x
  * -------------------------------------
  * c(-1, 1)  |    c(0, 1)   |  c(0, 1)
  *
  * @author Barry Becker
  */
object CellBlock {
  private val DIM = 5
}

class CellBlock() {
  private val block: Array[Array[Cell]] = Array.ofDim[Cell](DIM, DIM)

  for (i <- 0 until DIM)
    for (j <- 0 until DIM)
      block(i)(j) = new Cell

  updateCellStatuses()

  /**
    * Gets a cell relative to the center cell position.
    * e.g. get(0,0) returns the center cell.
    * @return cell relative to center of the block
    */
  def get(offsetX: Int, offsetY: Int): Cell = block(offsetX + 2)(offsetY + 2)

  /** @return the cell at the specified position in the array (excluding the outer obstacle cells). */
  def getAbsolute(x: Int, y: Int): Cell = block(x + 1)(y + 1)

  def setPressures(p: Double): Unit = {
    for (i <- 0 until DIM)
      for (j <- 0 until DIM)
        block(i)(j).setPressure(p)
  }

  def setVelocities(u: Double, v: Double): Unit = {
    var i = 0
    for (i <- 0 until DIM)
      for (j <- 0 until DIM)
        block(i)(j).initializeVelocity(u, v)
  }

  /** @param numParticles number of particles to add to each cell in the block. */
  def setCenterCellParticles(numParticles: Int): Unit = {
    setSingleCellParticles(1, 1, numParticles)
    updateCellStatuses()
  }

  /**
    * Set all the cell particles with numParticlesPerCell
    * @param numParticlesPerCell number of particles to add to each cell in the block.
    */
  def setAllCellParticles(numParticlesPerCell: Int): Unit = {
    setCellBlockParticles(1, 1, CellBlock.DIM - 1, CellBlock.DIM - 1, numParticlesPerCell)
  }

  /** set left 6 cells */
  def setLeftCellParticles(numParticlesPerCell: Int): Unit = {
    setCellBlockParticles(1, 1, CellBlock.DIM - 2, CellBlock.DIM - 1, numParticlesPerCell)
  }

  /** set right 6 cells */
  def setRightCellParticles(numParticlesPerCell: Int): Unit = {
    setCellBlockParticles(2, 1, CellBlock.DIM - 1, CellBlock.DIM - 1, numParticlesPerCell)
  }

  /** set top 6 cells */
  def setTopCellParticles(numParticlesPerCell: Int): Unit = {
    setCellBlockParticles(1, 1, CellBlock.DIM - 1, CellBlock.DIM - 2, numParticlesPerCell)
  }

  /** set bottom 6 cells */
  def setBottomCellParticles(numParticlesPerCell: Int): Unit = {
    setCellBlockParticles(1, 2, CellBlock.DIM - 1, CellBlock.DIM - 1, numParticlesPerCell)
  }

  /** @param numParticlesPerCell number of particles to add to each cell in the block.*/
  private def setCellBlockParticles(minX: Int, minY: Int, maxX: Int, maxY: Int, numParticlesPerCell: Int): Unit = {
    for (i <- minX until maxX)
      for (j <- minY until maxY)
        setSingleCellParticles(i, j, numParticlesPerCell)
    updateCellStatuses()
  }

  private def setSingleCellParticles(xpos: Int, ypos: Int, numParticles: Int): Unit = {
    val cell = getAbsolute(xpos, ypos)
    val n = cell.getNumParticles

    for (k <- 0 until n)
      cell.decParticles()
    for (k <- 0 until numParticles)
      cell.incParticles()
  }

  def updateCellStatuses(): Unit = {
    for (i <- 1 until DIM - 2)
      for (j <- 1 until DIM - 2)
        block(i)(j).updateStatus(findNeighbors(i, j))
  }

  def getCenterNeighbors: CellNeighbors = findNeighbors(2, 2)

  def findNeighbors(i: Int, j: Int) =
    new CellNeighbors(block(i + 1)(j), block(i - 1)(j), block(i)(j + 1), block(i)(j - 1))
}

