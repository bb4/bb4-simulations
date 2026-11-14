// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer

import org.scalatest.funsuite.AnyFunSuite

class ZoomBoxSuite extends AnyFunSuite {

  test("isValidBox requires two distinct corners") {
    val z = new ZoomBox()
    assert(!z.isValidBox)
    z.setFirstCorner(0, 0)
    assert(!z.isValidBox)
    z.setSecondCorner(0, 0)
    assert(!z.isValidBox)
    z.setSecondCorner(10, 10)
    assert(z.isValidBox)
  }

  test("clearBox clears validity") {
    val z = new ZoomBox()
    z.setFirstCorner(0, 0)
    z.setSecondCorner(20, 20)
    z.finalizeBoxForRange(1.0, keepAspectRatio = false)
    z.clearBox()
    assert(!z.isValidBox)
  }

  test("finalizeBoxForRange without render produces a usable box") {
    val z = new ZoomBox()
    z.setFirstCorner(10, 20)
    z.setSecondCorner(40, 50)
    z.finalizeBoxForRange(1.0, keepAspectRatio = false)
    val box = z.getBox
    assert(box.getWidth == 30)
    assert(box.getHeight == 30)
  }

  test("keepAspectRatio expands height when width-dominant for wide aspect") {
    val z = new ZoomBox()
    // Drag a wide short rectangle; aspect 2.0 means width/height should become 2
    z.setFirstCorner(0, 0)
    z.setSecondCorner(40, 10)
    z.finalizeBoxForRange(2.0, keepAspectRatio = true)
    val box = z.getBox
    assert(box.getWidth == 40)
    assert(box.getHeight == 20) // width / aspectRatio
  }

  test("keepAspectRatio expands width when height-dominant for tall aspect") {
    val z = new ZoomBox()
    z.setFirstCorner(0, 0)
    z.setSecondCorner(10, 40)
    z.finalizeBoxForRange(0.5, keepAspectRatio = true)
    val box = z.getBox
    assert(box.getHeight == 40)
    assert(box.getWidth == 20) // height * aspectRatio
  }
}
