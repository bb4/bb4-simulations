// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.liquid.Logger
import com.barrybecker4.simulation.liquid.compute.GridUpdater
import com.barrybecker4.simulation.liquid.config.Conditions
import com.barrybecker4.simulation.liquid.config.Source


/**
  * This is the global space containing all the cells, walls, and particles
  * Assumes an M*N grid of cells.
  * X axis increases to the left
  * Y axis increases downwards to be consistent with java graphics.
  * Adapted from work by Nick Foster.
  * See http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
  *
  * Improvements:
  *    - increase performance by only keeping track of particles near the surface.
  *    - allow configuring walls from file.
  *
  * @author Barry Becker
  */
object LiquidEnvironment {
  private val NUM_RAND_PARTS = 1
}

/**
  * Constructor to use if you want the environment based on a config file.
  */
class LiquidEnvironment(val configFile: String) {
  initializeFromConfigFile(configFile)
  /** the grid of cells that make up the environment */
  private var grid: Grid = _
  /** Does all the computational processing on the grid */
  private var gridUpdater: GridUpdater = _
  /** constraints and conditions from the configuration file. */
  private var conditions: Conditions = _
  // the set of particles in this simulation
  private var particles: Particles = _
  /** the time since the start of the simulation  */
  private var time = 0.0
  private var advectionOnly = false

  private def initializeFromConfigFile(configFile: String): Unit = {
    conditions = new Conditions(configFile)
    initEnvironment()
  }

  def reset(): Unit = { initEnvironment() }

  private def initEnvironment(): Unit = {
    val xDim = conditions.getGridWidth + 2
    val yDim = conditions.getGridHeight + 2
    grid = new VortexGrid(xDim, yDim)
    particles = new Particles
    gridUpdater = new GridUpdater(grid)
    setInitialLiquid()
    grid.setBoundaries()
    setConstraints()
  }

  def getWidth: Int = grid.getXDimension + 2
  def getHeight: Int = grid.getYDimension + 2
  def getGrid: Grid = grid
  def getParticles: Iterator[Particle] = particles.iterator

  def setViscosity(v: Double): Unit = { gridUpdater.setViscosity(v)}
  def setB0(b0: Double): Unit = { gridUpdater.setB0(b0) }
  def getAdvectionOnly: Boolean = advectionOnly
  def setAdvectionOnly(advectOnly: Boolean): Unit = { advectionOnly = advectOnly }

  /**
    * Steps the simulation forward in time.
    * If the timestep is too big, inaccuracy and instability will result.
    * To prevent the instability we halve the timestep until the
    * Courant-Friedrichs-Levy condition is met.
    * In other words a particle should not be able to move more than a single cell
    * magnitude in a given timestep.
    * @return new new timeStep to use.
    */
  def stepForward(timeStep: Double): Double = { // Update cell status so we can track the surface.
    grid.updateCellStatus()
    // Set up obstacle conditions for the free surface and obstacle cells
    setConstraints()
    if (!advectionOnly) { // Compute velocities for all full cells.
      gridUpdater.updateVelocity(timeStep, conditions.getGravity)
      ////println(grid.toString());
      // Compute the pressure for all Full Cells.
      gridUpdater.updatePressure(timeStep)
      // Re-calculate velocities for Surface cells.
      gridUpdater.updateSurfaceVelocity()
    }
    // Update the position of the surface and objects.
    val newTimeStep = gridUpdater.updateParticlePosition(timeStep, particles)
    time += newTimeStep
    Logger.log(1, " the Time= " + time)
    newTimeStep
  }

  private def setInitialLiquid(): Unit = {
    for (region <- conditions.getInitialLiquidRegions) {
      for (i <- region.getStart.getX to region.getStop.getX) {
        for (j <- region.getStart.getY to region.getStop.getY) {
          particles.addRandomParticles(i, j, 4 * LiquidEnvironment.NUM_RAND_PARTS, grid)
        }
      }
    }
  }

  private def setConstraints(): Unit = {
    grid.setBoundaryConstraints()
    //addWalls();
    addSources()
    //addSinks();
  }

  def addSources(): Unit = {
    for (source <- conditions.getSources) {
      addSource(source)
    }
  }

  private def addSource(source: Source): Unit = { //add a spigot of liquid
    val velocity = source.getVelocity
    if (source.isOn(time)) {
      for (i <- source.getStart.getX to source.getStop.getX) {
        for (j <- source.getStart.getY to source.getStop.getY) {
          grid.setVelocity(i, j, velocity)
          particles.addRandomParticles(i, j, LiquidEnvironment.NUM_RAND_PARTS, grid)
        }
      }
    }
  }
}
