// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}

import com.barrybecker4.simulation.water.model


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
object InteractionHandler {
  private[ui] val DEFAULT_FORCE = 3.0f
  private[ui] val DEFAULT_SOURCE_DENSITY = 1.0f
}

class InteractionHandler private[ui](val width: Int, val height: Int) extends MouseListener with MouseMotionListener {

  private var force: Double = InteractionHandler.DEFAULT_FORCE
  private var sourceDensity: Double = InteractionHandler.DEFAULT_SOURCE_DENSITY
  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0
  private var mouse1Down = false
  private var mouse3Down = false

  def setForce(force: Double): Unit = { this.force = force }
  private[ui] def setSourceDensity(sourceDensity: Double) { this.sourceDensity = sourceDensity }

  /** Make waves or adds ink when dragging depending on the mouse key held down. */
  override def mouseDragged(e: MouseEvent) {
    currentX = e.getX
    currentY = e.getY
    val i = currentX
    val j = currentY
    // apply the change to a convolution kernel area
    val startX = Math.max(1, i - 1)
    val stopX = Math.min(width, i + 1)
    val startY = Math.max(1, j - 1)
    val stopY = Math.min(height, j + 1)
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
  private def applyChange(i: Int, j: Int, weight: Double) {
    // if the left mouse is down, make waves
    if (mouse1Down) {
      val fu = weight * force * (currentX - lastX)
      val fv = weight * force * (currentY - lastY)
      //grid.incrementU(i, j, fu)
      //grid.incrementV(i, j, fv)
    }
    else if (mouse3Down) { // if the right mouse is down, add ink (density)
      //grid.incrementDensity(i, j, weight * sourceDensity)
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
  override def mouseClicked(e: MouseEvent) {}

  /** Remember the mouse button that is pressed. */
  override def mousePressed(e: MouseEvent) {
    mouse1Down = e.getButton == MouseEvent.BUTTON1
    mouse3Down = e.getButton == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent) {}
  override def mouseEntered(e: MouseEvent) {}
  override def mouseExited(e: MouseEvent) {}
}
