// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import java.awt.*
import com.barrybecker4.simulation.habitat.creatures.Creature
import com.barrybecker4.simulation.habitat.creatures.populations.Populations


/**
  * This class draws a the global habitat and all the creatures in it.
  * @author Barry Becker
  */
class HabitatRenderer private[habitat](var populations: Populations) {
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

    for (pop <- populations) {
      for (creature <- pop.creatures) {
        creatureRenderer.drawCreature(creature, g2)
      }
    }
  }
}
