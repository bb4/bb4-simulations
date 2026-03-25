// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering

import com.barrybecker4.simulation.common.RectangularModel
import com.barrybecker4.ui.util.ColorMap
import java.awt.Color
import java.awt.image.BufferedImage
import org.scalatest.funsuite.AnyFunSuite

class RectangularModelImageSuite extends AnyFunSuite {

  private def stubColormap: ColorMap = {
    val vals = Array(0.0, 1.0)
    val colors = Array(Color.RED, Color.BLUE)
    new ColorMap(vals, colors)
  }

  /** Values are 0.0 on even sum coordinates, 1.0 otherwise (checkerboard). */
  private class CheckerStub(w: Int, h: Int) extends RectangularModel {
    override def getWidth: Int = w
    override def getHeight: Int = h
    override def getValue(x: Int, y: Int): Double = if ((x + y) % 2 == 0) 0.0 else 1.0
    override def getCurrentRow: Int = h
    override def getLastRow: Int = 0
  }

  test("updateImageScaleOneMapsModelValuesThroughColormap") {
    val model = new CheckerStub(2, 2)
    val rmi = new RectangularModelImage(model, stubColormap, 1)
    rmi.updateImage(0, 2)
    val img = rmi.getImage.asInstanceOf[BufferedImage]
    assert(img.getRGB(0, 0) == Color.RED.getRGB)
    assert(img.getRGB(1, 0) == Color.BLUE.getRGB)
    assert(img.getRGB(0, 1) == Color.BLUE.getRGB)
    assert(img.getRGB(1, 1) == Color.RED.getRGB)
  }

  test("updateImageScaleTwoWithoutInterpolationUpsamplesSolidColorPerCell") {
    val model = new CheckerStub(2, 2)
    val rmi = new RectangularModelImage(model, stubColormap, 2)
    rmi.setUseLinearInterpolation(false)
    rmi.updateImage(0, 2)
    val img = rmi.getImage.asInstanceOf[BufferedImage]
    assert(img.getRGB(0, 0) == Color.RED.getRGB)
    assert(img.getRGB(1, 0) == Color.RED.getRGB)
    assert(img.getRGB(2, 0) == Color.BLUE.getRGB)
    assert(img.getRGB(3, 1) == Color.BLUE.getRGB)
  }

  test("interpolationAtModelEdgeDoesNotThrow") {
    val model = new CheckerStub(2, 2)
    val rmi = new RectangularModelImage(model, stubColormap, 2)
    rmi.setUseLinearInterpolation(true)
    rmi.updateImage(0, 2)
    val img = rmi.getImage.asInstanceOf[BufferedImage]
    assert(img.getWidth == 4)
    assert(img.getHeight == 4)
  }
}
