// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model.Particles
import com.barrybecker4.simulation.liquid.model.grid.Grid


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

  def setViscosity(v: Double): Unit =  { viscosity = v }

  def setB0(b0: Double): Unit =  {this.b0 = b0 }


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
