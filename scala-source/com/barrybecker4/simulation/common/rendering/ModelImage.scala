// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.ui.util.ImageUtil
import com.barrybecker4.simulation.common.RectangularModel
import java.awt._
import java.awt.image.BufferedImage


/**
  * Responsible for converting a model into an offscreen image.
  * This allows us to avoid threading issues when rendering because we can draw the whole image at once.
  * @author Barry Becker
  */
class ModelImage(var model: RectangularModel, var cmap: ColorMap, val scale: Int) {

  private var image = createBufferedImage()
  private var useLinearInterpolation = false

  def this(model: RectangularModel, cmap: ColorMap) { this(model, cmap, 1) }
  def getColorMap: ColorMap = cmap
  def setUseLinearInterpolation(useInterp: Boolean): Unit = {useLinearInterpolation = useInterp }

  /**
    * Update the global image with a new strip of just computed pixels.
    * @param lastRow    row to render from
    * @param currentRow row to render to
    */
  def updateImage(lastRow: Int, currentRow: Int): Unit = {
    updateImageSizeIfNeeded()
    val width = model.getWidth
    val rectHeight = currentRow - lastRow
    if (rectHeight <= 0) return
    val pixels = new Array[Int](scale * width * scale * rectHeight)

    for (x <- 0 until width)
      for (y <- 0 until rectHeight) {
        if (scale > 1) setColorRect(x, y, lastRow, pixels)
        else {
          val c = cmap.getColorForValue(model.getValue(x, y + lastRow))
          pixels(y * width + x) = c.getRGB
        }
      }

    image.setRGB(0, scale * lastRow, scale * width, scale * rectHeight, pixels, 0, scale * width)
  }

  /** Determine the colors for a rectangular strip of pixels. */
  private def setColorRect(xStart: Int, yStart: Int, lastRow: Int, pixels: Array[Int]): Unit = {
    val width = scale * model.getWidth
    val xScaledStart = scale * xStart
    val yScaledStart = scale * yStart
    val yPos = yStart + lastRow
    if (useLinearInterpolation) {
      val colorLL = new Array[Float](4)
      val colorLR = new Array[Float](4)
      val colorUL = new Array[Float](4)
      val colorUR = new Array[Float](4)
      cmap.getColorForValue(model.getValue(xStart, yPos)).getComponents(colorLL)
      cmap.getColorForValue(model.getValue(xStart + 1, yPos)).getComponents(colorLR)
      cmap.getColorForValue(model.getValue(xStart, yPos + 1)).getComponents(colorUL)
      cmap.getColorForValue(model.getValue(xStart + 1, yPos + 1)).getComponents(colorUR)
      for (xx <- 0 until scale) {
        for (yy <- 0 until scale) {
          val xrat = xx.toDouble / scale
          val yrat = yy.toDouble / scale
          val c = ImageUtil.interpolate(xrat, yrat, colorLL, colorLR, colorUL, colorUR)
          pixels((yScaledStart + yy) * width + xScaledStart + xx) = c.getRGB
        }
      }
    }
    else {
      val color = cmap.getColorForValue(model.getValue(xStart, yPos)).getRGB
      for (xx <- 0 until scale) {
        for (yy <- 0 until scale)
          pixels((yScaledStart + yy) * width + xScaledStart + xx) = color
      }
    }
  }

  /** @return the accumulated image so far.*/
  def getImage: Image = image

  private def updateImageSizeIfNeeded(): Unit = {
    if (image.getWidth != scale * model.getWidth || image.getHeight != scale * model.getHeight)
      image = createBufferedImage()
  }

  /** @return the buffered image to draw into. */
  private def createBufferedImage(): BufferedImage = {
    new BufferedImage(scale * model.getWidth, scale * model.getHeight, BufferedImage.TYPE_INT_RGB)
  }
}
