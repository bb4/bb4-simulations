/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common.rendering.ColorRect
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt._


/**
  * Renders the state of the GrayScottController model to an offscreen image,
  * then copies the whole image to the screen.
  * @author Barry Becker
  */
class RDOffscreenRenderer(model: GrayScottModel, cmap: ColorMap, options: RDRenderingOptions, var observer: Container)
  extends RDRenderer(model, cmap, options) {

  /** offline rendering is fast  (I wish it was anyway)  */
  private var offlineGraphics = new OfflineGraphics(observer.getSize, Color.BLACK)

  /** Renders a rectangular strip of pixels. */
  override def renderStrip(minX: Int, rect: ColorRect, g2: Graphics2D) {
    val img = rect.getAsImage
    offlineGraphics.drawImage(img, minX, 0, null)
  }

  override protected def postRender(g2: Graphics2D) {
    g2.drawImage(offlineGraphics.getOfflineImage.get, 0, 0, observer)
  }
}
