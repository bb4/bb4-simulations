// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorFrame
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.habitat.creatures.populations.Habitat
import com.barrybecker4.simulation.habitat.ui.options.DynamicOptions
import com.barrybecker4.simulation.habitat.creatures.CreatureProcessor
import com.barrybecker4.simulation.habitat.ui.renderers.CreatureRenderer

import javax.swing.*
import java.awt.*
import com.barrybecker4.simulation.habitat.ui.HabitatSplitPanel


/**
  * Simulates foxes (predators) and rabbits (prey) in the wild.
  *
  * @author Barry Becker
  */
object HabitatSimulator {
  def main(args: Array[String]): Unit = {
    val sim = new HabitatSimulator
    sim.setPaused(true)
    val frame = new SimulatorFrame(args.toIndexedSeq, sim)

    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
  }
}

class HabitatSimulator extends Simulator("Habitat Simulation") {

  setBackground(Color.WHITE)
  private var iterationsPerFrame = DynamicOptions.INITIAL_ITERATIONS_PER_FRAME
  private var useContinuousIteration = DynamicOptions.DEFAULT_USE_CONTINUOUS_ITERATION
  private var habitat = Habitat.HABITATS(Habitat.DEFAULT_HABITAT_INDEX)
  private var options: DynamicOptions = _
  private var splitPanel = new HabitatSplitPanel(habitat)
  this.add(splitPanel)

  override protected def reset(): Unit = {
    this.setPaused(true)
    // wait till actually paused.
    ThreadUtil.sleep(500)
    MathUtil.RANDOM.setSeed(1)
    habitat.reset()
    options.reset()
    this.setPaused(false)
  }

  override def getPreferredSize: Dimension = new Dimension(1200, 1000)

  override protected def getInitialTimeStep = 0.1

  override def timeStep: Double = {
    if (useContinuousIteration) {
      for (i <- 0 until iterationsPerFrame) {
        options.update()
        habitat.nextDay()
      }
    }
    tStep
  }

  def getHabitat: Habitat = habitat

  def setHabitat(habitat: Habitat): Unit = {
    this.habitat = habitat
    this.remove(splitPanel)
    splitPanel = new HabitatSplitPanel(habitat)
    this.add(splitPanel)
    this.reset()
    ThreadUtil.sleep(100)
    this.setPaused(true)
  }

  def setDebug(isDebug: Boolean): Unit = {
    CreatureProcessor.DEBUG = isDebug
    CreatureRenderer.DEBUG = isDebug
  }

  def setNumPixelsPerXPoint(numPixels: Int): Unit = {
    splitPanel.setNumPixelsPerXPoint(numPixels)
  }

  def setIterationsPerFrame(iterationsPerFrame: Int): Unit = {
    this.iterationsPerFrame = iterationsPerFrame
  }

  def setUseContinuousIteration(useContinuous: Boolean): Unit = {
    this.useContinuousIteration = useContinuous
  }

  def requestNextStep(): Unit = {
      habitat.nextDay()
      splitPanel.repaint()
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