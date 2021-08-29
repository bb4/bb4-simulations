/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse.model.data

import com.google.gson.annotations.SerializedName


case class Tile(
  @SerializedName("-name")
  val name: String,
  @SerializedName("-symmetry")
  val symmetry: String,
  @SerializedName("-weight")
  val weight: String
)