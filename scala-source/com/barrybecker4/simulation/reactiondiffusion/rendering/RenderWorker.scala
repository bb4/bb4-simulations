/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import java.awt._


/**
  * Renders one of the rectangular strips.
  * @author Barry Becker
  */
class RenderWorker private[rendering](var minX: Int, var maxX: Int, var renderer: RDRenderer, var g2: Graphics2D)
  extends Runnable {

  override def run(): Unit = {
    if (maxX - minX > 0) {
      val colorRect = renderer.getColorRect(minX, maxX)
      renderer.renderStrip(minX, colorRect, g2)
    }
  }
}
