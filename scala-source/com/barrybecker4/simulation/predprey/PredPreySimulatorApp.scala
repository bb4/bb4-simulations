// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey

import com.barrybecker4.ui.util.GUIUtil
import com.barrybecker4.simulation.common.ui.SimulatorApplet

/**
  * Simulates foxes (predators) and rabbits (prey) in the wild.
  *
  * @author Barry Becker
  */
object PredPreySimulatorApp {
  def main(args: Array[String]): Unit = {
    val sim = new PredPreySimulator
    sim.setPaused(true)
    GUIUtil.showApplet(new SimulatorApplet(sim))
  }
}