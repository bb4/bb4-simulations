// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.common.app.ClassLoaderSingleton
import com.barrybecker4.ui.animation.AnimationPanel
import com.barrybecker4.ui.application.ApplicationApplet
import com.barrybecker4.ui.util.GUIUtil
import javax.swing.JPanel
import java.awt.BorderLayout
import java.util


/**
  * Base class for all simulator applets.
  * Resizable applet for showing simulations.
  * @author Barry Becker
  */
object SimulatorApplet {
  private val RUN_OPTIMIZATION = false
  private val DEFAULT_SIMULATOR = "com.barrybecker4.simulation.fractalexplorer1.FractalExplorer"

  private def createSimulationFromClassName(className: String): Simulator = {
    if (className == null) return null
    val simulatorClass = ClassLoaderSingleton.loadClass(className)
    var simulator: Simulator = null
    try
      simulator = simulatorClass.newInstance.asInstanceOf[Simulator]
    catch {
      case e: InstantiationException =>
        System.err.println("Could not create class for " + className) //NON-NLS

        e.printStackTrace()
      case e: IllegalAccessException =>
        e.printStackTrace()
    }
    simulator
  }

  def main(args: Array[String]): Unit = { // create a simulator panel of the appropriate type based on the name of the class passed in.
    // if no simulator is specified as an argument, then we use the default.
    var simulatorClassName = DEFAULT_SIMULATOR
    if (args.length == 1) simulatorClassName = args(0)
    else if (args.length > 1) simulatorClassName = args(1)
    System.out.println("ARGS = " + args.mkString(", "))
    val applet = new SimulatorApplet(args, simulatorClassName)
    GUIUtil.showApplet(applet)
  }
}

/**
  * Construct the applet
  *
  */
class SimulatorApplet(args: Array[String], sim: Simulator) extends ApplicationApplet(args) {
  private var simulator = sim

  /** @param simulatorClassName name of the simulator class to show. */
  def this(args: Array[String], simulatorClassName: String) {
    this(Array[String](), SimulatorApplet.createSimulationFromClassName(simulatorClassName))
  }

  /** @param sim the simulator to show.*/
  def this(sim: Simulator) {
    this(Array[String](), sim)
  }

  override def getName: String = simulator.getName

  /**
    * create and initialize the simulation
    * The top controls define common buttons like start / reset
    * There is an optional set of dynamic options on the right for modifying the simulation as it runs.
    */
  override def createMainPanel: JPanel = {
    if (simulator == null) {
      var className = getParameter("panel_class") //NON-NLS
      className = if (className == null) SimulatorApplet.DEFAULT_SIMULATOR
      else className
      simulator = SimulatorApplet.createSimulationFromClassName(className)
    }
    val animPanel = new AnimationPanel(simulator)
    animPanel.add(simulator.createTopControls, BorderLayout.NORTH)
    val dynamicControls = simulator.createDynamicControls
    if (dynamicControls != null) animPanel.add(dynamicControls, BorderLayout.EAST)
    simulator.setVisible(true)
    animPanel
  }

  /** The applet's start method. */
  override def start(): Unit = {
    super.start()
    if (SimulatorApplet.RUN_OPTIMIZATION) simulator.doOptimization()
    this.repaint()
  }
}


