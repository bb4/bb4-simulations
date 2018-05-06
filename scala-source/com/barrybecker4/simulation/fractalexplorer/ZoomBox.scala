// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.common.geometry.IntLocation
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D


object ZoomBox {
  private val BOUNDING_BOX_COLOR = new Color(255, 100, 0)
}

/**
  * Represents the zoom box used to zoom into a rectangular region (corner positions while dragging).
  * @author Barry Becker
  */
class ZoomBox {
  private var firstCorner: Option[IntLocation] = None
  private var secondCorner: Option[IntLocation] = None
  private var box: Option[Box] = None

  def setFirstCorner(x: Int, y: Int): Unit = {
    firstCorner = Some(IntLocation(y, x))
  }

  def setSecondCorner(x: Int, y: Int): Unit = {
    secondCorner = Some(IntLocation(y, x))
  }

  def getBox: Box = box.get

  def clearBox(): Unit = {
    firstCorner = None
    secondCorner = None
}

  def isValidBox: Boolean = box.isDefined && firstCorner.isDefined && !(firstCorner == secondCorner)

  /** Draw the bounding box if dragging.
    */
  def render(g: Graphics, aspectRatio: Double, keepAspectRatio: Boolean): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    if (firstCorner.isEmpty || secondCorner.isEmpty) return
    box = Some(findBox(aspectRatio, keepAspectRatio))
    g2.setColor(ZoomBox.BOUNDING_BOX_COLOR)
    val topLeft = box.get.getTopLeftCorner
    g2.drawRect(topLeft.getX, topLeft.getY, box.get.getWidth, box.get.getHeight)
  }

  private def findBox(aspectRatio: Double, keepAspectRatio: Boolean): Box = {
    var box = new Box(firstCorner.get, secondCorner.get)
    val topLeft = box.getTopLeftCorner
    var width = box.getWidth
    var height = box.getHeight
    if (keepAspectRatio) {
      if (width > height) height = (width / aspectRatio).toInt
      else width = (height * aspectRatio).toInt
      box = new Box(topLeft, IntLocation(topLeft.getY + height, topLeft.getX + width))
    }
    box
  }
}
