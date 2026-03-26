// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway

import com.barrybecker4.simulation.conway.model.ConwayModel
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

/**
  * Mouse painting: left button sets cells alive, right button clears them.
  *
  * @param scale pixels per cell (must stay in sync with [[com.barrybecker4.simulation.conway.model.ConwayModel]] scale via `setScale`).
  * @author Barry Becker
  */
class InteractionHandler(var model: ConwayModel, var scale: Double)
  extends MouseListener with MouseMotionListener {

  private var currentX = 0
  private var currentY = 0
  private val brushRadius = 1
  private var mouse1Down = false
  private var mouse3Down = false

  def setScale(scale: Double): Unit = this.scale = scale

  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    doBrush()
  }

  private def doBrush(): Unit = {
    val col = (currentX / scale).toInt
    val row = (currentY / scale).toInt
    val cols = model.gridCols
    val rows = model.gridRows
    val startCol = math.max(0, col - brushRadius)
    val stopCol = math.min(cols, col + brushRadius + 1)
    val startRow = math.max(0, row - brushRadius)
    val stopRow = math.min(rows, row + brushRadius + 1)
    for {
      c <- startCol until stopCol
      r <- startRow until stopRow
    } do
      if mouse3Down then model.setDead(r, c)
      else if mouse1Down then model.setAlive(r, c)
    model.doRender()
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
  }

  override def mouseClicked(e: MouseEvent): Unit = doBrush()

  override def mousePressed(e: MouseEvent): Unit = {
    e.getButton match {
      case MouseEvent.BUTTON1 => mouse1Down = true
      case MouseEvent.BUTTON3 => mouse3Down = true
      case _ => ()
    }
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    e.getButton match {
      case MouseEvent.BUTTON1 => mouse1Down = false
      case MouseEvent.BUTTON3 => mouse3Down = false
      case _ => ()
    }
  }

  override def mouseEntered(e: MouseEvent): Unit = ()
  override def mouseExited(e: MouseEvent): Unit = ()
}
