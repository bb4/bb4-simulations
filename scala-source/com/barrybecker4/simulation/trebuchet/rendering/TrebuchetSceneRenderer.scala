// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.trebuchet.TrebuchetSimulator
import com.barrybecker4.simulation.trebuchet.model.Trebuchet
import com.barrybecker4.simulation.trebuchet.rendering.TrebuchetSceneRenderer.*
import com.barrybecker4.ui.util.GUIUtil
import jdk.internal.jimage.decompressor.CompressedResourceHeader.getSize

import java.awt.{Color, Dimension, Font, Graphics2D, RenderingHints}


object TrebuchetSceneRenderer {
  private val BACKGROUND_COLOR = new Color(253, 250, 253)
  private val TEXT_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 24)
  private val TEXT_COLOR = new Color(100, 100, 100)
}

class TrebuchetSceneRenderer(trebuchet: Trebuchet)  {

  def render(g2: Graphics2D, dim: Dimension, useAntialiasing: Boolean): Unit = {
    g2.setColor(BACKGROUND_COLOR)

    g2.fillRect(0, 0, dim.width, dim.height)

    val aliasing = if (useAntialiasing) RenderingHints.VALUE_ANTIALIAS_ON
    else RenderingHints.VALUE_ANTIALIAS_OFF
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aliasing)
    
    // draw the trebuchet in its current position
    trebuchet.render(g2, dim.height)

    g2.setFont(TEXT_FONT);
    g2.setColor(TEXT_COLOR);
    val distance = trebuchet.getProjectileDistanceX.toInt
    g2.drawString("Distance: " + distance, dim.width - 240, dim.height - 50);
  }

}
