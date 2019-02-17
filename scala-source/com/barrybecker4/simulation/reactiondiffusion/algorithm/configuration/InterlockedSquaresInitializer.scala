/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration

import Initializer.{INITIAL_U, INITIAL_V}


class InterlockedSquaresInitializer(initialU: Double = INITIAL_U, initialV: Double = INITIAL_V) extends Initializer {

  override def initialize(grid: InitializableGrid, width: Int, height: Int): Unit = {

    stampSquare(0, 0, width, height, 1.0, 0, grid)

    // random square 1
    val w3 = width / 3
    val h3 = height / 3
    stampSquare(w3, h3, w3, h3, INITIAL_U, INITIAL_V, grid)

    // random square 2
    val w7 = width / 7
    val h5 = height / 5
    stampSquare(5 * w7, 3 * h5, w7, h5, INITIAL_U, INITIAL_V, grid)
  }

}
