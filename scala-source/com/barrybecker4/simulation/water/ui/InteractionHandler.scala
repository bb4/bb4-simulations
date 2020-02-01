// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}
import com.barrybecker4.math.function.ErrorFunction
import com.barrybecker4.simulation.water.model.Environment
import InteractionHandler.X_SCALE

/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
object InteractionHandler {
  private[ui] val DEFAULT_FORCE = 3.0f
  private[ui] val DEFAULT_SOURCE_DENSITY = 1.0f
  private val X_SCALE = 10
}

class InteractionHandler private[ui](var env: Environment) extends MouseListener with MouseMotionListener {

  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0
  private var mouse1Drag = false
  private var ground = false
  private var xpos = 0
  private val gaussFunc = new ErrorFunction()
  setEnvironment(env)

  def setEnvironment(env: Environment) {
    this.env = env
  }

  /** Adjusts the water or land depending on whether you are above or below the ground level when you start dragging.
    */
  override def mouseDragged(e: MouseEvent) {
    currentX = e.getX
    currentY = e.getY
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
    env.pause()
    mouse1Drag = true
    ground = currentY > env.floor(currentX)
    xpos = currentX / env.xStep
  }

  override def mouseReleased(e: MouseEvent): Unit = {

    val i = currentX
    val j = currentY
    val ydiff = currentY - lastY  // amount dragged vertically
    if (ydiff == 0) return
    val absYDiff = Math.abs(ydiff)

    // range in x that is impacted is proportional to the amount dragged vertically
    var s1 = xpos - X_SCALE * absYDiff
    if (s1 < 0) s1 = 0
    var s2 = xpos + X_SCALE * absYDiff
    if (s2 >= env.width) s2 = env.width - 1

    // Over this range apply gaussian kernel function centered at xpos
    for (i <- s1 to s2) {
      val xdiff = Math.abs(i - xpos)
      val heightDelta = 0.3 * absYDiff * (1.0 - gaussFunc.getValue(0.5 * xdiff / absYDiff))
      //      if (i == xpos) {
      //        println(s"heightDelta = $heightDelta  floor($i) = ${env.floor(i)} absYDiff = $absYDiff" )
      //      }
      if (ground) {
        if (ydiff > 0) {
          if (i == xpos) println("floor before = " + env.floor(i))
          env.floor(i) += heightDelta // going down
          if (i == xpos) println("floor after = " + env.floor(i))
        } else {
          env.floor(i) -= heightDelta // going up
        }
      }
      else {
        if (ydiff > 0)
          env.h1(i) += heightDelta / X_SCALE        // going down
        else
          env.h1(i) -= heightDelta / X_SCALE        // going up
      }
    }

    lastX = currentX
    lastY = currentY
    env.resume()
    mouse1Drag = false
  }

  override def mouseEntered(e: MouseEvent) {}
  override def mouseExited(e: MouseEvent) {}
}
