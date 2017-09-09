// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.henonphase.algorithm.HenonAlgorithm
import javax.swing._
import java.awt._


/**
  * Interactively explores the Henon Phase attractors.
  * See   http://mathworld.wolfram.com/HenonMap.html
  * See   http://www.complexification.net/gallery/machines/henonPhaseDeep/
  * @author Barry Becker.
  */
object HenonPhaseExplorer {
  protected val INITIAL_TIME_STEP = 10.0
  protected val DEFAULT_STEPS_PER_FRAME = 10
}

class HenonPhaseExplorer() extends Simulator("Henon Phase Explorer") {
  commonInit()
  private var algorithm: HenonAlgorithm = _
  private var options: DynamicOptions = _
  private var useFixedSize = false

  /** @param fixed if true then the render area does not resize automatically.*/
  def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  private def commonInit() {
    algorithm = new HenonAlgorithm
    initCommonUI()
    reset()
  }

  override protected def reset() {
    algorithm.reset()
    setNumStepsPerFrame(HenonPhaseExplorer.DEFAULT_STEPS_PER_FRAME)
    if (options != null) options.reset()
  }

  def getUseFixedSize: Boolean = useFixedSize
  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = HenonPhaseExplorer.INITIAL_TIME_STEP
  override def setScale(scale: Double) {}
  override def getScale = 0.01
  def getColorMap: ColorMap = algorithm.getColorMap

  override def timeStep: Double = {
    if (!isPaused) {
      if (!useFixedSize) algorithm.setSize(this.getWidth, this.getHeight)
      algorithm.timeStep(timeStep_)
    }
    timeStep_
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(algorithm.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(algorithm, this)
    options
  }
}
