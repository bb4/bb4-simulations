// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import org.scalatest.funsuite.AnyFunSuite

import java.awt.Color
import java.awt.image.BufferedImage

class PatternColorSamplesSuite extends AnyFunSuite {

  test("index and patternFromIndex round-trip for small palette") {
    val img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB)
    img.setRGB(0, 0, Color.RED.getRGB)
    img.setRGB(1, 0, Color.BLUE.getRGB)
    img.setRGB(0, 1, Color.GREEN.getRGB)
    img.setRGB(1, 1, Color.RED.getRGB)

    val pcs = PatternColorSamples(img, N = 2)
    val p = Array[Byte](0, 1, 2, 0)
    val ind = pcs.index(p)
    val back = pcs.patternFromIndex(ind)
    assert(back.sameElements(p))
  }
}
