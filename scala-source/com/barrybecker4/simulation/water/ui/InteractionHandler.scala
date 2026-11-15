// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt.event.MouseEvent
import javax.swing.event.MouseInputAdapter

import com.barrybecker4.math.function.ErrorFunction
import com.barrybecker4.simulation.water.model.Environment
import com.barrybecker4.simulation.water.rendering.EnvironmentRenderer
import InteractionMath.X_SCALE

import scala.math.abs

enum EditTarget {
  case Floor, Water
}

/**
  * Handle mouse interactions - converting them into physical manifestations.
  *
  * Uses [[Environment.pause]] / [[Environment.resume]] during a drag so the
  * integrator does not advance while the user reshapes terrain or water.
  * That is separate from the UI [[com.barrybecker4.simulation.common.ui.Simulator]]
  * pause flag, which stops the whole animation loop.
  *
  * @author Barry Becker
  */
class InteractionHandler private[ui](var env: Environment) extends MouseInputAdapter {

  private var currentX = 0
  private var currentY = 0
  private var lastY = 0
  private var editTarget: EditTarget = EditTarget.Water
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
    lastY = currentY
  }

  override def mousePressed(e: MouseEvent): Unit = {
    env.pause()
    currentX = e.getX
    currentY = e.getY
    val offset = EnvironmentRenderer.OFFSET
    val col = InteractionHandler.columnAt(currentX, env.xStep, env.width, offset)
    editTarget =
      if (InteractionHandler.envYFromPixel(currentY, offset) > env.floor(col)) EditTarget.Floor
      else EditTarget.Water
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

    lastY = currentY
    env.resume()
  }

  private def applyTerrainOrWaterDelta(i: Int, ydiff: Int, heightDelta: Double): Unit = {
    editTarget match {
      case EditTarget.Floor =>
        if (ydiff > 0) env.adjustFloor(i, heightDelta)
        else env.adjustFloor(i, -heightDelta)
      case EditTarget.Water =>
        if (ydiff > 0) env.adjustWaterSurface(i, heightDelta / X_SCALE)
        else env.adjustWaterSurface(i, -heightDelta / X_SCALE)
    }
  }
}

object InteractionHandler {
  /** Map a pixel x-coordinate to a clamped column index, accounting for render inset. */
  private[water] def columnAt(pixelX: Int, xStep: Int, width: Int, offset: Int): Int =
    ((pixelX - offset) / xStep).max(0).min(width - 1)

  /** Map a pixel y-coordinate into environment vertical space. */
  private[water] def envYFromPixel(pixelY: Int, offset: Int): Int =
    pixelY - offset
}
