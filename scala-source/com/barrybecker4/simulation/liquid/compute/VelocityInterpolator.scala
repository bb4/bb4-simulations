// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model.Cell
import com.barrybecker4.simulation.liquid.model.Grid
import com.barrybecker4.simulation.liquid.model.Particle
import javax.vecmath.Point2d
import javax.vecmath.Vector2d


/**
  * Used to interpolate velocities in the grid.
  * @author Barry Becker
  */
class VelocityInterpolator(var grid: Grid) {

  /**
    * Find the velocity of a particle somewhere on the grid.
    * @param particle particle to find velocity for
    * @return the interpolated (weighted) velocity vector for the particle
    */
  def findVelocity(particle: Particle): Vector2d = {
    val i = particle.x.toInt
    val j = particle.y.toInt
    val ii = if ((particle.x - i) > 0.5) i + 1
    else i - 1
    val jj = if ((particle.y - j) > 0.5) j + 1
    else j - 1
    interpolateVelocity(
      particle, grid.getCell(i, j),
      grid.getCell(ii, j),
      grid.getCell(i, jj),
      grid.getCell(i - 1, j),
      grid.getCell(i - 1, jj), // u
      grid.getCell(i, j - 1),
      grid.getCell(ii, j - 1) // v
    )
  }

  /**
    * linearly interpolate the velocity of the particle based on its position
    * relative to 4 neighboring velocity vectors.
    * There are 4 cases: The numbers indicate the parameter
    * case 1: particle in the upper right corner: 6 distinct cells are passed in this pattern
    * 4  2  X
    * 3  c  1
    * X  5  6
    * case 2: particle in upper left
    * 4   2   X
    * 1/3  c   X
    * 6   5   X
    * case 3: lower right.
    * X   X   X
    * 3   c   1
    * 4  2/5  6
    * case 4: particle in lower left.
    * X    X   X
    * 1/3   c   X
    * 4/6 2/5   X
    * RISK: 1
    *
    * @param particle the particle to find the velocity of.
    * @param cell     the central cell that the particle is in.
    * @param cX       either one forward or one back in the x direction
    *                 depending on the position of the particle.   [1]
    * @param cY       either one forward or one back in the y direction
    *                 depending on the position of the particle.    [2]
    * @param cXm1     x - 1  (always the cell to the left)      [3]
    * @param cXm1y    x - 1 and either one forward or one back in the y
    *                 direction depending on the position of the particle. [4]
    * @param cYm1     y - 1  (always the cell to the bottom)       [5]
    * @param cYm1x    y - 1 and either one forward or one back in the x
    *                 direction depending on the position of the particle.  [6]
    * @return the interpolated velocity vector.
    */
  protected def interpolateVelocity(particle: Point2d, cell: Cell, cX: Cell, cY: Cell,
                                    cXm1: Cell, cXm1y: Cell, cYm1: Cell, cYm1x: Cell): Vector2d = {
    assert(!(cell.isObstacle || cell.isEmpty),
      "Error: interpVelocity cell status=" + cell.getStatus + " num particles = " + cell.getNumParticles)
    val x = particle.x - Math.floor(particle.x)
    val y = particle.y - Math.floor(particle.y)
    val xx = if (x > 0.5) 1.5 - x
    else 0.5 + x
    val yy = if (y > 0.5) 1.5 - y
    else 0.5 + y
    val x1 = (1.0 - x) * cXm1.getU + x * cell.getU
    val x2 = (1.0 - x) * cXm1y.getU + x * cY.getU
    val pu = x1 * yy + x2 * (1.0 - yy)
    val y1 = (1.0 - y) * cYm1.getV + y * cell.getV
    val y2 = (1.0 - y) * cYm1x.getV + y * cX.getV
    val pv = y1 * xx + y2 * (1.0 - xx)
    new Vector2d(pu, pv)
  }
}