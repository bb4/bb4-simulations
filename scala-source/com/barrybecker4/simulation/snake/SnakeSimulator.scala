// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.types.{DoubleParameter, Parameter}
import com.barrybecker4.optimization.strategy.GENETIC_SEARCH
import com.barrybecker4.simulation.common.rendering.BackgroundGridRenderer
import com.barrybecker4.simulation.common.ui.NewtonianSimulator
import com.barrybecker4.simulation.snake.rendering.RenderingParameters
import com.barrybecker4.simulation.snake.rendering.SnakeRenderer
import javax.vecmath.Point2d
import javax.vecmath.Vector2d
import java.awt._
import com.barrybecker4.simulation.snake.data.{SnakeData, SnakeType}
import scala.compiletime.uninitialized
import scala.util.Random


/**
  * Simulates the motion of a snake.
  */
object SnakeSimulator {
  /** the amount to advance the animation in time for each frame in seconds. */
  protected val NUM_STEPS_PER_FRAME = 200

  private val PARAMS = IndexedSeq[Parameter](
    new DoubleParameter(LocomotionParameters.WAVE_SPEED, 0.0001, 0.02, "wave speed"),
    new DoubleParameter(LocomotionParameters.WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude"),
    new DoubleParameter(LocomotionParameters.WAVE_PERIOD, 0.5, 9.0, "wave period")
  )

  private val INITIAL_PARAMS: NumericParameterArray =
    new NumericParameterArray(PARAMS, 5, new Random(1))

  /** initial time step */
  val INITIAL_TIME_STEP = 0.2
  // size of the background grid
  // note the ground should move, not the snake so that the snake always remains visible.
  private val XDIM = 12
  private val YDIM = 10
  private val CELL_SIZE = 80.0
  private val GRID_COLOR = new Color(0, 0, 60, 100)

  /** Physics steps per round when estimating locomotion fitness (no rendering). */
  private val FitnessStepsPerRound = 400
  /** Outer rounds: stop when speed plateaus or snake becomes unstable. */
  private val FitnessMaxRounds = 25
  private val FitnessSpeedImprovementEps = 1e-5
  /** Penalize configurations where the snake becomes unstable. */
  private val UnstableFitnessPenalty = 100000.0
  private val MinSpeedForFitness = 1e-9

  private def fitnessFromSpeed(speed: Double): Double =
    1.0 / math.max(speed, MinSpeedForFitness)
}

class SnakeSimulator(val snakeData: SnakeData) extends NewtonianSimulator("Snake") {

  private val updater: SnakeUpdater = new SnakeUpdater()
  /** change in center of the snake between time steps */
  private var oldCenter: Point2d = uninitialized
  /** the overall distance the the snake has travelled so far. */
  private val distance = new Vector2d(0.0, 0.0)
  /** magnitude of the snakes velocity vector */
  private var velocity: Double = 0
  private val gridColor = SnakeSimulator.GRID_COLOR
  private val renderParams = new RenderingParameters
  private var renderer: SnakeRenderer = uninitialized
  private var snake: Snake = Snake(snakeData, updater.getLocomotionParams)
  commonInit()
  reinitializeViewAndClock()

  def this() = {
    this(SnakeType.LONG_SNAKE.snakeData)
  }

  override protected def reset(): Unit = {
    snake.reset()
    reinitializeViewAndClock()
  }
  override protected def getInitialTimeStep: Double = SnakeSimulator.INITIAL_TIME_STEP

  def setSnakeData(snakeData: SnakeData): Unit = {
    snake = Snake(snakeData, updater.getLocomotionParams)
    commonInit()
    reinitializeViewAndClock()
  }

  /** Align camera/scroll state and physics clock with the current [[snake]] (after reset or new geometry). */
  private def reinitializeViewAndClock(): Unit = {
    updater.resetSimulationTime()
    oldCenter = new Point2d(snake.getCenter)
    distance.set(0.0, 0.0)
    velocity = 0.0
    tStep = getInitialTimeStep
  }

  private def commonInit(): Unit = {
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
    val optimizer = new Optimizer(this)
    setPaused(false)
    optimizer.doOptimization(GENETIC_SEARCH, SnakeSimulator.INITIAL_PARAMS, 0.3)
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
    * Implements the Optimizee interface: fitness from average center speed over physics steps only
    * (does not depend on [[paint]] or display refresh).
    */
  override def evaluateFitness(params: ParameterArray): Double = {
    val locoParams = updater.getLocomotionParams
    locoParams.waveSpeed = params.get(0).getValue
    locoParams.waveAmplitude = params.get(1).getValue
    locoParams.wavePeriod = params.get(2).getValue
    snake.reset()
    updater.resetSimulationTime()
    val (stable, bestSpeed) = runFitnessConvergence()
    if (!stable) SnakeSimulator.UnstableFitnessPenalty
    else SnakeSimulator.fitnessFromSpeed(bestSpeed)
  }

  /** Advance the simulation in rounds until speed plateaus or the snake becomes unstable. */
  private def runFitnessConvergence(): (Boolean, Double) = {
    var oldSpeed = 0.0
    var improved = true
    var round = 0
    var stable = true
    while (round < SnakeSimulator.FitnessMaxRounds && stable && improved) {
      val speed = sampleAverageCenterSpeed(SnakeSimulator.FitnessStepsPerRound, SnakeSimulator.INITIAL_TIME_STEP)
      improved = (speed - oldSpeed) > SnakeSimulator.FitnessSpeedImprovementEps
      oldSpeed = speed
      stable = snake.isStable
      round += 1
    }
    (stable, oldSpeed)
  }

  /**
    * Average speed of the snake center of mass over the given number of [[SnakeUpdater.stepForward]] calls.
    */
  private def sampleAverageCenterSpeed(steps: Int, initialDt: Double): Double = {
    val c0 = new Point2d(snake.getCenter)
    var dt = initialDt
    var elapsed = 0.0
    var i = 0
    while (i < steps) {
      val used = dt
      dt = updater.stepForward(snake, dt)
      elapsed += used
      i += 1
    }
    val c1 = snake.getCenter
    if (elapsed <= 0.0) 0.0
    else Math.hypot(c1.x - c0.x, c1.y - c0.y) / elapsed
  }
}
