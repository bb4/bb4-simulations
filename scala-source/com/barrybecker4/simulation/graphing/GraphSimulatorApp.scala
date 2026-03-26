// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.ui.animation.AnimationFrame

import java.awt.Dimension


/**
  * Simulates graphing a function
  */
object GraphSimulatorApp {

  private val FrameSize = new Dimension(1200, 800)

  def main(args: Array[String]): Unit = {
    val sim = new GraphSimulator()
    sim.setPaused(true)
    val frame = new AnimationFrame(sim)
    frame.setSize(FrameSize)
  }

}
