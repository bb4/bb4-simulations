// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled


case class Tile(
  name: String,
  symmetry: String,
  weight: String
) {
  def getWeight: Double = if (weight == null) 1.0 else weight.toDouble
}