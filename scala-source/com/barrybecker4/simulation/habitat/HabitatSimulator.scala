// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorApplet
import com.barrybecker4.simulation.habitat.creatures.Populations
import com.barrybecker4.simulation.habitat.creatures.SerengetiPopulations
import com.barrybecker4.simulation.habitat.ui.options.DynamicOptions
import com.barrybecker4.simulation.habitat.ui.options.HabitatOptionsDialog
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt._

import com.barrybecker4.simulation.habitat.ui.{HabitatPanel, PopulationGraphPanel}



/**
  * Simulates foxes (predators) and rabbits (prey) in the wild.
  *
  * @author Barry Becker
  */
object HabitatSimulator {
  def main(args: Array[String]): Unit = {
    val sim = new HabitatSimulator
    sim.setPaused(true)
    GUIUtil.showApplet(new SimulatorApplet(sim))
  }
}

class HabitatSimulator() extends Simulator("Habitat Simulation") {
  setBackground(Color.WHITE)
  private var populations = new SerengetiPopulations
  private var options: DynamicOptions = _
  val graphPanel = new PopulationGraphPanel(populations)
  val habitatPanel = new HabitatPanel(populations)
  val splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, habitatPanel, graphPanel)
  //Provide minimum sizes for the two components in the split pane
  override val minimumSize = new Dimension(100, 50)
  habitatPanel.setMinimumSize(minimumSize)
  graphPanel.setMinimumSize(minimumSize)
  splitPane.setDividerLocation(350)
  this.add(splitPane)

  override protected def reset(): Unit = {
    this.setPaused(true)
    // wait till actually paused. Not clean, but oh well.
    ThreadUtil.sleep(500)
    MathUtil.RANDOM.setSeed(1)
    populations.reset()
    options.reset()
    this.setPaused(false)
  }

  override protected def getInitialTimeStep = 1.0

  override def timeStep: Double = {
    options.update()
    populations.nextDay()
    timeStep_
  }

  def getPopulations: Populations = populations

  /** Draw the population graph under the habitat. */
  override def paint(g: Graphics): Unit = {
    splitPane.setSize(getSize)
    splitPane.paint(g)
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  override protected def createOptionsDialog = new HabitatOptionsDialog(frame, this)
}