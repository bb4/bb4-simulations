// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled


case class SampleSet(
  size: String,
  unique: String,
  neighbors: Array[Neighbor],
  tiles: Array[Tile],
  subsets: Array[Subset]
)