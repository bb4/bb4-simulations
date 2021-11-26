// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.ui.animation.AnimationFrame


/**
  * Simulates graphing a function
  */
object GraphSimulatorApp {

  def main(args: Array[String]): Unit = {
    val sim = new GraphSimulator()
    sim.setPaused(true)
    new AnimationFrame(sim)
  }

}
