// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.trebuchet.rendering

import java.awt.Graphics2D


abstract class AbstractPartRenderer {
  
  def render(g2: Graphics2D, scale: Double, viewHeight: Int): Unit

}
