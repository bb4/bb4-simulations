/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.simulation.common.rendering.bumps.BumpMapper
import com.barrybecker4.simulation.common.rendering.bumps.HeightField
import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel


/**
  * Determines pixel colors based on chemical concentrations and lighting models.
  * @author Barry Becker
  */
class RenderingColorModel private[rendering](var model: GrayScottModel, var cmap: ColorMap, var options: RDRenderingOptions) {
  private val bmapper = new BumpMapper
  private val heightMap = new HeightMap

  def getColorMap: ColorMap = cmap

  /**
    * Get the color for a specific position based on chemical concentrations.
    *
    * @return the color to use.
    */
  private[rendering] def getColorForPosition(x: Int, y: Int) = {
    val concentration = heightMap.getValue(x, y)
    var colorForValue = cmap.getColorForValue(concentration)
    if (options.getHeightScale != 0) {
      val htScale = options.getHeightScale
      bmapper.adjustForLighting(colorForValue, x, y, heightMap, htScale, options.getSpecular, BumpMapper.DEFAULT_LIGHT_SOURCE_DIR)
    } else colorForValue
  }

  private class HeightMap extends HeightField {
    override def getWidth: Int = model.getWidth
    override def getHeight: Int = model.getHeight

    override def getValue(x: Int, y: Int): Double =
      (if (options.isShowingU) model.u(x)(y) else 0.0) +
        (if (options.isShowingV) model.v(x)(y) else 0.0)
  }

}
