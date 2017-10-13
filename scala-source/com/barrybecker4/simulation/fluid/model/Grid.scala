// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import com.barrybecker4.simulation.common1.RectangularModel
import com.barrybecker4.simulation.fluid.model.CellProperty.CellProperty
import com.barrybecker4.simulation.fluid.model.Boundary.Boundary

/**
  * Data behind the Fluid.
  * @author Barry Becker
  */
class Grid(var dimX: Int, var dimY: Int) extends RectangularModel {

  private val grid0 = new CellGrid(dimX, dimY)
  private val grid1 = new CellGrid(dimX, dimY)

  grid1.addInitialInkDensity()

  override def getWidth: Int = dimX
  override def getHeight: Int = dimY

  private[model] def getGrid0 = grid0
  private[model] def getGrid1 = grid1

  def getU(i: Int, j: Int): Double = grid1.u(i)(j)
  def getV(i: Int, j: Int): Double = grid1.v(i)(j)
  def getDensity(i: Int, j: Int): Double = grid1.density(i)(j)

  /** used for rendering. */
  override def getValue(i: Int, j: Int): Double = getDensity(i, j)
  override def getCurrentRow: Int = getHeight
  override def getLastRow = 0

  /** Swap x[0] and x[1] arrays */
  def swap(prop: CellProperty) {
    val temp = grid0.getProperty(prop)
    grid0.setProperty(prop, grid1.getProperty(prop))
    grid1.setProperty(prop, temp)
  }

  def incrementU(i: Int, j: Int, value: Double) { grid0.u(i)(j) += value }
  def incrementV(i: Int, j: Int, value: Double) { grid0.v(i)(j) += value }
  def incrementDensity(i: Int, j: Int, value: Double) { grid0.density(i)(j) += value }

  /** Set a boundary to contain the liquid. */
  def setBoundary(boundary: Boundary, x: TwoDArray): Unit = {
    for (i <- 1 to dimX) {
      x(i)(0) = if (boundary eq Boundary.HORIZONTAL) -x(i)(1)
      else x(i)(1)
      x(i)(dimY + 1) = if (boundary eq Boundary.HORIZONTAL) -x(i)(dimY)
      else x(i)(dimY)
    }
    for (i <- 1 to dimY) {
      x(0)(i) = if (boundary eq Boundary.VERTICAL) -x(1)(i)
      else x(1)(i)
      x(dimX + 1)(i) = if (boundary eq Boundary.VERTICAL) -x(dimX)(i)
      else x(dimX)(i)
    }
    x(0)(0) = 0.5f * (x(1)(0) + x(0)(1))
    x(0)(dimY + 1) = 0.5f * (x(1)(dimY + 1) + x(0)(dimY))
    x(dimX + 1)(0) = 0.5f * (x(dimX)(0) + x(dimX + 1)(1))
    x(dimX + 1)(dimY + 1) = 0.5f * (x(dimX)(dimY + 1) + x(dimX + 1)(dimY))
  }
}
