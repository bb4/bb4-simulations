// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.data

import com.google.gson.annotations.SerializedName


case class Tile(
  @(SerializedName @scala.annotation.meta.field)("-name") name: String,
  @(SerializedName @scala.annotation.meta.field)("-symmetry") symmetry: String,
  @(SerializedName @scala.annotation.meta.field)("-weight") weight: String
) {
  def getWeight: Double = if (weight == null) 1.0 else weight.toDouble
}