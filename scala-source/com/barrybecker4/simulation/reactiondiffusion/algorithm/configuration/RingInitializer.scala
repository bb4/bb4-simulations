/** Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration

import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration.Initializer.{INITIAL_U, INITIAL_V}


class RingInitializer(initialU: Double = INITIAL_U, initialV: Double = INITIAL_V) extends Initializer {

  override def initialize(grid: InitializableGrid, width: Int, height: Int): Unit = {

    stampSquare(0, 0, width, height, 1.0, 0, grid)

    // random square 1
    val w2 = width / 2
    val h3 = height / 3
    stampRing(w2, h3 + 10, w2 / 2, 12, INITIAL_U, INITIAL_V, grid)

    // random square 2
    val w7 = width / 7
    val h5 = height / 5
    stampRing(4 * w7, 3 * h5, h5 / 2, 15, INITIAL_U, INITIAL_V, grid)
  }
}
