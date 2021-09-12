// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled

import com.google.gson.annotations.SerializedName


case class SampleTiledData(
  @(SerializedName @scala.annotation.meta.field)("set") set: SampleSet
)