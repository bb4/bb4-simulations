// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.Logger
import com.barrybecker4.simulation.liquid.model.Grid
import com.barrybecker4.simulation.liquid.model.Particles
import javax.vecmath.Vector2d
import com.barrybecker4.simulation.common1.PhysicsConstants.ATMOSPHERIC_PRESSURE


/**
  * Updates the grid physics.
  * adapted from work by Nick Foster.
  * See http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
  *
  * Improvements:
  *    - increase performance by only keeping track of particles near the surface.
  *    - allow configuring walls from file.
  *
  * @author Barry Becker
  */
object GridUpdater {
  /**
    * Viscosity of the liquid. Larger for molasses (.3), smaller for kerosene (.0001)
    * Water is about .001 Ns/m	&#94;2 or .01 g/s mm
    */
  val DEFAULT_VISCOSITY = 0.002

  /** Used in mass conservation (how?) */
  val DEFAULT_B0 = 1.4 // 1.7
}

/** The grid of cells that make up the environment */
class GridUpdater(var grid: Grid) {

  private var viscosity = GridUpdater.DEFAULT_VISCOSITY
  /** used in mass conservation as a relaxation constant */
  private var b0 = GridUpdater.DEFAULT_B0

  def setViscosity(v: Double) { viscosity = v }

  def setB0(b0: Double) {this.b0 = b0 }

  /** Compute tilde velocity of each cell */
  def updateVelocity(timeStep: Double, gravity: Double): Unit = {
    Logger.log(1, "stepForward: about to update the velocity field (timeStep=" + timeStep + ')')
    //var i = 0
    //var j = 0
    val force = new Vector2d(0, gravity)
    val velocityUpdater = new VelocityUpdater
    for (j <- 1 until grid.getYDimension - 1) {
      for (i <- 1 until grid.getXDimension - 1) {
        velocityUpdater.updateTildeVelocities(
          grid.getCell(i, j), grid.getNeighbors(i, j), grid.getCell(i - 1, j + 1), grid.getCell(i + 1, j - 1),
          timeStep, force, viscosity)
      }
    }

    for (j <- 1 until grid.getYDimension - 1)
      for (i <- 1 until grid.getXDimension - 1)
        grid.getCell(i, j).swap()
  }

  /**
    * perform pressure iteration to consider mass conservation.
    * repeat till all cells in the flow field have a divergence less than EPSILON.
    * When things go bad, this can take 50-70 or more iterations.
    * RISK: 6
    * @return the maximum divergence of any of the cells in the grid.
    */
  def updatePressure(timeStep: Double): Double = {
    val updater = new PressureUpdater(grid, b0)
    val div = updater.updatePressure(timeStep)
    if (updater.getNumIterations > 10) Logger.log(1, " updatePress: converged to maxDiv = " + div + " after " + updater.getNumIterations + " iterations.")
    div
  }

  /** compute velocity and pressure of SURFACE cells. */
  def updateSurfaceVelocity(): Unit = {
    val surfaceUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE)

    for (j <- 1 until grid.getYDimension - 1)
      for (i <- 1 until grid.getXDimension - 1)
        surfaceUpdater.updateSurfaceVelocities(grid.getCell(i, j), grid.getNeighbors(i, j))
  }

  /**
    * move particles according to vector field.
    * updates the timeStep if the cfl condition is not met.
    * RISK: 3
    * @return the current timeStep (it was possible adjusted)
    */
  def updateParticlePosition(timeStep: Double, particles: Particles): Double = {
    val updater = new ParticleAdvector(grid)
    updater.advectParticles(timeStep, particles)
  }
}
