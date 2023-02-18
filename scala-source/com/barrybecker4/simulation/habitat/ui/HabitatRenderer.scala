// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import java.awt.*
import com.barrybecker4.simulation.habitat.creatures.Creature
import com.barrybecker4.simulation.habitat.creatures.populations.Populations
import com.barrybecker4.simulation.habitat.ui.HabitatRenderer.HEALTH_BAR_COLOR


/**
  * This class draws a the global hab and all the creatures in it.
  * @author Barry Becker
  */
object HabitatRenderer {
  private val SIZE_SCALE = 0.001
  private val HEALTH_BAR_COLOR = new Color(0, 145, 0)
}

class HabitatRenderer private[habitat](var populations: Populations) {
  private var width = 0
  private var height = 0

  def setSize(width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
  }

  /** draw the cartesian functions */
  def paint(g: Graphics): Unit = {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]

    for (pop <- populations) {
      for (creature <- pop.creatures) {
        drawCreature(creature, g2)
      }
    }
  }

  private def drawCreature(creature: Creature, g2: Graphics2D): Unit = {
    g2.setColor(creature.cType.color)
    val w = (creature.getSize * width * HabitatRenderer.SIZE_SCALE + 1.0).toInt
    val h = (creature.getSize * height * HabitatRenderer.SIZE_SCALE + 1.0).toInt
    val centerX = (creature.getLocation.x * width).toInt
    val centerY = (creature.getLocation.y * height).toInt
    g2.fillOval(centerX - w / 2, centerY - h / 2, w, h)
    val vectorEndpointX = (centerX + creature.getVelocity.x * width).toInt
    val vectorEndpointY = (centerY + creature.getVelocity.y * height).toInt
    g2.drawLine(centerX, centerY, vectorEndpointX, vectorEndpointY)
    if (creature.isPursuing) g2.drawOval(centerX - w, centerY - h, 2 * w, 2 * h)

    // draw line to prey
    if (creature.isPursuing) {
      g2.setColor(Color.BLACK)
      val prey = creature.getPrey.get
      val preyX = (prey.getLocation.x * width).toInt
      val preyY = (prey.getLocation.y * height).toInt
      g2.drawLine(centerX, centerY, preyX, preyY)
    }
    // draw starvation bar
    g2.setColor(HEALTH_BAR_COLOR)
    val healthSize = creature.getHealth / 4
    g2.drawLine(centerX - 8, centerY + 4, centerX + healthSize, centerY + 4)
  }
}
