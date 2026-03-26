// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import com.barrybecker4.ui.animation.AnimationFrame


/**
  * Entry point for the Verhulst (logistic) rabbit population simulation.
  */
object VerhulstSimulatorApp {

  def main(args: Array[String]): Unit = {
    val sim = new VerhulstSimulator
    sim.setPaused(true)
    new AnimationFrame(sim)
  }

}
