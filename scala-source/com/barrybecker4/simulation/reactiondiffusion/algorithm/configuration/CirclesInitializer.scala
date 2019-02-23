/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration

import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration.Initializer.{INITIAL_U, INITIAL_V}


case object CirclesInitializer extends Initializer("Circles", INITIAL_U, INITIAL_V) {

  override def initialize(grid: InitializableGrid, width: Int, height: Int): Unit = {

    stampSquare(0, 0, width, height, 1.0, 0, grid)

    // random square 1
    val h4 = height / 4
    stampCircle(h4 + 40, h4 + 30, h4, INITIAL_U, INITIAL_V, grid)

    // random square 2
    val w7 = width / 7
    val h5 = height / 5
    stampCircle(4 * w7, 3 * h5, w7, INITIAL_U, INITIAL_V, grid)
  }
}
