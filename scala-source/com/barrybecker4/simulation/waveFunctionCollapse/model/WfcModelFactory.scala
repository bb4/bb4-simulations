// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{Overlapping, SimpleTiled}

/**
  * Pure construction of [[WfcModel]] instances from sample metadata and chosen parameters.
  * Kept separate from Swing wiring in [[com.barrybecker4.simulation.waveFunctionCollapse.ui.DynamicOptions]].
  */
object WfcModelFactory {

  def overlapping(
      sample: Overlapping,
      width: Int,
      height: Int,
      periodicOutput: Boolean,
      imageParams: OverlappingImageParams,
      allowInconsistencies: Boolean,
      limit: Int = 100
  ): WfcModel =
    new OverlappingModel(
      sample.getName,
      width,
      height,
      periodicOutput,
      imageParams,
      limit,
      allowInconsistencies
    )

  def simpleTiled(
      sample: SimpleTiled,
      width: Int,
      height: Int,
      subset: String,
      periodicOutput: Boolean,
      black: Boolean,
      allowInconsistencies: Boolean,
      limit: Int = 100
  ): WfcModel =
    new SimpleTiledModel(
      width,
      height,
      sample.getName,
      subset,
      periodicOutput,
      black,
      limit,
      allowInconsistencies
    )
}
