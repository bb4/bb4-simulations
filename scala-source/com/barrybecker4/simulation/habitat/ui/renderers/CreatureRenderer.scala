// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.renderers

import com.barrybecker4.simulation.habitat.creatures.Creature
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
  var DEBUG = false
}

case class CreatureRenderer(width: Int, height: Int) {

  def drawCreature(creature: Creature, g2: Graphics2D): Unit = {

    val centerX = (creature.getLocation.x * width).toInt
    val centerY = (creature.getLocation.y * height).toInt

    // if being eaten, and has ability to move, draw some blood
    if (creature.isBeingEaten && creature.cType.maxSpeed > 0) {
      drawBlood(creature, g2)
      return
    }
    g2.setColor(creature.cType.color)
    val w = (creature.getSize * width * SIZE_SCALE + 1.0).toInt
    val h = (creature.getSize * height * SIZE_SCALE + 1.0).toInt
    g2.fillOval(centerX - w / 2, centerY - h / 2, w, h)
    val vectorEndpointX = (centerX + creature.getVelocity.x * width).toInt
    val vectorEndpointY = (centerY + creature.getVelocity.y * height).toInt
    g2.drawLine(centerX, centerY, vectorEndpointX, vectorEndpointY)

    if (creature.isPursuing) 
      g2.drawOval(centerX - w, centerY - h, 2 * w, 2 * h)

    if (DEBUG) {
      drawLineToPrey(creature, g2)
      drawStarvationBar(creature, centerX, centerY, g2)
    }
  }


  private def drawLineToPrey(creature: Creature, g2: Graphics2D): Unit =  {
    if (creature.isPursuing) {
        drawConnectingLine(creature, g2)
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

  private def drawStarvationBar(creature: Creature, centerX: Int, centerY: Int, g2: Graphics2D): Unit = {
    g2.setColor(HEALTH_BAR_COLOR)
    val healthSize = creature.getHealth / 4
    val y = (centerY + 3 + creature.getSize * height * SIZE_SCALE / 2).toInt
    g2.drawLine(centerX - 8, y, centerX + healthSize, y)
  }

  private def drawConnectingLine(creature: Creature, g2: Graphics2D): Unit = {
    g2.setColor(Color.BLACK)
    val prey = creature.getPrey.get
    val (preyX, creatureX) =
      adjustTorusAxisForLine(prey.getLocation.x, creature.getLocation.x)
    val (preyY, creatureY) =
      adjustTorusAxisForLine(prey.getLocation.y, creature.getLocation.y)

    g2.drawLine(
      (creatureX * width).toInt, (creatureY * height).toInt,
      (preyX * width).toInt, (preyY * height).toInt
    )
  }

  /** On a torus, choose shifted coordinates so the line segment is the shortest in pixel space. */
  private def adjustTorusAxisForLine(preyCoord: Double, creatureCoord: Double): (Double, Double) = {
    var preyC = preyCoord
    var creatureC = creatureCoord
    val delta = creatureCoord - preyC
    if (Math.abs(delta) > 0.5) {
      if (preyC < creatureC) {
        if (preyC < (1 - creatureC)) preyC += 1.0
        else creatureC -= 1.0
      } else {
        if ((1 - preyC) < creatureC) preyC += 1.0
        else creatureC -= 1.0
      }
    }
    (preyC, creatureC)
  }
}
