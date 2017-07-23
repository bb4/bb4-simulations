/** Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer

import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm


/**
  * Create a zoom box while dragging.
  * Maintain aspect if control key or shift key while dragging.
  *
  * @author Barry Becker
  */
class ZoomHandler(var algorithm: FractalAlgorithm)
  extends MouseListener with MouseMotionListener {

  /** the physical representation of the dragged rectangle */
  private val zoomBox: ZoomBox = new ZoomBox()

  /** if control or shift key held down while dragging, maintain aspect ratio */
  private var keepAspectRatio = false


  /**
    * Remember the location of the mouse when pressed,
    * and determine if aspect ration should be preserved based on control/shit key.
    */
  override def mousePressed(e: MouseEvent): Unit = {
    keepAspectRatio = determineIfKeepAspectRation(e)
    zoomBox.setFirstCorner(e.getX, e.getY)
  }

  override def mouseDragged(e: MouseEvent): Unit = zoomBox.setSecondCorner(e.getX, e.getY)


  private def determineIfKeepAspectRation(e: MouseEvent) = e.isControlDown || e.isShiftDown

  override def mouseReleased(e: MouseEvent): Unit = {
    if (zoomBox.isValidBox) {
      val range = algorithm.getRange(zoomBox.getBox)
      algorithm.setRange(range)
    }
    zoomBox.clearBox()
  }

  /** Draw the bounding box rectangle when dragging.  */
  def render(g: Graphics, aspectRatio: Double): Unit = {
    zoomBox.render(g, aspectRatio, keepAspectRatio)
  }

  // unused mouse interface methods
  override def mouseMoved(e: MouseEvent): Unit = {}
  override def mouseClicked(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}
}
