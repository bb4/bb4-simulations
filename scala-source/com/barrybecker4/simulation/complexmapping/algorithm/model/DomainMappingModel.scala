/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import java.awt.image.BufferedImage
import scala.compiletime.uninitialized
import com.barrybecker4.simulation.complexmapping.algorithm._
import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import com.barrybecker4.ui.util.ColorMap


/**
  * Caches the domain heatmap image for a function/viewport/color-mode combination.
  */
case class DomainMappingModel(
  function: ComplexFunction,
  n: Int,
  viewport: Box,
  sampleInc: Double,
  colorMode: DomainColorMode,
  colorMap: ColorMap,
  showKnownZeros: Boolean
) {
  private var lastPixelWidth: Int = -1
  private var lastPixelHeight: Int = -1
  private var lastImage: BufferedImage = uninitialized

  def getImage(pixelWidth: Int, pixelHeight: Int): BufferedImage = {
    if (pixelWidth != lastPixelWidth || pixelHeight != lastPixelHeight || lastImage == null) {
      lastPixelWidth = pixelWidth
      lastPixelHeight = pixelHeight
      val renderer = DomainHeatmapRenderer(
        function, n, viewport, effectiveSampleInc, colorMode, colorMap, showKnownZeros
      )
      lastImage = renderer.render(pixelWidth, pixelHeight)
    }
    lastImage
  }

  /** Cap sample count so redraws stay interactive on the tall critical strip. */
  private def effectiveSampleInc: Double = {
    val maxSamples = 12000.0
    val rough = Math.max(2.0, viewport.width / Math.max(0.01, sampleInc)) *
      Math.max(2.0, viewport.height / Math.max(0.01, sampleInc))
    if (rough <= maxSamples) sampleInc
    else Math.sqrt(viewport.width * viewport.height / maxSamples)
  }
}
