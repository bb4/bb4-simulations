// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.rendering

import com.barrybecker4.simulation.conway.model.ConwayProcessor
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap
import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage

/**
  * Renders live Conway cells into a buffered image. When [[showShadows]] is true, past positions linger.
  * @author Barry Becker
  */
object ConwayRenderer {
  private val FloorColor = new Color(240, 244, 254)
}

class ConwayRenderer(
    val width: Double,
    val height: Double,
    val showShadows: Boolean,
    val scale: Int,
    var processor: ConwayProcessor,
    var cmap: ColorMap
) {

  final private val offlineGraphics: OfflineGraphics =
    new OfflineGraphics(new Dimension(width.toInt, height.toInt), ConwayRenderer.FloorColor)

  def getWidth: Int = width.toInt
  def getHeight: Int = height.toInt
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  /** Draw live cells using the color map (cell age). */
  def render(): Unit = synchronized {
    if !showShadows then offlineGraphics.clear()
    for c <- processor.getPoints do
      val value = processor.getValue(c)
      offlineGraphics.setColor(cmap.getColorForValue(value))
      val xpos = c.getX * scale
      val ypos = c.getY * scale
      offlineGraphics.fillRect(xpos, ypos, scale, scale)
  }
}
