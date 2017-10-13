/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common1.rendering.ColorRect
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import java.awt._


/**
  * Renders the state of the GrayScottController model to the screen.
  *
  * @author Barry Becker
  */
class RDOnscreenRenderer(model: GrayScottModel, cmap: ColorMap, options: RDRenderingOptions)
  extends RDRenderer(model, cmap, options) {

  /** Renders a rectangular strip of pixels. */
  override def renderStrip(minX: Int, rect: ColorRect, g2: Graphics2D) {
    val img = rect.getAsImage
    if (g2 != null) g2.drawImage(img, minX, 0, null)
  }

  override protected def postRender(g2: Graphics2D) {}
}
