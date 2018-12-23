// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.rendering

import java.awt.{Color, Graphics2D}
import com.barrybecker4.simulation.water.model.Environment
import EnvironmentRenderer._


/**
  * Renders the moving water in its current state.
  * @author Barry Becker
  */
object EnvironmentRenderer {
  private val VECTOR_COLOR = new Color(200, 60, 30, 50)
  private val VECTOR_SCALE = 40.0
  private val OFFSET = 10

  private val GROUND_COLOR = new Color(90, 60, 50)
  private val WATER_COLOR = new Color(90, 210, 255)
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
  def render(g: Graphics2D) {
    drawBorder(g)
    drawGround(g)
    drawWater(g)
    //if (options.getShowVelocities) drawVectors(g)
  }

  private def drawBorder(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.drawRect(OFFSET, OFFSET, env.width, env.height)
  }

  private def drawGround(g: Graphics2D): Unit = {
    g.setColor(GROUND_COLOR)
    for (i <- 0 until env.width) {
      val j = Math.round(env.floor(i)).toInt
      g.drawRect(OFFSET + env.xStep * i, OFFSET + j - 1, env.xStep - 1, 2)
    }
  }

  private def drawWater(g: Graphics2D): Unit = {
    g.setColor(WATER_COLOR)
    for (i <- 0 until env.width) {
      val top = Math.round(env.h1(i)).toInt
      val bot = Math.round(env.floor(i)).toInt - 2
      if (top < bot) {
        g.drawRect(OFFSET + env.xStep * i, OFFSET + top, env.xStep - 1, bot - top)
      }
    }
  }

  private def drawVectors(g: Graphics2D): Unit = {
    g.setColor(EnvironmentRenderer.VECTOR_COLOR)
    for (j <- 0 until env.height) {
      for (i <- 0 until env.width) {
        //val u = grid.getU(i, j)
        //val v = grid.getV(i, j)
        val x = i + OFFSET
        val y = j + OFFSET
        g.drawLine(x, y,
          (i + VECTOR_SCALE).toInt + OFFSET,
          (j + VECTOR_SCALE).toInt + OFFSET)
      }
    }
  }
}
