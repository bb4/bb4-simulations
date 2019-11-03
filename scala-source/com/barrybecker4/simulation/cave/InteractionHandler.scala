/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.CaveModel
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import InteractionHandler.SQRT2

object InteractionHandler {
  val SQRT2: Double = Math.sqrt(2)
}

/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * Using this handler, you can lower the cave walls.
  * @author Barry Becker
  */
class InteractionHandler(var cave: CaveModel, var scale: Double)
  extends MouseListener with MouseMotionListener {

  private var currentX = 0
  private var currentY = 0
  private var brushRadius = CaveModel.DEFAULT_BRUSH_RADIUS
  private var brushStrength = CaveModel.DEFAULT_BRUSH_STRENGTH
  private var lastX = 0
  private var lastY = 0
  private var mouse1Down = false
  private var mouse3Down = false

  def setScale(scale: Double): Unit =  { this.scale = scale }
  def setBrushRadius(rad: Int): Unit = { brushRadius = rad }
  def setBrushStrength(strength: Double): Unit = { brushStrength = strength }

  /** Lowers (or raises) cave walls when dragging. Left mouse lowers; right mouse drag raises. */
  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    doBrush()
    lastX = currentX
    lastY = currentY
  }

  private def doBrush(): Unit = {
    val i = (currentX / scale).toInt
    val j = (currentY / scale).toInt
    // apply the change to a convolution kernel area
    val startX = Math.max(1, i - brushRadius)
    val stopX = Math.min(cave.getWidth, i + brushRadius)
    val startY = Math.max(1, j - brushRadius)
    val stopY = Math.min(cave.getHeight, j + brushRadius)
    // adjust by this so that there is not a discontinuity at the periphery
    val minWt = 0.9 / (SQRT2 * brushRadius)
    for (ii <- startX to stopX) {
      for (jj <- startY to stopY) {
        val weight = getWeight(i, j, ii, jj, minWt)
        applyChange(ii, jj, weight)
      }
    }
    cave.doRender()
  }

  /** @return the weight is 1 / distance. */
  private def getWeight(i: Int, j: Int, ii: Int, jj: Int, minWt: Double): Double = {
    val deltaX = i.toDouble - ii
    val deltaY = j.toDouble - jj
    var distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
    if (distance < 0.5) distance = 1.0
    1.0 / distance - minWt
  }

  /** Make waves or adds ink depending on which mouse key is being held down. */
  private def applyChange(i: Int, j: Int, weight: Double): Unit = {
    if (mouse1Down || mouse3Down) {
      val sign = if (mouse1Down) 1 else -1
      cave.incrementHeight(i, j, sign * brushStrength * weight)
    }
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  /** The following methods implement MouseListener */
  override def mouseClicked(e: MouseEvent): Unit = {doBrush() }

  /** Remember the mouse button that is pressed. */
  override def mousePressed(e: MouseEvent): Unit = {
    mouse1Down = e.getButton == MouseEvent.BUTTON1
    mouse3Down = e.getButton == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}
}
