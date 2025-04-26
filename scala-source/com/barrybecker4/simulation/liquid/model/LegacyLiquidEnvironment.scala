// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.liquid.compute.GridUpdater
import com.barrybecker4.simulation.liquid.config.Conditions
import com.barrybecker4.simulation.liquid.config.Source
import com.barrybecker4.simulation.liquid.model.grid.{Grid, VortexGrid}
import scala.compiletime.uninitialized


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
object LegacyLiquidEnvironment {
  private val NUM_RAND_PARTS = 1
}

/**
  * Constructor to use if you want the environment based on a config file.
  */
class LegacyLiquidEnvironment(val configFile: String) {
  initializeFromConfigFile(configFile)
  /** the grid of cells that make up the environment */
  private var grid: Grid = uninitialized
  /** Does all the computational processing on the grid */
  private var gridUpdater: GridUpdater = uninitialized
  /** constraints and conditions from the configuration file. */
  private var conditions: Conditions = uninitialized

  // the set of particles in this simulation
  private var particles: Particles = uninitialized
  /** the time since the start of the simulation  */
  private var time = 0.0

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
  def getParticles: Iterator[LegacyParticle] = particles.iterator

  def setViscosity(v: Double): Unit = { gridUpdater.setViscosity(v)}
  def setB0(b0: Double): Unit = { gridUpdater.setB0(b0) }

  /**
    * Steps the simulation forward in time.
    * If the timestep is too big, inaccuracy and instability will result.
    * To prevent the instability we halve the timestep until the
    * Courant-Friedrichs-Levy condition is met.
    * In other words a particle should not be able to move more than a single cell
    * magnitude in a given timestep.
    * @return new timeStep to use.
    */
  def stepForward(timeStep: Double): Double = { // Update cell status so we can track the surface.
    grid.updateCellStatus()
    // Set up obstacle conditions for the free surface and obstacle cells
    setConstraints()

    // Update the position of the surface and objects.
    val newTimeStep = gridUpdater.updateParticlePosition(timeStep, particles)
    time += newTimeStep
    newTimeStep
  }

  private def setInitialLiquid(): Unit = {
    for (region <- conditions.getInitialLiquidRegions) {
      for (i <- region.getStart.getX to region.getStop.getX) {
        for (j <- region.getStart.getY to region.getStop.getY) {
          particles.addRandomParticles(i, j, 4 * LegacyLiquidEnvironment.NUM_RAND_PARTS, grid)
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
          particles.addRandomParticles(i, j, LegacyLiquidEnvironment.NUM_RAND_PARTS, grid)
        }
      }
    }
  }
}
