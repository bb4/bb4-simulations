// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.ui.util.GUIUtil


object SimulatorApp {

  /**
    * Resolves the simulator class name from CLI args (matches [[SimulatorApplet]] conventions).
    * If no simulator is specified, returns [[SimulatorApplet.DEFAULT_SIMULATOR]].
    */
  def simulatorClassNameFromArgs(args: Array[String]): String =
    if (args.length == 1) args(0)
    else if (args.length > 1) args(1)
    else SimulatorApplet.DEFAULT_SIMULATOR

  def main(args: Array[String] = Array[String]()): Unit = {
    val simulatorClassName = simulatorClassNameFromArgs(args)
    val applet = new SimulatorApplet(args.toIndexedSeq, simulatorClassName)
    GUIUtil.showApplet(applet)
  }
}
