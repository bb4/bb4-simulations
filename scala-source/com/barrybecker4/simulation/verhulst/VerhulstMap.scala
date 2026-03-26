// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

/**
  * Normalized discrete Verhulst (logistic-style) growth in one step:
  * `P' = P * (1 + r * (1 - P))` with growth parameter `r` and population `P` in `(0, 1)`.
  * Fixed points at `P = 0` and `P = 1`.
  */
object VerhulstMap {

  def nextPopulation(population: Double, growthParameter: Double): Double =
    population * (1.0 + growthParameter * (1.0 - population))
}
