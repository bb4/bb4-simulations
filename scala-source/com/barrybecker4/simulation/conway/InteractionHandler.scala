// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway

import com.barrybecker4.simulation.conway.model.ConwayModel
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * Using this handler, you can lower the gave walls.
  * @param scale amount of the effect
  * @author Barry Becker
  */
class InteractionHandler(var model: ConwayModel, var scale: Double)
  extends MouseListener with MouseMotionListener {
  private var currentX = 0
  private var currentY = 0
  private val brushRadius = 1
  private var mouse1Down = false
  private var mouse3Down = false

  def setScale(scale: Double): Unit = { this.scale = scale }

  /**
    * Lowers (or raises) cave walls when dragging.
    * Left mouse lowers; right mouse drag raises.
    */
  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    doBrush()
  }

  private def doBrush(): Unit = {
    val i = (currentX / scale).toInt
    val j = (currentY / scale).toInt
    // apply the change to a convolution kernel area
    val startX = Math.max(1, i - brushRadius)
    val stopX = Math.min(model.getWidth, i + brushRadius)
    val startY = Math.max(1, j - brushRadius)
    val stopY = Math.min(model.getHeight, j + brushRadius)
    // adjust by this so that there is not a discontinuity at the periphery
    val minWt = 0.9 / brushRadius
    for (ii <- startX until stopX) {
      for (jj <- startY until stopY) {
        val weight = getWeight(i, j, ii, jj, minWt)
        applyChange(ii, jj, weight)
      }
    }
    model.doRender()
  }

  /** @return the weight is 1 / distance. */
  private def getWeight(i: Int, j: Int, ii: Int, jj: Int, minWt: Double) = {
    val deltaX = i.toDouble - ii
    val deltaY = j.toDouble - jj
    var distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
    if (distance < 0.5) distance = 1.0
    1.0 / distance - minWt
  }

  /** Make waves or adds ink depending on which mouse key is being held down. */
  private def applyChange(i: Int, j: Int, weight: Double): Unit = {
    val sign = if (mouse3Down) -1 else 1
    model.setAlive(j, i)
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
  }

  /*** The following methods implement MouseListener */
  override def mouseClicked(e: MouseEvent): Unit = { doBrush() }

  /** Remember the mouse button that is pressed. */
  override def mousePressed(e: MouseEvent): Unit = {
    mouse1Down = e.getButton == MouseEvent.BUTTON1
    mouse3Down = e.getButton == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}
}
