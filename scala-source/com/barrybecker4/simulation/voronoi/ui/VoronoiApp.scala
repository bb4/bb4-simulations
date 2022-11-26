// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.ui

object VoronoiApp {
  private val NUM_POINTS = 100

  def main(args: Array[String]): Unit = {
    new VoronoiFrame(NUM_POINTS)
  }
}
