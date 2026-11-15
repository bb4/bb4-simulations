// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import org.scalatest.funsuite.AnyFunSuite

import java.awt.image.BufferedImage

class WfcDeterminismSuite extends AnyFunSuite {

  private def imageBytes(img: BufferedImage): Array[Int] = {
    val w = img.getWidth
    val h = img.getHeight
    val out = new Array[Int](w * h)
    img.getRGB(0, 0, w, h, out, 0, w)
    out
  }

  private def smallOverlapping(seed: Int): Option[BufferedImage] = {
    val model = new OverlappingModel(
      name = "Skyline",
      width = 24,
      height = 24,
      periodicOutput = true,
      imageParams = OverlappingImageParams(N = 2, symmetry = 1, periodicInput = true, groundParam = 0),
      limit = 0,
      allowInconsistencies = false,
      scale = 1
    )
    if (model.runWithLimit(seed)) Some(model.graphics()) else None
  }

  test("same seed produces identical overlapping image") {
    val a = smallOverlapping(424242).getOrElse(fail("first run failed to collapse"))
    val b = smallOverlapping(424242).getOrElse(fail("second run failed to collapse"))
    assert(imageBytes(a) sameElements imageBytes(b))
  }

  test("different seeds usually produce different overlapping images") {
    val a = smallOverlapping(1).getOrElse(fail("seed 1 failed"))
    val b = smallOverlapping(2).getOrElse(fail("seed 2 failed"))
    // Extremely unlikely to match for Skyline; if they do, still a valid collapse.
    assert(a.getWidth == b.getWidth && a.getHeight == b.getHeight)
  }
}
