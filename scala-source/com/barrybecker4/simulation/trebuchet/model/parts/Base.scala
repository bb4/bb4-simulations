// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.Variables
import com.barrybecker4.simulation.trebuchet.model.parts.RenderablePart.*
import com.barrybecker4.simulation.trebuchet.model.parts.{Base, RenderablePart}

import java.awt.*


/**
  * @author Barry Becker   (Sep 25, 2005)
  */
object Base {
  private val BASE_WIDTH = 400
  private val STRUT_BASE_HALF_WIDTH = 50
  private val BASE_STROKE = new BasicStroke(2.0f)
  private val BASE_COLOR = new Color(10, 40, 160)
}

class Base(variables: Variables) extends RenderablePart {
  override def render(g2: Graphics2D, scale: Double, height: Int): Unit = {
    g2.setStroke(Base.BASE_STROKE)
    g2.setColor(Base.BASE_COLOR)

    val x = (scale * BASE_X).toInt
    val y = height - BASE_Y
    val width = (scale * Base.BASE_WIDTH).toInt
    val baseHeight = (scale * 10.0).toInt
    val strutBackX = (scale * (STRUT_BASE_X - Base.STRUT_BASE_HALF_WIDTH)).toInt
    val strutMidX = (scale * STRUT_BASE_X).toInt
    val strutFrontX = (scale * (STRUT_BASE_X + Base.STRUT_BASE_HALF_WIDTH)).toInt
    val strutJoinY = (y - scale * SCALE_FACTOR * variables.height).toInt 

    g2.draw3DRect(x, y, width, baseHeight, false)
    g2.drawLine(strutBackX, y, strutMidX, strutJoinY)
    g2.drawLine(strutFrontX, y, strutMidX, strutJoinY)
  }
}
