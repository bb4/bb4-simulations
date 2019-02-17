/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration


object Initializer {
  val INITIAL_U: Double = 0.5
  val INITIAL_V: Double = 0.25
}

trait Initializer {
  def initialize(grid: InitializableGrid, width: Int, height: Int)


  /** Place a square of chemicals with the initial concentrations. */
  protected def stampSquare(startX: Int, startY: Int,
                            width: Int, height: Int,
                            initialU: Double, initialV: Double,
                            grid: InitializableGrid): Unit = {
    for (x <- 0 until width)
      for (y <- 0 until height)
        grid.initializePoint(startX + x, startY + y, initialU, initialV)
  }


  /** Place a square of chemicals with the initial concentrations. */
  protected def stampRing(centerX: Int, centerY: Int, radius: Int, thickness: Int,
                        initialU: Double, initialV: Double,
                        grid: InitializableGrid): Unit = {
    val xMin = centerX - radius
    val xMax = centerX + radius
    val yMin = centerY - radius
    val yMax = centerY + radius
    val thicknessD2 = thickness / 2

    for (x <- xMin to xMax)
      for (y <- yMin to yMax) {
        val r = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2))
        if (r > radius - thicknessD2 && r <= radius + thicknessD2)
          grid.initializePoint(x, y, initialU, initialV)
      }

  }
}
