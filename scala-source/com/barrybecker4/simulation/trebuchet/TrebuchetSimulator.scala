// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.optimizee.EvaluationBudgetExceededException
import com.barrybecker4.optimization.parameter.{ParameterArray, ParameterArrayWithFitness}
import com.barrybecker4.simulation.common.ui.NewtonianSimulator
import com.barrybecker4.simulation.trebuchet.model.RenderablePart
import com.barrybecker4.simulation.trebuchet.model.Trebuchet
import javax.swing._
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt._

import scala.compiletime.uninitialized
import scala.util.Random


/**
  * Physically base dynamic simulation of a trebuchet firing.
  * Try simulating using Breve.
  * @author Barry Becker
  */
object TrebuchetSimulator {
  private val DEFAULT_NUM_STEPS_PER_FRAME = 1
  // the amount to advance the animation in time for each frame in seconds
  private val BACKGROUND_COLOR = new Color(253, 250, 253)
}

class TrebuchetSimulator() extends NewtonianSimulator("Trebuchet") with ChangeListener {
  private var trebuchet: Trebuchet = uninitialized
  private var zoomSlider: JSlider = uninitialized
  private var optimizeButton: JButton = uninitialized
  private var optimizeStatus: JLabel = uninitialized

  /** Evaluations used by the Optimize button. Tests set this lower than the default. */
  private[trebuchet] var optimizationEvaluations: Int = TrebuchetParameters.MaxEvaluations

  /** Best design scored during the current search. 2.0.0 throws when the evaluation budget is used up. */
  private var bestSeen: Option[ParameterArrayWithFitness] = None

  reset()
  this.setPreferredSize(new Dimension(800, 900))

  def this(trebuchet: Trebuchet) = {
    this()
    commonInit(trebuchet)
  }

  private def commonInit(trebuchet: Trebuchet): Unit = {
    this.trebuchet = trebuchet
    setNumStepsPerFrame(TrebuchetSimulator.DEFAULT_NUM_STEPS_PER_FRAME)
    this.setBackground(TrebuchetSimulator.BACKGROUND_COLOR)
    initCommonUI()
    this.render()
  }

  override protected def reset(): Unit = {
    val trebuchet = new Trebuchet
    commonInit(trebuchet)
  }

  override def getBackground: Color = TrebuchetSimulator.BACKGROUND_COLOR

  override def createTopControls: JPanel = {
    val controls = super.createTopControls
    val zoomPanel = new JPanel
    zoomPanel.setLayout(new FlowLayout)
    val zoomLabel = new JLabel(" Zoom")
    zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 15, 255, 200)
    zoomSlider.addChangeListener(this)
    zoomPanel.add(zoomLabel)
    zoomPanel.add(zoomSlider)
    controls.add(zoomPanel)
    optimizeButton = new JButton("Optimize")
    optimizeButton.setName("optimize")
    optimizeButton.addActionListener(_ => startOptimization())
    optimizeStatus = new JLabel(" ")
    optimizeStatus.setName("optimizeStatus")
    controls.add(optimizeButton)
    controls.add(optimizeStatus)
    controls
  }

  /** Run genetic search on a background thread so the window stays responsive, then show the best design. */
  private def startOptimization(): Unit = {
    optimizeButton.setEnabled(false)
    optimizeStatus.setText("Optimizing...")
    setPaused(true)
    val thread = new Thread(() => {
      val outcome =
        try Right(optimizeDesign())
        catch { case e: Exception => Left(Option(e.getMessage).getOrElse(e.toString)) }
      SwingUtilities.invokeLater(() => showOptimization(outcome))
    }, "trebuchet-optimize")
    thread.setDaemon(true)
    thread.start()
  }

  private def showOptimization(outcome: Either[String, ParameterArrayWithFitness]): Unit = {
    outcome match {
      case Right(best) =>
        optimizeStatus.setText(TrebuchetFitness.statusText(best.fitness))
        repaint()
      case Left(message) =>
        optimizeStatus.setText("Optimization failed: " + message)
    }
    optimizeButton.setEnabled(true)
  }

  /**
    * Search from the current design and leave the trebuchet reset at the best parameters found.
    * The animation stays paused; press Start to watch that design fire.
    */
  private[trebuchet] def optimizeDesign(): ParameterArrayWithFitness = {
    bestSeen = None
    val params = TrebuchetParameters.initialArray(trebuchet, new Random())
    val best =
      try
        new Optimizer(this).doOptimization(
          TrebuchetParameters.SearchStrategy,
          params,
          TrebuchetParameters.FitnessRange,
          new Random(),
          Some(optimizationEvaluations)
        ).best
      catch {
        case _: EvaluationBudgetExceededException =>
          bestSeen.getOrElse(ParameterArrayWithFitness(params, TrebuchetFitness.UnstablePenalty))
      }
    synchronized {
      trebuchet.reset()
      TrebuchetParameters.apply(trebuchet, best.pa)
    }
    best
  }

  override def doOptimization(): Unit = { optimizeDesign(); () }

  def getNumParameters: Int = TrebuchetParameters.All.size
  override protected def getInitialTimeStep: Double = TrebuchetFitness.TimeStep
  override protected def createOptionsDialog = new TrebuchetOptionsDialog(frame, this)

  override def timeStep: Double = synchronized {
    if (!isPaused) tStep = trebuchet.stepForward(tStep)
    tStep
  }

  override def paint(g: Graphics): Unit = synchronized {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setColor(TrebuchetSimulator.BACKGROUND_COLOR)
    val dim = getSize()
    g2.fillRect(0, 0, dim.width, dim.height)
    val aliasing = if (getAntialiasing) RenderingHints.VALUE_ANTIALIAS_ON
                   else RenderingHints.VALUE_ANTIALIAS_OFF
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aliasing)
    // draw the trebuchet in its current position
    trebuchet.render(g2)
  }

  override def setScale(scale: Double): Unit = {
    trebuchet.setScale(scale)
  }
  override def getScale: Double = trebuchet.getScale

  override def setShowVelocityVectors(show: Boolean): Unit = {
    RenderablePart.showVelocityVectors = show
  }
  override def getShowVelocityVectors: Boolean = RenderablePart.showVelocityVectors

  override def setShowForceVectors(show: Boolean): Unit = {
    RenderablePart.showForceVectors = show
  }
  override def getShowForceVectors: Boolean = RenderablePart.showForceVectors

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
      val v = zoomSlider.getValue.toDouble / 200.0
      trebuchet.setScale(v)
      this.repaint()
    }
  }

  /** Headless throw distance. Lower is better. Does not sleep and does not open a window. */
  override def evaluateFitness(params: ParameterArray): Double = synchronized {
    val fitness = TrebuchetFitness.shoot(trebuchet, params).fitness
    val candidate = ParameterArrayWithFitness(params, fitness)
    bestSeen = bestSeen match {
      case Some(prev) if prev.fitness <= candidate.fitness => Some(prev)
      case _ => Some(candidate)
    }
    fitness
  }
}
