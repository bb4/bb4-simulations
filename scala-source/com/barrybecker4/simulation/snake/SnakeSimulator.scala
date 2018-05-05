// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.types.{DoubleParameter, Parameter}
import com.barrybecker4.optimization.strategy.OptimizationStrategyType
import com.barrybecker4.simulation.common.rendering.BackgroundGridRenderer
import com.barrybecker4.simulation.common.ui.NewtonianSimulator
import com.barrybecker4.simulation.snake.rendering.RenderingParameters
import com.barrybecker4.simulation.snake.rendering.SnakeRenderer
import com.barrybecker4.ui.util.GUIUtil
import javax.vecmath.Point2d
import javax.vecmath.Vector2d
import java.awt._

import scala.collection.JavaConverters._
import com.barrybecker4.simulation.snake.data.{SnakeData, SnakeType}


/**
  * Simulates the motion of a snake.
  */
object SnakeSimulator {
  /** the amount to advance the animation in time for each frame in seconds. */
  protected val NUM_STEPS_PER_FRAME = 200

  private val PARAMS: java.util.List[Parameter] = Seq[Parameter](
    new DoubleParameter(LocomotionParameters.WAVE_SPEED, 0.0001, 0.02, "wave speed"),
    new DoubleParameter(LocomotionParameters.WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude"),
    new DoubleParameter(LocomotionParameters.WAVE_PERIOD, 0.5, 9.0, "wave period")
  ).asJava

  private val INITIAL_PARAMS: NumericParameterArray = new NumericParameterArray(PARAMS)
  /** initial time step */
  val INITIAL_TIME_STEP = 0.2
  // size of the background grid
  // note the ground should move, not the snake so that the snake always remains visible.
  private val XDIM = 12
  private val YDIM = 10
  private val CELL_SIZE = 80.0
  private val GRID_COLOR = new Color(0, 0, 60, 100)
}

class SnakeSimulator(snakeData: SnakeData) extends NewtonianSimulator("Snake") {

  private val snake: Snake = new Snake(snakeData)
  private val updater: SnakeUpdater = new SnakeUpdater()
  /** change in center of the snake between time steps */
  private var oldCenter: Point2d = _
  /** the overall distance the the snake has travelled so far. */
  private val distance = new Vector2d(0.0, 0.0)
  /** magnitude of the snakes velocity vector */
  private var velocity: Double = 0
  private val gridColor = SnakeSimulator.GRID_COLOR
  private val renderParams = new RenderingParameters
  private var renderer: SnakeRenderer = _
  commonInit()

  def this() { this(SnakeType.LONG_SNAKE.snakeData) }

  override protected def reset(): Unit = { snake.reset()}
  override protected def getInitialTimeStep: Double = SnakeSimulator.INITIAL_TIME_STEP

  def setSnakeData(snakeData: SnakeData): Unit = {
    snake.setData(snakeData)
    commonInit()
  }

  private def commonInit(): Unit = {
    oldCenter = snake.getCenter
    renderer = new SnakeRenderer(renderParams)
    setNumStepsPerFrame(SnakeSimulator.NUM_STEPS_PER_FRAME)
    this.setPreferredSize(
      new Dimension(
        (SnakeSimulator.CELL_SIZE * SnakeSimulator.XDIM).toInt,
        (SnakeSimulator.CELL_SIZE * SnakeSimulator.YDIM).toInt
      )
    )
    initCommonUI()
  }

  def getLocomotionParams: LocomotionParameters = updater.getLocomotionParams
  override def setScale(scale: Double): Unit = {
    renderParams.scale = scale
  }

  override def getScale: Double = renderParams.scale
  override def setShowVelocityVectors(show: Boolean): Unit = {
    renderParams.showVelocityVectors = show
  }

  override def getShowVelocityVectors: Boolean = renderParams.showVelocityVectors
  override def setShowForceVectors(show: Boolean): Unit = {
    renderParams.showForceVectors = show
  }

  override def getShowForceVectors: Boolean = renderParams.showForceVectors
  override def setDrawMesh(use: Boolean): Unit = {
    renderParams.drawMesh = use
  }

  override def getDrawMesh: Boolean = renderParams.drawMesh
  override def setStaticFriction(staticFriction: Double): Unit = {
    updater.getLocomotionParams.staticFriction = staticFriction
  }

  override def getStaticFriction: Double = updater.getLocomotionParams.staticFriction
  override def setDynamicFriction(dynamicFriction: Double): Unit = {
    updater.getLocomotionParams.dynamicFriction = dynamicFriction
  }

  override def getDynamicFriction: Double = updater.getLocomotionParams.dynamicFriction
  def setDirection(direction: Double): Unit = {
    updater.getLocomotionParams.direction = direction
  }

  override def createDynamicControls = new SnakeDynamicOptions(this)
  override def doOptimization(): Unit = {
    var optimizer: Optimizer = null
    if (GUIUtil.hasBasicService) { // need to verify
      optimizer = new Optimizer(this)
    }
    else optimizer = new Optimizer(this, FileUtil.getHomeDir + "performance/snake/snake_optimization.txt")
    setPaused(false)
    optimizer.doOptimization(OptimizationStrategyType.GENETIC_SEARCH, SnakeSimulator.INITIAL_PARAMS, 0.3)
  }

  override def timeStep: Double = {
    if (!isPaused) tStep = updater.stepForward(snake, tStep)
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, if (getAntialiasing) RenderingHints.VALUE_ANTIALIAS_ON
    else RenderingHints.VALUE_ANTIALIAS_OFF)
    val newCenter = snake.getCenter
    val distanceDelta = new Vector2d(oldCenter.x - newCenter.x, oldCenter.y - newCenter.y)
    velocity = distanceDelta.length / (getNumStepsPerFrame * tStep)
    distance.add(distanceDelta)
    val bgRenderer = new BackgroundGridRenderer(gridColor)
    bgRenderer.drawGridBackground(g2, SnakeSimulator.CELL_SIZE, SnakeSimulator.XDIM, SnakeSimulator.YDIM, distance)
    // draw the snake on the grid
    snake.translate(distanceDelta)
    renderer.render(snake, g2)
    oldCenter = snake.getCenter
  }

  override protected def createOptionsDialog = new SnakeOptionsDialog(frame, this)
  override protected def getStatusMessage: String = super.getStatusMessage + "    velocity=" + FormatUtil.formatNumber(velocity)

  /**
    * *** implements the key method of the Optimizee interface
    * evaluates the snake's fitness.
    * The measure is purely based on its velocity.
    * If the snake becomes unstable, then 0.0 is returned.
    */
  override def evaluateFitness(params: ParameterArray): Double = {
    val locoParams = updater.getLocomotionParams
    locoParams.waveSpeed = params.get(0).getValue
    locoParams.waveAmplitude = params.get(1).getValue
    locoParams.wavePeriod = params.get(2).getValue
    var stable = true
    var improved = true
    var oldVelocity = 0.0
    var ct = 0
    while ( {
      stable && improved
    }) { // let the snake run for a while
      ThreadUtil.sleep(1000 + (3000 / (1.0 + 0.2 * ct)).toInt)
      improved = (velocity - oldVelocity) > 0.00001
      oldVelocity = velocity
      ct += 1
      stable = snake.isStable
    }
    if (!stable) {
      System.out.println("SnakeSim unstable")
      100000.0
    }
    else 1.0 / oldVelocity
  }
}