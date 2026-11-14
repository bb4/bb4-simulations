/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt._
import java.awt.image.BufferedImage


/**
  * Blits the completed image into an offline buffer, then copies that buffer to the screen.
  */
class RDOffscreenRenderer(model: GrayScottModel, cmap: ColorMap, options: RDRenderingOptions, var observer: Container)
  extends RDRenderer(model, cmap, options) {

  private val offlineGraphics = new OfflineGraphics(observer.getSize, Color.BLACK)

  override protected def blit(image: BufferedImage, g2: Graphics2D): Unit = {
    offlineGraphics.drawImage(image, 0, 0, null)
    if (g2 != null) g2.drawImage(offlineGraphics.getOfflineImage.get, 0, 0, observer)
  }
}
