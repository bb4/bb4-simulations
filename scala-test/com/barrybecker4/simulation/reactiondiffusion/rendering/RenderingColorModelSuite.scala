/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import org.scalatest.funsuite.AnyFunSuite

class RenderingColorModelSuite extends AnyFunSuite {

  test("combined U+V concentration is clamped to colormap domain") {
    val model = new GrayScottModel(4, 4)
    model.initializePoint(1, 1, 0.8, 0.8) // sum 1.6 > 1
    val options = new RDRenderingOptions()
    options.setShowingU(true)
    options.setShowingV(true)
    val cmap = new RDColorMap()
    val colorModel = new RenderingColorModel(model, cmap, options)
    // Should not throw and should produce a defined color for overshoot
    val color = colorModel.getColorForPosition(1, 1)
    assert(color != null)
  }
}
