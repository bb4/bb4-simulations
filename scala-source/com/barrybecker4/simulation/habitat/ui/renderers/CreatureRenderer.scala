// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.renderers

import com.barrybecker4.simulation.habitat.creatures.Creature
import com.barrybecker4.simulation.habitat.creatures.populations.Habitat
import com.barrybecker4.simulation.habitat.ui.renderers.CreatureRenderer.*

import java.awt.*


/**
  * This class draws a the global hab and all the creatures in it.
  * @author Barry Becker
  */
object CreatureRenderer {
  private val SIZE_SCALE = 0.001
  private val HEALTH_BAR_COLOR = new Color(0, 145, 0)
  private val BLOOD_RED = new Color(210, 20, 0)
  private val HEALTH_BAR_X_OFFSET = 4
  private val DEBUG = true
}

case class CreatureRenderer(width: Int, height: Int) {

  def drawCreature(creature: Creature, g2: Graphics2D): Unit = {

    // if being eaten, and has ability to move, draw some blood
    if (creature.isBeingEaten && creature.cType.maxSpeed > 0) {
      drawBlood(creature, g2)
      return
    }
    g2.setColor(creature.cType.color)
    val w = (creature.getSize * width * SIZE_SCALE + 1.0).toInt
    val h = (creature.getSize * height * SIZE_SCALE + 1.0).toInt
    val centerX = (creature.getLocation.x * width).toInt
    val centerY = (creature.getLocation.y * height).toInt
    g2.fillOval(centerX - w / 2, centerY - h / 2, w, h)
    val vectorEndpointX = (centerX + creature.getVelocity.x * width).toInt
    val vectorEndpointY = (centerY + creature.getVelocity.y * height).toInt
    g2.drawLine(centerX, centerY, vectorEndpointX, vectorEndpointY)

    if (creature.isPursuing) 
      g2.drawOval(centerX - w, centerY - h, 2 * w, 2 * h)

    if (DEBUG) {
      // draw line to prey
      if (creature.isPursuing) {
        drawConnectingLine(creature, g2)
      }
      // draw starvation bar
      g2.setColor(HEALTH_BAR_COLOR)
      val healthSize = creature.getHealth / 4
      val x = centerX - HEALTH_BAR_X_OFFSET
      g2.drawLine(x, centerY + 4, x + healthSize, centerY + 4)
    }
  }

  private def drawBlood(creature: Creature, g2: Graphics2D): Unit = {
    g2.setColor(BLOOD_RED)
    val w = (creature.getSize * width * (1.1 + Math.random()) * SIZE_SCALE + 1).toInt
    val h = (creature.getSize * height * (1.1 + Math.random()) * SIZE_SCALE + 1).toInt
    val centerX = (creature.getLocation.x * width).toInt
    val centerY = (creature.getLocation.y * height).toInt
    g2.fillOval(centerX - w / 2, centerY - h / 2, w, h)
  }

  private def drawConnectingLine(creature: Creature, g2: Graphics2D): Unit = {
    g2.setColor(Color.BLACK)
    val prey = creature.getPrey.get
    var preyX = prey.getLocation.x
    var preyY = prey.getLocation.y

    var creatureX = creature.getLocation.x
    var creatureY = creature.getLocation.y
    val xdelta = creature.getLocation.x - preyX
    val ydelta = creature.getLocation.y - preyY
    if (Math.abs(xdelta) > 0.5) {
      if (preyX < creatureX) {
        if (preyX < (1 - creatureX)) preyX += 1.0
        else creatureX -= 1.0
      }
      else {
        if ((1 - preyX) < creatureX) preyX += 1.0
        else creatureX -= 1.0
      }
    }
    if (Math.abs(ydelta) > 0.5) {
      if (preyY < creatureY) {
        if (preyY < (1 - creatureY)) preyY += 1.0
        else creatureY -= 1.0
      }
      else {
        if ((1 - preyY) < creatureY) preyY += 1.0
        else creatureY -= 1.0
      }
    }

    g2.drawLine(
      (creatureX * width).toInt, (creatureY * height).toInt,
      (preyX * width).toInt, (preyY * height).toInt
    )
  }
}
