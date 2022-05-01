// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.trebuchet.model.parts.Base
import com.barrybecker4.simulation.trebuchet.rendering.BaseRenderer.{BASE_STROKE, BASE_COLOR}
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.{HEIGHT, SCALE_FACTOR}
import java.awt.{BasicStroke, Color, Graphics2D}


object BaseRenderer {
  private val BASE_STROKE = new BasicStroke(2.0f)
  private val BASE_COLOR = new Color(10, 40, 160)
}

class BaseRenderer(base: Base) extends AbstractPartRenderer {

  override def render(g2: Graphics2D, scale: Double, viewHeight: Int): Unit = {
    g2.setStroke(BASE_STROKE)
    g2.setColor(BASE_COLOR)

    val x = (scale * base.getBaseX).toInt
    val y = viewHeight - base.getBaseY
    val width = (scale * base.getWidth).toInt
    val baseHeight = (scale * 10.0).toInt
    val strutBackX = (scale * (base.getStrutBaseX - base.getStrutBaseHalfWidth)).toInt
    val strutMidX = (scale * base.getStrutBaseX).toInt
    val strutFrontX = (scale * (base.getStrutBaseX + base.getStrutBaseHalfWidth)).toInt
    val strutJoinY = (y - scale * SCALE_FACTOR * HEIGHT).toInt

    g2.draw3DRect(x, y, width, baseHeight, false)
    g2.drawLine(strutBackX, y, strutMidX, strutJoinY)
    g2.drawLine(strutFrontX, y, strutMidX, strutJoinY)
  }
}
