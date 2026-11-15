// Copyright by Barry G. Becker, 2018-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.model

import scala.math.{cos, sin}

/**
  * Named initial ground and water surface profiles.
  * Y grows downward: smaller values are higher on screen; water sits where h1 &lt; floor.
  */
enum InitialEnvironment(val displayName: String):

  case SlopingBeach extends InitialEnvironment("Sloping Beach")
  case FlatPool extends InitialEnvironment("Flat Pool")
  case Basin extends InitialEnvironment("Basin")
  case SteppedTerraces extends InitialEnvironment("Stepped Terraces")

  /**
    * Fill water (h0/h1) and floor arrays for the given dimensions.
    * Profiles scale with height via yrat = height / 400.0.
    */
  def fill(h0: Array[Double], h1: Array[Double], floor: Array[Double],
           width: Int, height: Int): Unit = {
    val yrat = height / 400.0
    this match {
      case SlopingBeach =>
        for (i <- 0 until width) {
          val ht = yrat * (50.0 + 20.0 * sin(0.018 * i) + i / 5.0)
          h0(i) = ht
          h1(i) = ht
          floor(i) = yrat * (180.0 + 42.0 * cos(0.01 * i) + i / 8.0)
        }
      case FlatPool =>
        val floorY = yrat * 200.0
        val waterY = yrat * 100.0
        for (i <- 0 until width) {
          h0(i) = waterY
          h1(i) = waterY
          floor(i) = floorY
        }
      case Basin =>
        // Bowl: shallow (small y) at edges, deep (large y) in the middle.
        val waterY = yrat * 90.0
        for (i <- 0 until width) {
          val t = if (width <= 1) 0.5 else i.toDouble / (width - 1)
          val bowl = 1.0 - 4.0 * (t - 0.5) * (t - 0.5) // 0 at edges, 1 at center
          floor(i) = yrat * (120.0 + 100.0 * bowl)
          h0(i) = waterY
          h1(i) = waterY
        }
      case SteppedTerraces =>
        val numSteps = 4
        val stepW = math.max(1, width / numSteps)
        for (i <- 0 until width) {
          val step = math.min(numSteps - 1, i / stepW)
          val floorY = yrat * (140.0 + 40.0 * step)
          val waterY = floorY - yrat * 40.0
          h0(i) = waterY
          h1(i) = waterY
          floor(i) = floorY
        }
    }
  }

end InitialEnvironment
