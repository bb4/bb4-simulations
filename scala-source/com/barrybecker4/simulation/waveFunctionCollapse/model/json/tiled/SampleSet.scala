// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled

import com.google.gson.annotations.SerializedName


case class SampleSet(
  @(SerializedName @scala.annotation.meta.field)("-size") size: String,
  @(SerializedName @scala.annotation.meta.field)("-unique") unique: String,
  @(SerializedName @scala.annotation.meta.field)("neighbors") neighbors: Array[Neighbor],
  @(SerializedName @scala.annotation.meta.field)("tiles") tiles: Array[Tile],
  @(SerializedName @scala.annotation.meta.field)("subsets") subsets: Array[Subset]
)