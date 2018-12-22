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
  private val NUM_STEPS_PER_FRAME = 1

  private val GROUND_COLOR = new Color(70, 50, 40)
  private val WATER_COLOR = new Color(100, 200, 255)
}

final class EnvironmentRenderer(var env: Environment, var options: RenderingOptions) {

  private def drawGround(g: Graphics2D): Unit = {
    g.setColor(GROUND_COLOR)
    for (i <- 0 until env.width) {
      val j = Math.round(env.floor(i)).toInt
      g.drawRect(env.xStep * i, j - 1, env.xStep - 1, 2)
    }
  }

  private def drawWater(g: Graphics2D): Unit = {
    g.setColor(WATER_COLOR)
    for (i <- 0 until env.width) {
      val top = Math.round(env.h1(i)).toInt
      val bot = Math.round(env.floor(i)).toInt - 2
      if (top < bot) {
        g.drawRect(env.xStep * i, top, env.xStep - 1, bot - top)
      }
    }
  }

  def getOptions: RenderingOptions = options

  def stepForward(dt: Double): Double = {
    for (i <- 0 until NUM_STEPS_PER_FRAME) {
      env.integrate(dt)
    }
    dt
  }

  /** Render the Environment on the screen. */
  def render(g: Graphics2D) {
    g.drawRect(EnvironmentRenderer.OFFSET, EnvironmentRenderer.OFFSET, env.width, env.height)
    drawGround(g)
    drawWater(g)
    //if (options.getShowVelocities) drawVectors(g)
  }

  private def drawVectors(g: Graphics2D): Unit = {
    g.setColor(EnvironmentRenderer.VECTOR_COLOR)
    for (j <- 0 until env.height) {
      for (i <- 0 until env.width) {
        //val u = grid.getU(i, j)
        //val v = grid.getV(i, j)
        val x = i + EnvironmentRenderer.OFFSET
        val y = j + EnvironmentRenderer.OFFSET
        g.drawLine(x, y,
          (i + EnvironmentRenderer.VECTOR_SCALE).toInt + EnvironmentRenderer.OFFSET,
          (j + EnvironmentRenderer.VECTOR_SCALE).toInt + EnvironmentRenderer.OFFSET)
      }
    }
  }
}
