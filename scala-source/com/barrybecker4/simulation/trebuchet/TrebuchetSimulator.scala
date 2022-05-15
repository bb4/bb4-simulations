// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.optimization.strategy.{GENETIC_SEARCH, OptimizationStrategyType}
import com.barrybecker4.simulation.common.ui.NewtonianSimulator
import com.barrybecker4.simulation.trebuchet.model.{Trebuchet, TrebuchetConstants}
import com.barrybecker4.simulation.trebuchet.rendering.{RenderingConstants, TrebuchetSceneRenderer}
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.*
import scala.util.Random


/**
  * Physically base dynamic simulation of a trebuchet firing.
  */
object TrebuchetSimulator {
  private val DEFAULT_NUM_STEPS_PER_FRAME = 1
  private val OPTIMIZED_NUM_STEPS_PER_FRAME = 10
  // the amount to advance the animation in time for each frame in seconds
  private val TIME_STEP = 0.005
  private val NUM_PARAMS = 3
}

class TrebuchetSimulator() extends NewtonianSimulator("Trebuchet") with ChangeListener {
  reset()
  this.setPreferredSize(new Dimension(800, 900))
  private var trebuchet: Trebuchet = _
  private var zoomSlider: JSlider = _
  private var sceneRenderer: TrebuchetSceneRenderer = _

  def this(trebuchet: Trebuchet) = {
    this()
    commonInit(trebuchet)
  }

  private def commonInit(trebuchet: Trebuchet): Unit = {
    this.trebuchet = trebuchet
    this.sceneRenderer = new TrebuchetSceneRenderer(trebuchet)
    setNumStepsPerFrame(TrebuchetSimulator.DEFAULT_NUM_STEPS_PER_FRAME)
    initCommonUI()
    this.render()
  }

  override protected def reset(): Unit = {
    val trebuchet = new Trebuchet
    commonInit(trebuchet)
  }

  //override def getBackground: Color = TrebuchetSimulator.BACKGROUND_COLOR

  override def createTopControls: JPanel = {
    val controls = super.createTopControls
    val zoomPanel = createZoomPanel()
    controls.add(zoomPanel)
    controls
  }

  private def createZoomPanel(): JPanel = {
    val zoomPanel = new JPanel
    zoomPanel.setLayout(new FlowLayout)
    val zoomLabel = new JLabel(" Zoom")
    zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 1000, 10 * TrebuchetConstants.INITIAL_SCALE.toInt)
    zoomSlider.addChangeListener(this)
    zoomPanel.add(zoomLabel)
    zoomPanel.add(zoomSlider)
    zoomPanel
  }

  override def doOptimization(): Unit = {
    val optimizer = new Optimizer(this)
    setNumStepsPerFrame(TrebuchetSimulator.OPTIMIZED_NUM_STEPS_PER_FRAME)
    val params = new Array[Parameter](TrebuchetSimulator.NUM_PARAMS).toIndexedSeq
    val paramArray = new NumericParameterArray(params, 5, new Random(1))
    setPaused(false)
    optimizer.doOptimization(GENETIC_SEARCH, paramArray, 0.3)
  }

  def getNumParameters: Int = TrebuchetSimulator.NUM_PARAMS
  override protected def getInitialTimeStep: Double = TrebuchetSimulator.TIME_STEP
  override protected def createOptionsDialog = new TrebuchetOptionsDialog(frame, this)

  override def timeStep: Double = {
    if (!isPaused) tStep = trebuchet.stepForward(tStep)
    keepProjectileInView()
    if (trebuchet.hasProjectileLanded) {
      setPaused(true)
    }
    tStep
  }

  // Scale if need to keep the projectile in view
  private def keepProjectileInView(): Unit = {
    val projectileDistanceX = trebuchet.getProjectileDistanceX
    if (zoomSlider != null && projectileDistanceX > 100) {
      val zoom = (15000.0 / (100 + projectileDistanceX)).toInt
      zoomSlider.setValue(Math.min(zoomSlider.getValue, zoom))
    }
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    sceneRenderer.render(g2, getSize(), this.getAntialiasing);
  }

  override def setScale(scale: Double): Unit = {
    trebuchet.setScale(scale)
  }
  override def getScale: Double = trebuchet.getScale

  override def setShowVelocityVectors(show: Boolean): Unit = {
    RenderingConstants.showVelocityVectors = show
  }
  override def getShowVelocityVectors: Boolean = RenderingConstants.showVelocityVectors

  override def setShowForceVectors(show: Boolean): Unit = {
    RenderingConstants.showForceVectors = show
  }
  override def getShowForceVectors: Boolean = RenderingConstants.showForceVectors

  override def setDrawMesh(use: Boolean): Unit = {
    //trebuchet_.setDrawMesh(use);
  }
  override def getDrawMesh: Boolean = {
    false
  }

  override def setStaticFriction(staticFriction: Double): Unit = {}
  override def getStaticFriction = 0.1
  override def setDynamicFriction(dynamicFriction: Double): Unit = {}
  override def getDynamicFriction = 0.01

  /** api for setting trebuchet params  */
  def getTrebuchet: Trebuchet = trebuchet

  override def stateChanged(event: ChangeEvent): Unit = {
    val src = event.getSource
    if (src eq zoomSlider) {
      val v = zoomSlider.getValue.toDouble / 10.0
      trebuchet.setScale(v)
      this.repaint()
    }
  }

  /**
    * Evaluates the trebuchet's fitness.
    * This method is an implement the Optimizee interface.
    * The measure is purely based the distance that the projectile travels.
    * If the trebuchet becomes unstable, then 0.0 is returned.
    */
  override def evaluateFitness(params: ParameterArray): Double = {
    this.reset()
    this.setPaused(false)

    while (!trebuchet.hasProjectileLanded) {
      ThreadUtil.sleep(1000)
    }
    trebuchet.getProjectileDistanceX
  }
}
