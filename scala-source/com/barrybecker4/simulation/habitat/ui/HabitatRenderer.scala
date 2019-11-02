// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import java.awt._

import com.barrybecker4.simulation.habitat.creatures.{Creature, Populations}


/**
  * This class draws a the global hab and all the creatures in it.
  * @author Barry Becker
  */
object HabitatRenderer {
  private val SIZE_SCALE = 0.001
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
      g2.setColor(pop.creatureType.color)
      for (creature <- pop.creatures) {
        drawCreature(creature, g2)
      }
    }
  }

  private def drawCreature(creature: Creature, g2: Graphics2D): Unit = {
    val w = (creature.getSize * width * HabitatRenderer.SIZE_SCALE + 1.0).toInt
    val h = (creature.getSize * height * HabitatRenderer.SIZE_SCALE + 1.0).toInt
    val centerX = (creature.getLocation.x * width).toInt
    val centerY = (creature.getLocation.y * height).toInt
    g2.fillOval(centerX - w / 2, centerY - h / 2, w, h)
    val vectorEndpointX = (centerX + creature.getVelocity.x * width).toInt
    val vectorEndpointY = (centerY + creature.getVelocity.y * height).toInt
    g2.drawLine(centerX, centerY, vectorEndpointX, vectorEndpointY)
    if (creature.isPursuing) g2.drawOval(centerX - w, centerY - h, 2 * w, 2 * h)
  }
}
