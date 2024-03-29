// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.renderers

import com.barrybecker4.simulation.habitat.creatures.Creature
import com.barrybecker4.simulation.habitat.creatures.populations.Habitat
import com.barrybecker4.simulation.habitat.ui.renderers.CreatureRenderer

import java.awt.*


/**
  * This class draws a the global habitat and all the creatures in it.
  * @author Barry Becker
  */
class HabitatRenderer private[habitat](var populations: Habitat) {
  private var width = 0
  private var height = 0
  private var creatureRenderer: CreatureRenderer = _

  def setSize(width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
    this.creatureRenderer = CreatureRenderer(width, height)
  }

  /** draw the cartesian functions */
  def paint(g: Graphics): Unit = {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    
    g2.setColor(Color.WHITE)
    g2.fillRect(0, 0, width, height)

    for (pop <- populations) {
      for (creature <- pop.creatures) {
        creatureRenderer.drawCreature(creature, g2)
      }
    }
  }
}
