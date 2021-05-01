// Copyright by Barry G. Becker, 2016-2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
object InteractionHandler {
  private[ui] val DEFAULT_FORCE = 2.0f
  private[ui] val DEFAULT_SOURCE_DENSITY = 1.0f
}

class InteractionHandler private[ui](var env: FluidEnvironment, var scale: Double,
                                     var force: Double = InteractionHandler.DEFAULT_FORCE)
  extends MouseListener with MouseMotionListener {

  private var sourceDensity: Double = InteractionHandler.DEFAULT_SOURCE_DENSITY
  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0
  private var mouse1Down = false
  private var mouse3Down = false

  def setForce(force: Double): Unit = { this.force = force }
  private[ui] def setSourceDensity(sourceDensity: Double): Unit = { this.sourceDensity = sourceDensity }

  /** Make waves or adds ink when dragging depending on the mouse key held down. */
  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    val i = (currentX / scale).toInt
    val j = (currentY / scale).toInt
    // apply the change to a convolution kernel area
    val startX = Math.max(1, i - 1)
    val stopX = Math.min(env.getWidth, i + 1)
    val startY = Math.max(1, j - 1)
    val stopY = Math.min(env.getHeight, j + 1)
    for (ii <- startX until stopX) {
      for (jj <- startY until stopY) {
        val weight = if (ii == i && jj == j) 1.0f
        else 0.3f
        applyChange(ii, jj, weight)
      }
    }
    lastX = currentX
    lastY = currentY
  }

  /** Make waves or adds ink depending on which mouse key is being held down. */
  private def applyChange(i: Int, j: Int, weight: Double): Unit = {
    // if the left mouse is down, make waves
    if (mouse1Down) {
      val fu = weight * force * (currentX - lastX) / scale
      val fv = weight * force * (currentY - lastY) / scale
      env.incrementU(i, j, fu)
      env.incrementV(i, j, fv)
    }
    else if (mouse3Down) { // if the right mouse is down, add ink (density)
      env.incrementDensity(i, j, weight * sourceDensity)
    }
    else println("dragged with no button down")
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  /** The following methods implement MouseListener */
  override def mouseClicked(e: MouseEvent): Unit = {}

  /** Remember the mouse button that is pressed. */
  override def mousePressed(e: MouseEvent): Unit = {
    mouse1Down = e.getButton == MouseEvent.BUTTON1
    mouse3Down = e.getButton == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}
}
