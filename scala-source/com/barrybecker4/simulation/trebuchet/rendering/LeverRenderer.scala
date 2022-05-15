// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.trebuchet.model.parts.Lever
import com.barrybecker4.simulation.trebuchet.rendering.LeverRenderer.{LEVER_COLOR, LEVER_STROKE}

import java.awt.{BasicStroke, Color, Graphics2D}


object LeverRenderer {
  private val LEVER_STROKE = new BasicStroke(10.0f)
  private val LEVER_COLOR = new Color(70, 50, 180)
}

class LeverRenderer(lever: Lever) extends AbstractPartRenderer {

  override def render(g2: Graphics2D, scale: Double, viewHeight: Int): Unit = {
    g2.setStroke(LEVER_STROKE)
    g2.setColor(LEVER_COLOR)

    val y = (viewHeight - scale * lever.getBaseY).toInt
    val fulcrumPos = lever.getFulcrumPosition
    val cos = Math.cos(lever.getAngle)
    val sin = Math.sin(lever.getAngle)

    g2.drawLine((scale * (fulcrumPos.x + sin * lever.getCounterWeightLeverLength)).toInt,
      (scale * (fulcrumPos.y - cos * lever.getCounterWeightLeverLength)).toInt + y,
      (scale * (fulcrumPos.x - sin * lever.getSlingLeverLength)).toInt,
      (scale * (fulcrumPos.y + cos * lever.getSlingLeverLength)).toInt + y)
  }
}
