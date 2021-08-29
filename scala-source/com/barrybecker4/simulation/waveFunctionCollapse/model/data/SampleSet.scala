/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse.model.data

import com.google.gson.annotations.SerializedName


case class SampleSet(
  @SerializedName("-size")
  val size: String,
  @SerializedName("-unique")
  val unique: String,
  @SerializedName("neighbors")
  val neighbors: Neighbors,
  @SerializedName("tiles")
  val tiles: Tiles,
  @SerializedName("subsets")
  val subsets: Subsets
)