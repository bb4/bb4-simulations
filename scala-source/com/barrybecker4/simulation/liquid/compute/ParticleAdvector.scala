// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.Logger
import com.barrybecker4.simulation.liquid.model.Cell
import com.barrybecker4.simulation.liquid.model.CellDimensions
import com.barrybecker4.simulation.liquid.model.CellStatus
import com.barrybecker4.simulation.liquid.model.Grid
import com.barrybecker4.simulation.liquid.model.Particle
import com.barrybecker4.simulation.liquid.model.Particles


/**
  * Updates positions of the particle markers in the grid.
  * @author Barry Becker
  */
object ParticleAdvector {
  private val EPSILON = 0.0000001
}

/** @param grid the grid of cells that make up the environment */
class ParticleAdvector(var grid: Grid) {

  private[compute] var interpolator = new VelocityInterpolator(grid)

  /**
    * move particles according to vector field.
    * updates the timeStep if the cfl condition is not met.
    * RISK: 3
    *
    * @return the current timeStep (it was possible adjusted)
    */
  def advectParticles(timeStep: Double, particles: Particles): Double = { // keep track of the biggest velocity magnitude so we can adjust the timestep if needed.
    var maxLength = Double.MinValue

    for (particle <- particles.iterator) {
      val length = advectParticle(timeStep, particle)
      if (length > maxLength) maxLength = length
    }
    adjustTimeStep(timeStep, maxLength)
    timeStep
  }

  /**
    * The velocity of a particle is determined using area weighting interpolation.
    *
    * @return advect a particle if it is completely under water.
    */
  private def advectParticle(timeStep: Double, particle: Particle) = {
    val i = Math.floor(particle.x).toInt
    val j = Math.floor(particle.y).toInt
    val status = grid.getCell(i, j).getStatus
    var magnitude: Double = 0
    if ((status eq CellStatus.FULL) || (status eq CellStatus.SURFACE) || (status eq CellStatus.ISOLATED))
      magnitude = advectWaterParticle(timeStep, particle, i, j)
    magnitude
  }

  /**
    * Advect a single water particle.
    * @return the magnitude of its velocity vector.
    */
  private def advectWaterParticle(timeStep: Double, particle: Particle, i: Int, j: Int) = {
    val vel = interpolator.findVelocity(particle)
    // scale the velocity by the cell size so we can assume the cells have unit dims
    vel.scale(CellDimensions.INVERSE_CELL_SIZE)
    val xChange = timeStep * vel.x
    val yChange = timeStep * vel.y
    particle.set(particle.x + xChange, particle.y + yChange)
    particle.incAge(timeStep)
    val newHomeCell = findNewHomeCell(particle, i, j)
    assert(particle.x >= 1 && particle.y >= 1 &&
      particle.x < grid.getXDimension - 1 && particle.y < grid.getYDimension - 1,
      "particle.x=" + particle.x + "particle.y=" + particle.y)

    // adjust # particles as they cross cell boundaries
    newHomeCell.incParticles() // increment new cell

    grid.getCell(i, j).decParticles() // decrement last cell

    particle.setCell(newHomeCell)
    assert(grid.getCell(i, j).getNumParticles >= 0,
      "The number of particles in grid[" + i + "][" + j + "] is " + grid.getCell(i, j).getNumParticles)
    assert(newHomeCell.getNumParticles >= 0)
    vel.length
  }

  /**
    * Determine the cell that the particle is in now after moving it. May be same as it was.
    * Ensure the liquid does not enter an OBSTACLE.
    *
    * @return new Cell that the particle is in. Not necessarily different than original.
    */
  private def findNewHomeCell(particle: Particle, i: Int, j: Int) = {
    var ii = Math.floor(particle.x).toInt
    var jj = Math.floor(particle.y).toInt
    if (ii < 0) {
      particle.x = 0.0
      ii = 0
    }
    if (jj < 0) {
      particle.y = 0.0
      jj = 0
    }
    if (ii >= grid.getXDimension) {
      ii = grid.getXDimension - 1
      particle.x = ii
    }
    if (jj >= grid.getYDimension) {
      jj = grid.getYDimension - 1
      particle.y = jj
    }
    // move outside the obstacle if we find ourselves in one
    if (grid.getCell(ii, jj).getStatus eq CellStatus.OBSTACLE) {
      if (i < ii) particle.set(ii - ParticleAdvector.EPSILON, particle.y)
      else if (i > ii) particle.set(ii + 1.0 + ParticleAdvector.EPSILON, particle.y)
      if (j < jj) particle.set(particle.x, jj - ParticleAdvector.EPSILON)
      else if (j > jj) particle.set(particle.x, jj + 1.0 + ParticleAdvector.EPSILON)
    }
    ii = particle.x.toInt
    jj = particle.y.toInt
    grid.getCell(ii, jj)
  }

  /**
    * Adaptively change the time step if needed.
    *
    * @return new timeStep to use. May be unchanged.
    */
  private def adjustTimeStep(timeStep: Double, maxLength: Double) = { // max distance to go in one step. Beyond this, we apply the governor.
    val maxDistance = CellDimensions.CELL_SIZE / 10.0
    val minDistance = CellDimensions.CELL_SIZE / 10000.0
    // adjust the timestep if needed.
    val increment = timeStep * maxLength
    var newTimeStep = timeStep
    if (increment > maxDistance) {
      newTimeStep /= 2.0
      Logger.log(1, "advectParticles: HALVED dt=" + timeStep + " increment=" + increment)
    }
    else if (increment < minDistance) {
      newTimeStep *= 2.0
      Logger.log(1, "advectParticles: DOUBLED dt=" + timeStep + " increment=" + increment + " maxLength=" + maxLength)
    }
    newTimeStep
  }
}
