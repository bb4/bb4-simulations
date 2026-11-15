// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.types.{DoubleParameter, Parameter}
import com.barrybecker4.optimization.strategy.OptimizationStrategyType
import com.barrybecker4.optimization.strategy.SIMULATED_ANNEALING
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
  /**
    * Physics steps per rendered frame. Kept moderate so default locomotion is watchable;
    * fitness evaluation uses its own step counts and does not depend on this.
    */
  protected val NUM_STEPS_PER_FRAME = 40

  private val PARAMS = IndexedSeq[Parameter](
    new DoubleParameter(LocomotionParameters.WAVE_SPEED, 0.0001, 0.02, "wave speed"),
    new DoubleParameter(LocomotionParameters.WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude"),
    new DoubleParameter(LocomotionParameters.WAVE_PERIOD, 0.5, 9.0, "wave period")
  )

  private val INITIAL_PARAMS: NumericParameterArray =
    initialParameters(new Random(1))

  /** The three locomotion parameters the optimizer searches, with `rnd` as the array's own generator. */
  def initialParameters(rnd: Random): NumericParameterArray =
    new NumericParameterArray(PARAMS, 5, rnd)

  /** initial time step */
  val INITIAL_TIME_STEP = 0.2
  // size of the background grid
  // note the ground should move, not the snake so that the snake always remains visible.
  private val XDIM = 12
  private val YDIM = 10
  private val CELL_SIZE = 80.0
  private val GRID_COLOR = new Color(0, 0, 60, 100)

  /** Preferred panel size in pixels (also used as fallback before the component is realized). */
  private[snake] val PreferredWidth: Int = (CELL_SIZE * XDIM).toInt
  private[snake] val PreferredHeight: Int = (CELL_SIZE * YDIM).toInt

  /**
    * Combined user zoom and window-relative scale. Growing the window enlarges grid and snake together.
    */
  private[snake] def effectiveScale(userScale: Double, panelWidth: Int, panelHeight: Int): Double = {
    val s = if (userScale <= 0) RenderingParameters.SCALE else userScale
    val w = math.max(panelWidth, 1).toDouble
    val h = math.max(panelHeight, 1).toDouble
    val windowScale = math.min(w / PreferredWidth, h / PreferredHeight)
    s * windowScale
  }

  /**
    * Graphics translation so a world point `(centerX, centerY)` drawn with `screen = scale * world`
    * lands at the panel center. Does not mutate snake physics state.
    */
  private[snake] def cameraTranslation(
    panelWidth: Int,
    panelHeight: Int,
    scale: Double,
    centerX: Double,
    centerY: Double
  ): (Double, Double) =
    (panelWidth / 2.0 - scale * centerX, panelHeight / 2.0 - scale * centerY)

  /** Number of grid cells needed to cover the panel for the given pixel cell size. */
  private[snake] def gridCellCounts(panelWidth: Int, panelHeight: Int, cellSizePx: Double): (Int, Int) = {
    val cs = math.max(cellSizePx, 1.0)
    val xDim = math.max(1, math.ceil(panelWidth / cs).toInt + 1)
    val yDim = math.max(1, math.ceil(panelHeight / cs).toInt + 1)
    (xDim, yDim)
  }

  /** Physics steps per round when estimating locomotion fitness (no rendering). */
  private val FitnessStepsPerRound = 400
  /** Outer rounds: stop when speed plateaus or snake becomes unstable. */
  private val FitnessMaxRounds = 25
  private val FitnessSpeedImprovementEps = 1e-5
  /** Penalize configurations where the snake becomes unstable. */
  private val UnstableFitnessPenalty = 100000.0

  /**
    * Scale of a good locomotion fitness. `1 / speed` is a few units for a fast snake and
    * [[UnstableFitnessPenalty]] for a failure. Annealing's starting temperature is this divided by 10,
    * so a range of 0.3 freezes the search against ordinary speed differences.
    */
  val FitnessRange = 10.0

  /** Best median fitness on the 2.1-SNAPSHOT benchmark column. */
  val SearchStrategy: OptimizationStrategyType = SIMULATED_ANNEALING
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

  override protected def reset(): Unit = synchronized {
    snake.reset()
    reinitializeViewAndClock()
  }
  override protected def getInitialTimeStep: Double = SnakeSimulator.INITIAL_TIME_STEP

  def setSnakeData(snakeData: SnakeData): Unit = synchronized {
    snake = Snake(snakeData, updater.getLocomotionParams)
    commonInit()
    reinitializeViewAndClock()
  }

  /** Align scroll/speed tracking and physics clock with the current [[snake]] (after reset or new geometry). */
  private def reinitializeViewAndClock(): Unit = {
    updater.resetSimulationTime()
    oldCenter = new Point2d(snake.getCenter)
    distance.set(0.0, 0.0)
    velocity = 0.0
    tStep = getInitialTimeStep
  }

  /** Panel size for rendering; prefer actual size once realized. */
  private def viewPanelSize: (Int, Int) = {
    val w = if (getWidth > 0) getWidth else SnakeSimulator.PreferredWidth
    val h = if (getHeight > 0) getHeight else SnakeSimulator.PreferredHeight
    (w, h)
  }

  private def commonInit(): Unit = {
    renderer = new SnakeRenderer(renderParams)
    setNumStepsPerFrame(SnakeSimulator.NUM_STEPS_PER_FRAME)
    this.setPreferredSize(new Dimension(SnakeSimulator.PreferredWidth, SnakeSimulator.PreferredHeight))
    initCommonUI()
  }

  def getLocomotionParams: LocomotionParameters = updater.getLocomotionParams
  override def setScale(scale: Double): Unit = synchronized {
    renderParams.scale = scale
  }

  override def getScale: Double = synchronized { renderParams.scale }
  override def setShowVelocityVectors(show: Boolean): Unit = synchronized {
    renderParams.showVelocityVectors = show
  }

  override def getShowVelocityVectors: Boolean = synchronized { renderParams.showVelocityVectors }
  override def setShowForceVectors(show: Boolean): Unit = synchronized {
    renderParams.showForceVectors = show
  }

  override def getShowForceVectors: Boolean = synchronized { renderParams.showForceVectors }
  override def setDrawMesh(use: Boolean): Unit = synchronized {
    renderParams.drawMesh = use
  }

  override def getDrawMesh: Boolean = synchronized { renderParams.drawMesh }
  override def setStaticFriction(staticFriction: Double): Unit = synchronized {
    updater.getLocomotionParams.staticFriction = staticFriction
  }

  override def getStaticFriction: Double = synchronized { updater.getLocomotionParams.staticFriction }
  override def setDynamicFriction(dynamicFriction: Double): Unit = synchronized {
    updater.getLocomotionParams.dynamicFriction = dynamicFriction
  }

  override def getDynamicFriction: Double = synchronized { updater.getLocomotionParams.dynamicFriction }
  def setDirection(direction: Double): Unit = synchronized {
    updater.getLocomotionParams.direction = direction
  }

  override def createDynamicControls = new SnakeDynamicOptions(this)
  override def doOptimization(): Unit = {
    val optimizer = new Optimizer(this)
    setPaused(false)
    optimizer.doOptimization(SnakeSimulator.SearchStrategy, SnakeSimulator.INITIAL_PARAMS, SnakeSimulator.FitnessRange)
  }

  override def timeStep: Double = synchronized {
    if (!isPaused) tStep = updater.stepForward(snake, tStep)
    tStep
  }

  /**
    * Camera is applied only to [[Graphics2D]] (no particle mutation), so window resize cannot
    * race with the animation thread and tear the snake apart. Grid and snake share [[effectiveScale]].
    */
  override def paint(g: Graphics): Unit = synchronized {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, if (getAntialiasing) RenderingHints.VALUE_ANTIALIAS_ON
    else RenderingHints.VALUE_ANTIALIAS_OFF)
    val newCenter = snake.getCenter
    // Physics motion since last frame (for grid scroll + speed readout)
    val distanceDelta = new Vector2d(oldCenter.x - newCenter.x, oldCenter.y - newCenter.y)
    velocity = distanceDelta.length / (getNumStepsPerFrame * math.max(tStep, 1e-9))
    distance.add(distanceDelta)
    oldCenter.set(newCenter.x, newCenter.y)

    val (w, h) = viewPanelSize
    val scale = SnakeSimulator.effectiveScale(renderParams.scale, w, h)
    val cellSizePx = SnakeSimulator.CELL_SIZE * scale
    val (xDim, yDim) = SnakeSimulator.gridCellCounts(w, h, cellSizePx)
    val gridOffset = new Vector2d(distance.x * scale, distance.y * scale)
    val bgRenderer = new BackgroundGridRenderer(gridColor)
    bgRenderer.drawGridBackground(g2, cellSizePx, xDim, yDim, gridOffset)

    // Temporarily put effective scale into params so edge drawing matches window zoom.
    val userScale = renderParams.scale
    val (tx, ty) = SnakeSimulator.cameraTranslation(w, h, scale, newCenter.x, newCenter.y)
    val prior = g2.getTransform
    try {
      renderParams.scale = scale
      g2.translate(tx, ty)
      renderer.render(snake, g2)
    } finally {
      g2.setTransform(prior)
      renderParams.scale = userScale
    }
  }

  override protected def createOptionsDialog = new SnakeOptionsDialog(frame, this)
  override protected def getStatusMessage: String = super.getStatusMessage + "    velocity=" + FormatUtil.formatNumber(velocity)

  /**
    * Implements the Optimizee interface: fitness from average center speed over physics steps only
    * (does not depend on [[paint]] or display refresh).
    */
  override def evaluateFitness(params: ParameterArray): Double = synchronized {
    val locoParams = updater.getLocomotionParams
    locoParams.waveSpeed = params.get(0).getValue
    locoParams.waveAmplitude = params.get(1).getValue
    locoParams.wavePeriod = params.get(2).getValue
    snake.reset()
    updater.resetSimulationTime()
    oldCenter = new Point2d(snake.getCenter)
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
