// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.trebuchet.rendering

import java.awt.{BasicStroke, Color}

object RenderingConstants {

  val VELOCITY_VECTOR_STROKE = new BasicStroke(1.0f)
  val FORCE_VECTOR_STROKE = new BasicStroke(0.3f)
  val VELOCITY_VECTOR_COLOR = new Color(70, 10, 255, 200)
  val FORCE_VECTOR_COLOR = new Color(200, 0, 80, 200)

  var showVelocityVectors = true
  var showForceVectors = true
  
}
