// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}
import com.barrybecker4.simulation.water.model.Environment


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
object InteractionHandler {
  private[ui] val DEFAULT_FORCE = 3.0f
  private[ui] val DEFAULT_SOURCE_DENSITY = 1.0f
}

class InteractionHandler private[ui](var env: Environment) extends MouseListener with MouseMotionListener {

  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0
  private var mouse1Drag = false
  private var ground = false
  private var xpos = 0
  private var oldFloor: Array[Double] = _
  setEnvironment(env)

  def setEnvironment(env: Environment) {
    this.env = env
    oldFloor = Array.ofDim(env.width)
    env.floor.copyToArray(oldFloor)
  }

  /** Adjusts the water or land depending on whether you are above or below the ground level when you start dragging.
    */
  override def mouseDragged(e: MouseEvent) {
    currentX = e.getX
    currentY = e.getY
    val i = currentX
    val j = currentY
    val ydiff = (currentY - lastY) / 2

    var s1 = xpos - Math.abs(ydiff)
    if (s1 < 0) s1 = 0
    var s2 = xpos + Math.abs(ydiff)
    if (s2 >= env.width) s2 = env.width - 1

    if (ground) {
      for (i <- s1 to s2) {
        val xdiff = Math.abs(i - xpos)
        if (ydiff > 0)
          env.floor(i) = oldFloor(i) + ydiff - xdiff          // going down
        else
          env.floor(i) = oldFloor(i) - (Math.abs(ydiff) - xdiff)   // going up
      }
    }
    // adjust water height
    for (i <- s1 to s2) {
      val xdiff = Math.abs(i - xpos)
      if (ydiff > 0)
        env.h1(i) = env.h0(i) + ydiff - xdiff      // going down
      else
        env.h1(i) = env.h0(i) - (Math.abs(ydiff) - xdiff)   // going up
    }

    lastX = currentX
    lastY = currentY
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
    mouse1Drag = true
    ground = currentY > env.floor(currentX)
    xpos = currentX / env.xStep
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    mouse1Drag = false
//    env.h1.copyToArray(env.h0)
    env.floor.copyToArray(oldFloor)
  }

  override def mouseEntered(e: MouseEvent) {}
  override def mouseExited(e: MouseEvent) {}
}
