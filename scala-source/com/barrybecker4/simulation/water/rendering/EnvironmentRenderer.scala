// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.rendering

import java.awt.{Color, Graphics2D}
import com.barrybecker4.simulation.water.model.Environment
import EnvironmentRenderer._
import scala.math.round


/**
  * Renders the moving water in its current state.
  * @author Barry Becker
  */
object EnvironmentRenderer {
  private val OFFSET = 10
  private val GROUND_COLOR = new Color(90, 60, 50)
  private val WATER_COLOR = new Color(90, 210, 255)
  private val VECTOR_COLOR = new Color(20, 40, 160)
  private val VECTOR_SCALE = 8.0
  private val VELOCITY_SUBSAMPLE = 4

  /**
    * Vertical stroke endpoints for a surface velocity sample.
    * Positive (h1 - h0) means the surface moved downward in screen y.
    * @return (yStart, yEnd) in pixel coordinates including OFFSET
    */
  private[water] def velocityStrokeY(
      surfaceY: Double,
      h0: Double,
      h1: Double,
      scale: Double = VECTOR_SCALE
  ): (Int, Int) = {
    val y0 = OFFSET + round(surfaceY).toInt
    val y1 = y0 + round((h1 - h0) * scale).toInt
    (y0, y1)
  }
}

final class EnvironmentRenderer(var env: Environment, var options: RenderingOptions) {

  def getOptions: RenderingOptions = options

  def stepForward(dt: Double): Double = {
    env.integrate(dt)
    dt
  }

  def setEnvironment(env: Environment): Unit = {
    this.env = env
  }

  /** Render the Environment on the screen. */
  def render(g: Graphics2D): Unit = {
    drawBorder(g)
    drawGround(g)
    drawWater(g)
    if (options.showVelocities) drawVelocities(g)
  }

  private def drawBorder(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.drawRect(OFFSET, OFFSET, env.width, env.height)
  }

  private def drawGround(g: Graphics2D): Unit = {
    g.setColor(GROUND_COLOR)
    for (i <- 0 until env.width) {
      val j = round(env.floor(i)).toInt
      g.drawRect(OFFSET + env.xStep * i, OFFSET + j - 1, env.xStep - 1, 2)
    }
  }

  private def drawWater(g: Graphics2D): Unit = {
    g.setColor(WATER_COLOR)
    for (i <- 0 until env.width) {
      val top = round(env.h1(i)).toInt
      val bot = round(env.floor(i)).toInt - 2
      if (top < bot) {
        g.drawRect(OFFSET + env.xStep * i, OFFSET + top, env.xStep - 1, bot - top)
      }
    }
  }

  private def drawVelocities(g: Graphics2D): Unit = {
    g.setColor(VECTOR_COLOR)
    var i = 0
    while (i < env.width) {
      if (env.h1(i) < env.floor(i)) {
        val x = OFFSET + env.xStep * i + env.xStep / 2
        val (y0, y1) = velocityStrokeY(env.h1(i), env.h0(i), env.h1(i))
        g.drawLine(x, y0, x, y1)
      }
      i += VELOCITY_SUBSAMPLE
    }
  }
}
