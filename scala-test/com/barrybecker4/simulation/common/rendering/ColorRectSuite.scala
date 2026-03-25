// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering

import java.awt.Color
import org.scalatest.funsuite.AnyFunSuite

class ColorRectSuite extends AnyFunSuite {

  test("setColorWritesPixelAtIndex") {
    val rect = new ColorRect(3, 2)
    val c = new Color(10, 20, 30)
    rect.setColor(1, 0, c)
    assert(rect.getRgb(1, 0) == c.getRGB)
    assert(rect.getRgb(0, 0) != c.getRGB)
  }

  test("setColorRectFillsEntireSubrectangle") {
    val rect = new ColorRect(4, 4)
    val fill = new Color(99, 88, 77)
    rect.setColorRect(1, 1, 2, 2, fill)
    val rgb = fill.getRGB
    assert(rect.getRgb(0, 0) != rgb)
    assert(rect.getRgb(1, 1) == rgb)
    assert(rect.getRgb(2, 1) == rgb)
    assert(rect.getRgb(1, 2) == rgb)
    assert(rect.getRgb(2, 2) == rgb)
    assert(rect.getRgb(0, 1) != rgb)
    assert(rect.getRgb(3, 3) != rgb)
  }
}
