// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem

import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}
import com.barrybecker4.common.geometry.IntLocation


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
class InteractionHandler(var panable: Panable) extends MouseListener with MouseMotionListener {
  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0

  /** pan the panable. */
  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY

    // pan by the amount dragged
    val loc = IntLocation(lastY - currentY, currentX - lastX)
    panable.incrementOffset(loc)

    lastX = currentX
    lastY = currentY
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  override def mouseExited(e: MouseEvent): Unit = {}
  override def mousePressed(e: MouseEvent): Unit = {}
  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseClicked(e: MouseEvent): Unit = {
    lastX = e.getX
    lastY = e.getY
  }
}
