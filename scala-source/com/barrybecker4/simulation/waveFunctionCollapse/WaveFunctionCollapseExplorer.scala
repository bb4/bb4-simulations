// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import com.barrybecker4.simulation.waveFunctionCollapse.model.WfcModel
import com.barrybecker4.simulation.waveFunctionCollapse.ui.DynamicOptions
import com.barrybecker4.simulation.waveFunctionCollapse.utils.WfcDebug

import javax.swing.*
import java.awt.*
import java.awt.event.{ComponentAdapter, ComponentEvent}


/**
  * Interactively explores generating Wave Function Collapse procedural modeling.
  * @author Barry Becker.
  */
class WaveFunctionCollapseExplorer() extends Simulator("Wave Function Collapse Explorer") {

  private var wfcModel: Option[WfcModel] = None
  private var options: Option[DynamicOptions] = None
  /** Debounce rapid resize events so we do not allocate many Wave solvers mid-drag. */
  private val resizeDebounce = new Timer(150, _ => applyPanelSizeToModel())
  resizeDebounce.setRepeats(false)

  private val resizeListener = new ComponentAdapter {
    override def componentResized(ce: ComponentEvent): Unit = {
      if (options.isDefined) resizeDebounce.restart()
    }
  }

  commonInit()

  private def commonInit(): Unit = {
    initCommonUI()
    // Register once — reset() must not add duplicate listeners (that was a leak).
    removeComponentListener(resizeListener)
    addComponentListener(resizeListener)
  }

  private def applyPanelSizeToModel(): Unit = {
    options.foreach { opts =>
      val size = getSize
      if (size.width > 0 && size.height > 0) {
        val current = opts.getOutputDimensions
        if (size.width != current.width || size.height != current.height) {
          if (WfcDebug.enabled) println(s"resized ${current.width}x${current.height} -> ${size.width}x${size.height}")
          opts.setDimensions(size)
        }
      }
    }
  }

  def setModel(m: WfcModel): Unit = {
    wfcModel = Some(m)
    this.setPaused(false)
  }

  override protected def reset(): Unit = {
    options.foreach(_.reset())
    commonInit()
  }

  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = 1

  override def timeStep: Double = {
    if (!isPaused) {
      for {
        model <- wfcModel
        opts <- options
      } {
        val result = opts.advanceModel()
        this.repaint()
        if (result.isDefined) this.setPaused(true)
      }
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    wfcModel match {
      case Some(model) if g != null && model.isReady =>
        super.paint(g)
        Profiler.getInstance.startRenderingTime()
        g.drawImage(model.graphics(), 0, 0, null)
        Profiler.getInstance.stopRenderingTime()
      case _ =>
    }
  }

  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    val opts = new DynamicOptions(this)
    options = Some(opts)
    opts
  }

  override def createTopControls: JPanel = {
    new JPanel
  }
}
