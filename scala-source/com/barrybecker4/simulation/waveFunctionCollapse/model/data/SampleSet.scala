// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.data

import com.google.gson.annotations.SerializedName


case class SampleSet(
  @(SerializedName @scala.annotation.meta.field)("-size") size: String,
  @(SerializedName @scala.annotation.meta.field)("-unique") unique: String,
  @(SerializedName @scala.annotation.meta.field)("neighbors") neighbors: Neighbors,
  @(SerializedName @scala.annotation.meta.field)("tiles") tiles: Tiles,
  @(SerializedName @scala.annotation.meta.field)("subsets") subsets: Subsets
)