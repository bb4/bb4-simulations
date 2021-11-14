// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.common.app.ClassLoaderSingleton
import com.barrybecker4.ui.util.GUIUtil


object SimulatorApp {
  def main(args: Array[String] = Array[String]()): Unit = {
    // Create a simulator panel of the appropriate type based on the name of the class passed in.
    // If no simulator is specified as an argument, then we use the default.
    var simulatorClassName = SimulatorApplet.DEFAULT_SIMULATOR

    if (args.length == 1) simulatorClassName = args(0)
    else if (args.length > 1) simulatorClassName = args(1)

    val applet = new SimulatorApplet(args.toIndexedSeq, simulatorClassName)
    GUIUtil.showApplet(applet)
  }
}
