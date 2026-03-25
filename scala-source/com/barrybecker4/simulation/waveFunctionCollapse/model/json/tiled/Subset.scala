// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled

import com.google.gson.annotations.SerializedName

import scala.annotation.meta.field

case class Subset(
  name: String,
  @(SerializedName @field)("tile") tiles: Array[Tile]
)