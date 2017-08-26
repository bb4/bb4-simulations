// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model


/**
  * The cells directly neighboring a given cell.
  * @param cellXp1 cell to the right
  * @param cellXm1 cell to the left
  * @param cellYp1 cell above
  * @param cellYm1 cell below
  * @author Barry Becker
  */
class CellNeighbors(var cellXp1: Cell, var cellXm1: Cell, var cellYp1: Cell, var cellYm1: Cell) {

  def getLeft: Cell = cellXm1  // neighbor cell to the left
  def getRight: Cell = cellXp1 // neighbor cell to the right
  def getBottom: Cell = cellYm1 // neighbor cell to the top
  def getTop: Cell = cellYp1  // neighbor cell to the bottom.

  /** @return true if all the neighbors have at least one particle*/
  private[model] def allHaveParticles =
    getRight.getNumParticles > 0 && getLeft.getNumParticles > 0 &&
      getBottom.getNumParticles > 0 && getTop.getNumParticles > 0

  /** @return false if none of the neighbors have any particles.*/
  private[model] def noneHaveParticles =
    getRight.getNumParticles == 0 && getLeft.getNumParticles == 0 &&
      getBottom.getNumParticles == 0 && getTop.getNumParticles == 0
}