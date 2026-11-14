// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.simulation.common.rendering.RectangularModelImage


/**
  * Renders one of the rectangular strips.
  * @author Barry Becker
  */
class RenderWorker private[rendering](modelImage: RectangularModelImage) extends Runnable {
  private var minY = 0
  private var maxY = 0

  def setRange(minY: Int, maxY: Int): Unit = {
    this.minY = minY
    this.maxY = maxY
  }

  override def run(): Unit = {
    modelImage.updateImage(minY, maxY)
  }
}
