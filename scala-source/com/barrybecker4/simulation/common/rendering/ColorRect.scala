/*
 * // Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 *
 */

package com.barrybecker4.simulation.common.rendering

import com.barrybecker4.ui.util.ImageUtil
import java.awt.Color
import java.awt.Image


/**
  * Creates a uniformly colored rectangle.
  * @author Barry Becker
  */
class ColorRect(var width: Int, var height: Int) {

  /** 2d array represented with a 1 dimensional array */
  private var pixels = Array.ofDim[Int](width * height)

  def setColor(x: Int, y: Int, c: Color): Unit = setColor(x, y, c.getRGB)

  /** @return an image representing this rectangle of colors. */
  def getAsImage: Image = ImageUtil.getImageFromPixelArray(pixels, width, height)

  /** Set the color for a whole rectangular region. */
  def setColorRect(x: Int, y: Int, width: Int, height: Int, c: Color): Unit = {
    val color = c.getRGB
    for (i <- x until x + width)
      for (j <- y until y + height)
        setColor(x, y, color)
  }

  private def setColor(x: Int, y: Int, color: Int): Unit = {
    val location = y * width + x
    pixels(location) = color
  }
}