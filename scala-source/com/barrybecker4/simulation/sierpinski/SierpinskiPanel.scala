// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import javax.swing._
import java.awt._


/**
  * Draws a recursive Sierpinksi triangle.
  * @author Barry Becker
  */
class SierpinskiPanel private[sierpinski]() extends JPanel {

  private var renderer = new SierpinskiRenderer

  private[sierpinski] def setRecursiveDepth(depth: Int): Unit = {
    renderer.setDepth(depth)
  }

  private[sierpinski] def setLineWidth(width: Float): Unit = {
    renderer.setLineWidth(width)
  }

  override def paint(g: Graphics): Unit = {
    renderer.setSize(getWidth, getHeight)
    renderer.paint(g)
  }
}
