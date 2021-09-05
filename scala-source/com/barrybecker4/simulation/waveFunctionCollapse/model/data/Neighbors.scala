// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.data

import com.google.gson.annotations.SerializedName


case class Neighbors(
  @(SerializedName @scala.annotation.meta.field)("neighbor") neighbor: Array[Neighbor]
)