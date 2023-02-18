// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorApplet
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.habitat.creatures.populations.{Populations, PredPreyPopulations}
import com.barrybecker4.simulation.habitat.ui.options.DynamicOptions
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.*
import java.awt.*
import com.barrybecker4.simulation.habitat.ui.{HabitatPanel, HabitatSplitPanel, PopulationGraphPanel}



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
  private val populations = new PredPreyPopulations
  private var options: DynamicOptions = _
  private val splitPanel = new HabitatSplitPanel(populations)
  this.add(splitPanel)

  override protected def reset(): Unit = {
    this.setPaused(true)
    // wait till actually paused. Not clean, but oh well.
    ThreadUtil.sleep(500)
    MathUtil.RANDOM.setSeed(1)
    populations.reset()
    options.reset()
    this.setPaused(false)
  }

  override protected def getInitialTimeStep = 0.1

  override def timeStep: Double = {
    options.update()
    populations.nextDay()
    tStep
  }

  def getPopulations: Populations = populations

  def setNumPixelsPerXPoint(numPixels: Int): Unit = {
    splitPanel.setNumPixelsPerXPoint(numPixels)
  }
  
  /** Draw the population graph under the habitat. */
  override def paint(g: Graphics): Unit = {
    splitPanel.setSize(getSize)
    splitPanel.paint(g)
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)
}