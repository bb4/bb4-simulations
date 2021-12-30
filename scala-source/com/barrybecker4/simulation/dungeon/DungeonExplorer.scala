// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}
import com.barrybecker4.simulation.dungeon.rendering.DungeonRenderer
import com.barrybecker4.simulation.dungeon.ui.{DungeonOptionsChangedListener, DynamicOptions}
import com.barrybecker4.simulation.dungeon.DungeonExplorer
import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.uniongraph.UnionGraphDungeonGenerator

import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.{Dimension, Graphics}
import javax.swing.JPanel



object DungeonExplorer {
  val INITIAL_TIME_STEP: Float = 0.01
}

/**
 * Interactively generates a dungeon level.
 * @author Barry Becker.
 */
class DungeonExplorer() extends Simulator("Dungeon Generator") with DungeonOptionsChangedListener {

  private var dungeonOptions: DungeonOptions = DungeonOptions()
  private var oldDungeonOptions: DungeonOptions = dungeonOptions
  private val generator: DungeonGeneratorStrategy = UnionGraphDungeonGenerator()
  private var options: DynamicOptions = _
  private val dungeonRenderer: DungeonRenderer = DungeonRenderer()
  commonInit()

  private def commonInit(): Unit = {

    initCommonUI()

    val self = this
    this.addComponentListener(new ComponentAdapter {
      override def componentResized(ce: ComponentEvent): Unit =
        dungeonOptions = oldDungeonOptions.setDimension(getDimensionsInCells)
    })
  }

  override def createTopControls: JPanel = new JPanel

  override protected def reset(): Unit = {
    if (options != null) options.reset()
    commonInit()
  }

  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = DungeonExplorer.INITIAL_TIME_STEP

  def optionsChanged(options: DungeonOptions): Unit = {
    dungeonOptions = options.setDimension(this.getDimensionsInCells)
  }

  private def getDimensionsInCells = {
    new Dimension(getWidth / dungeonOptions.cellSize, getHeight /dungeonOptions.cellSize)
  }

  override def timeStep: Double = {
    if (!isPaused && dungeonOptions != oldDungeonOptions) {
      val dungeonModel = generator.generateDungeon(dungeonOptions)
      dungeonRenderer.render(dungeonOptions, dungeonModel)
      //dungeonModel.timeStep(tStep)
      oldDungeonOptions = dungeonOptions
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    //super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(dungeonRenderer.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    setPaused(false)
    options
  }
}
