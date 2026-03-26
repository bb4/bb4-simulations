// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt.event.MouseEvent
import javax.swing.event.MouseInputAdapter

import com.barrybecker4.math.function.ErrorFunction
import com.barrybecker4.simulation.water.model.Environment
import InteractionMath.X_SCALE

import scala.math.abs

/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
class InteractionHandler private[ui](var env: Environment) extends MouseInputAdapter {

  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0
  private var ground = false
  private var xpos = 0
  private val gaussFunc = new ErrorFunction()
  setEnvironment(env)

  def setEnvironment(env: Environment): Unit = {
    this.env = env
  }

  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  override def mousePressed(e: MouseEvent): Unit = {
    env.pause()
    val col = (currentX / env.xStep).max(0).min(env.width - 1)
    ground = currentY > env.floor(col)
    xpos = col
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    val ydiff = currentY - lastY
    if (ydiff == 0) return
    val absYDiff = abs(ydiff)

    val (s1, s2) = InteractionMath.horizontalRange(xpos, absYDiff, env.width)

    for (i <- s1 to s2) {
      val xdiff = abs(i - xpos)
      val heightDelta = InteractionMath.heightDeltaGaussian(absYDiff, xdiff, gaussFunc.getValue)
      applyTerrainOrWaterDelta(i, ydiff, heightDelta)
    }

    lastX = currentX
    lastY = currentY
    env.resume()
  }

  private def applyTerrainOrWaterDelta(i: Int, ydiff: Int, heightDelta: Double): Unit = {
    if (ground) {
      if (ydiff > 0) env.floor(i) += heightDelta
      else env.floor(i) -= heightDelta
    } else {
      if (ydiff > 0) env.h1(i) += heightDelta / X_SCALE
      else env.h1(i) -= heightDelta / X_SCALE
    }
  }
}
