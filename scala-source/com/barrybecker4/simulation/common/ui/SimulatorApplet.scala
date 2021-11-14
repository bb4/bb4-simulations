// Copyright by Barry G. Becker, 2017 - 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.common.app.{AppContext, ClassLoaderSingleton, CommandLineOptions}
import com.barrybecker4.ui.animation.AnimationPanel
import com.barrybecker4.ui.application.ApplicationApplet
import com.barrybecker4.ui.util.{GUIUtil, Log}
import javax.swing.JPanel
import java.awt.BorderLayout
import com.barrybecker4.common.i18n.LocaleType
import SimulatorApplet._


object SimulatorApplet {
  val RUN_OPTIMIZATION = false
  val DEFAULT_SIMULATOR = "com.barrybecker4.simulation.fractalexplorer.FractalExplorer"


  def createSimulationFromClassName(className: String): Simulator = {
    if (className == null) return null
    val simulatorClass = ClassLoaderSingleton.loadClass(className)
    var simulator: Simulator = null
    try
      simulator = simulatorClass.getDeclaredConstructor().newInstance().asInstanceOf[Simulator]
    catch {
      case e: InstantiationException =>
        System.err.println("Could not create class for " + className) //NON-NLS
        e.printStackTrace()
      case e: IllegalAccessException =>
        e.printStackTrace()
    }
    simulator
  }
}

/**
  * Construct the applet
  */
class SimulatorApplet(args: Seq[String], sim: Simulator) extends ApplicationApplet(args.toArray) {
  private var simulator = sim

  if (args.length > 1) {
    val options = new CommandLineOptions(args.toArray)
    if (options.contains("help")) {
      println("Usage: -panel_class <simulation panel class name> [-locale <locale>]\n" +
        s"where <locale> is one of ${LocaleType.VALUES.mkString(", ")}")
    }
    if (options.contains("locale")) { // then a locale has been specified
      val localeName = options.getValueForOption("locale", "ENGLISH")
      AppContext.initialize(localeName,
        List("com.barrybecker4.ui.message", "com.barrybecker4.simulation.common.ui.message"),
        new Log)
    }
  }

  /** @param simulatorClassName name of the simulator class to show. */
  def this(args: Seq[String], simulatorClassName: String) = {
    this(args, SimulatorApplet.createSimulationFromClassName(simulatorClassName))
  }

  /** @param sim the simulator to show.*/
  def this(sim: Simulator) = {
    this(Seq[String](), sim)
  }

  override def getName: String = simulator.getName

  /** Create and initialize the simulation
    * The top controls define common buttons like start / reset
    * There is an optional set of dynamic options on the right for modifying the simulation as it runs.
    */
  override def createMainPanel: JPanel = {
    if (simulator == null) {
      var className = getParameter("panel_class") //NON-NLS
      className = if (className == null) DEFAULT_SIMULATOR else className
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
    if (RUN_OPTIMIZATION) simulator.doOptimization()
    this.repaint()
  }
}
