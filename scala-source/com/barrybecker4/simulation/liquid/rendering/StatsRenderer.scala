// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.liquid.rendering

import com.barrybecker4.simulation.liquid.model.environment.MpmEnvironment

import java.awt.{Color, Graphics2D}

class StatsRenderer(environment: MpmEnvironment) {

  private val fpsCalculator = new FpsCalculator()
  
  def renderStats(g2d: Graphics2D, xpos: Int, ypos: Int): Unit = {
    g2d.setColor(Color.WHITE)
    g2d.drawString(s"Particles: ${environment.getParticles.size}", xpos, ypos)
    g2d.drawString(s"Frame: ${environment.getIter}", xpos, ypos + 20)

    val fps = fpsCalculator.update()
    g2d.drawString(s"FPS: $fps", xpos, ypos + 40)
  }
}
