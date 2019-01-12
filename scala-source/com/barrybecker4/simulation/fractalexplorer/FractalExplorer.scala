/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer

import com.barrybecker4.common.math.ComplexNumber
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.rendering.ModelImage
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.fractalexplorer.algorithm._
import com.barrybecker4.ui.util.ColorMap
import javax.swing.JPanel
import java.awt.Graphics


/**
  * Interactively explores the Mandelbrot set.
  *
  * @author Barry Becker.
  */
object FractalExplorer {
  val DEFAULT_ALGORITHM_ENUM: AlgorithmEnum = MANDELBROT
}

class FractalExplorer extends Simulator("Fractal Explorer") {

  private var algorithm: FractalAlgorithm = _
  private var algorithmEnum: AlgorithmEnum = _
  private var juliaSeed = JuliaAlgorithm.DEFAULT_JULIA_SEED
  private var model: FractalModel = _
  private var options: DynamicOptions = _
  private var zoomHandler: ZoomHandler = _
  private var useFixedSize: Boolean = false

  commonInit()

  /** @param fixed if true then the render area does not resize automatically.*/
  def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  def getUseFixedSize: Boolean = useFixedSize

  private def commonInit(): Unit = {
    initCommonUI()
    algorithmEnum = FractalExplorer.DEFAULT_ALGORITHM_ENUM
    reset()
  }

  def setAlgorithm(alg: AlgorithmEnum): Unit = {
    algorithmEnum = alg
    reset()
  }

  def setJuliaSeed(seed: ComplexNumber): Unit = {
    juliaSeed = seed
  }

  /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
  def getAlgorithm: FractalAlgorithm = algorithm

  override protected def reset(): Unit = {
    model = new FractalModel()
    algorithm = algorithmEnum.createInstance(model)
    // this is a hack. The Options dialog should only know about this seed
    algorithm match {
      case algorithm1: JuliaAlgorithm => algorithm1.setJuliaSeed(juliaSeed)
      case _ =>
    }

    setNumStepsPerFrame(DynamicOptions.DEFAULT_STEPS_PER_FRAME)
    if (zoomHandler != null) {
      this.removeMouseListener(zoomHandler)
      this.removeMouseMotionListener(zoomHandler)
    }
    zoomHandler = new ZoomHandler(algorithm)
    this.addMouseListener(zoomHandler)
    this.addMouseMotionListener(zoomHandler)
    if (options != null) options.reset()
  }

  override protected def createOptionsDialog = new FractalOptionsDialog(frame, this)

  override protected def getInitialTimeStep: Double = DynamicOptions.INITIAL_TIME_STEP

  override def timeStep: Double = {
    if (!isPaused) {
      if (!useFixedSize)
        model.setSize(getWidth, getHeight)
      algorithm.timeStep(tStep)
      model.updateImage(model.getLastRow, model.getCurrentRow)
      options.setCoordinates(algorithm.getRange)
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    if (g != null)
      g.drawImage(model.getImage, 0, 0, null)
    zoomHandler.render(g, model.getAspectRatio)
    options.setCoordinates(algorithm.getRange)
    Profiler.getInstance.stopRenderingTime()
  }

  override def setScale(scale: Double): Unit = {
  }

  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  def getColorMap: ColorMap = model.getColorMap
}
