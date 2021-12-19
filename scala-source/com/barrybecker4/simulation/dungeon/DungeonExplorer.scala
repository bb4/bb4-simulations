// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.dungeon

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}

import java.awt.{Dimension, Graphics}
import java.awt.event.{ComponentAdapter, ComponentEvent}
import javax.swing.JPanel


/**
  * Interactively generates a dungeon level.
  *
  * @author Barry Becker.
  */
object DungeonExplorer {
  val INITIAL_TIME_STEP: Float = 0.01
}

class DungeonExplorer() extends Simulator("Dungeon Generator") with DungeonOptionsChangedListener {

  private var oldDungeonOptions: DungeonOptions = new DungeonOptions()
  private var dungeonOptions: DungeonOptions = _
  private var dungeonModel: DungeonModel = new DungeonModel(oldDungeonOptions)
  private val generator: DungeonGenerator = new DungeonGenerator()
  private var options: DynamicOptions = _
  private val dungeonRenderer: DungeonRenderer = new DungeonRenderer()


  commonInit()


  private def commonInit(): Unit = {

    initCommonUI()

    val self = this
    this.addComponentListener(new ComponentAdapter {
      override def componentResized(ce: ComponentEvent): Unit = {
        val size: Dimension = self.getSize
        if (size.width != options.getWidth || size.height != options.getHeight) {
          println("resized so rerunning...")
          dungeonOptions = oldDungeonOptions.setDimension(size)
        }
      }
    })
  }

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createResetButton)
    controls.add(createOptionsButton)
    controls
  }

  override protected def reset(): Unit = {
    if (options != null) options.reset()
    commonInit()
  }

  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = DungeonExplorer.INITIAL_TIME_STEP

  def optionsChanged(options: DungeonOptions): Unit = {
    dungeonOptions = oldDungeonOptions.setDimension(this.getSize)

  }

  override def timeStep: Double = {
    if (!isPaused && dungeonOptions != oldDungeonOptions) {
      dungeonModel = generator.generateDungeon(dungeonOptions)
      //dungeonModel.timeStep(tStep)
      oldDungeonOptions = dungeonOptions
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
      super.paint(g)
    Profiler.getInstance.startRenderingTime()

    dungeonRenderer.render(g, dungeonModel)

    Profiler.getInstance.stopRenderingTime()
  }

  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    setPaused(false)
    options
  }
}
