/** Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration

trait InitializableGrid {

  def initializePoint(x: Int, y: Int, initialU: Double, initialV: Double): Unit
}
