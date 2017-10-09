// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import java.awt.Graphics2D
import java.awt._
import RenderablePart._


/**
  * @author Barry Becker   (Sep 25, 2005)
  */
object Base {
  private val BASE_WIDTH = 400
  private val STRUT_BASE_HALF_WIDTH = 50
  private val BASE_STROKE = new BasicStroke(2.0f)
  private val BASE_COLOR = new Color(10, 40, 160)
}

class Base private[model]() extends RenderablePart {
  override def render(g2: Graphics2D, scale: Double): Unit = {
    g2.setStroke(Base.BASE_STROKE)
    g2.setColor(Base.BASE_COLOR)
    g2.draw3DRect((scale * BASE_X).toInt, BASE_Y, (scale * Base.BASE_WIDTH).toInt, (scale * 10.0).toInt, false)
    g2.drawLine((scale * (STRUT_BASE_X - Base.STRUT_BASE_HALF_WIDTH)).toInt,
      BASE_Y, (scale * STRUT_BASE_X).toInt,
      (BASE_Y - scale * SCALE_FACTOR * height).toInt)
    g2.drawLine((scale * (STRUT_BASE_X + Base.STRUT_BASE_HALF_WIDTH)).toInt, BASE_Y,
      (scale * STRUT_BASE_X).toInt,
      (BASE_Y - scale * SCALE_FACTOR * height).toInt)
  }
}
