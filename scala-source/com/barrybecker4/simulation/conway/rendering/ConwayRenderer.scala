// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.rendering

import com.barrybecker4.simulation.conway.model.ConwayProcessor
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap
import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage


/**
  * Everything we need to know to compute the l-System tree.
  * Should make the tree automatically center.
  * Shadows show where the life has been.
  * @author Barry Becker
  */
object ConwayRenderer {
  private val FLOOR_COLOR = new Color(240, 244, 254)
}

class ConwayRenderer(val width: Double, val height: Double,
 val showShadows: Boolean, val scale: Int, var processor: ConwayProcessor, var cmap: ColorMap) {

  final private var offlineGraphics: OfflineGraphics =
    new OfflineGraphics(new Dimension(width.toInt, height.toInt), ConwayRenderer.FLOOR_COLOR)

  def getWidth: Int = width.toInt
  def getHeight: Int = height.toInt
  def getImage: BufferedImage = offlineGraphics.getOfflineImage

  /** render the live cells on the grid */
  def render(): Unit = synchronized {
    // if not showing where life has been, clear all first
    if (!showShadows) offlineGraphics.clear()
    for (c <- processor.getPoints) {
      val value = processor.getValue(c)
      if (value != null) {
        val color = cmap.getColorForValue(value)
        offlineGraphics.setColor(color)
        val xpos = c.getX * scale
        val ypos = c.getY * scale
        offlineGraphics.fillRect(xpos, ypos, scale, scale)
      }
    }
  }
}
