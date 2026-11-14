/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import java.awt._
import java.awt.image.BufferedImage


/**
  * Blits the completed image directly to the on-screen Graphics2D (main thread only).
  */
class RDOnscreenRenderer(model: GrayScottModel, cmap: ColorMap, options: RDRenderingOptions)
  extends RDRenderer(model, cmap, options) {

  override protected def blit(image: BufferedImage, g2: Graphics2D): Unit = {
    if (g2 != null) g2.drawImage(image, 0, 0, null)
  }
}
