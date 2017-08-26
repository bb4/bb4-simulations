// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.simulation.common.rendering.ModelImage


/**
  * Renders one of the rectangular strips.
  * @author Barry Becker
  */
class RenderWorker private[rendering](var modelImage: ModelImage, var minY: Int, var maxY: Int) extends Runnable {

  override def run(): Unit = {
    modelImage.updateImage(minY, maxY)
  }
}
