/* Copyright by Barry G. Becker, 2019-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import java.awt.{Color, Font, Graphics, Graphics2D}
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.complexmapping.algorithm._
import com.barrybecker4.simulation.complexmapping.algorithm.functions._
import com.barrybecker4.ui.util.ColorMap
import javax.swing.JPanel
import javax.vecmath.Point2d
import ComplexMappingExplorer._
import com.barrybecker4.simulation.complexmapping.algorithm.model.{Box, DomainMappingModel, Grid, MeshMappingModel}


object ComplexMappingExplorer {
  val DEFAULT_ORIG_GRID_BOUNDS: Box = Box(new Point2d(-1.0, 1.5), new Point2d(2, -2))
  /** Critical strip covering the first ~10 non-trivial zeros. */
  val DEFAULT_DOMAIN_VIEWPORT: Box = Box(new Point2d(0.0, 52.0), new Point2d(1.0, 0.0))
  val DEFAULT_FUNCTION: FunctionType = FunctionType.ZETA_ANALYTIC_EXTENSION
  val DEFAULT_N: Int = 50
  val DEFAULT_INTERPOLATION_VAL: Double = 1.0
  val DEFAULT_MESH_DETAIL: Double = 0.1
  val DEFAULT_DOMAIN_COLOR_MODE: DomainColorMode = DomainColorMode.Magnitude

  def isZetaFamily(func: ComplexFunction): Boolean = func match {
    case _: RiemannZetaFunction | _: ZetaAnalyticExtensionFunction | _: DirichletEtaFunction => true
    case _ => false
  }
}

/**
  * Interactively explores complex mappings: domain heatmap (left) for locating zeros,
  * and a warped mesh (right) for seeing how the function transforms the plane.
  * @author Barry Becker.
  */
class ComplexMappingExplorer extends Simulator("Complex Mapping Explorer") {

  private var function: ComplexFunction = DEFAULT_FUNCTION.function
  private var origGridBounds = DEFAULT_ORIG_GRID_BOUNDS
  private var domainViewport = DEFAULT_DOMAIN_VIEWPORT
  private var increment = DEFAULT_MESH_DETAIL
  private var grid = new Grid(origGridBounds, increment, increment)
  private var theN = DEFAULT_N
  private var model: MeshMappingModel = MeshMappingModel(grid, function, theN, DEFAULT_INTERPOLATION_VAL)
  private var domainModel: DomainMappingModel = uninitializedDomainModel()
  private var options: Option[DynamicOptions] = None
  private var interpolationValue = DEFAULT_INTERPOLATION_VAL
  private var colorMap: ColorMap = new MeshColorMap()
  private var domainColorMode: DomainColorMode = DEFAULT_DOMAIN_COLOR_MODE
  private var magnitudeColorMap: ColorMap = new MagnitudeColorMap()
  private var argumentColorMap: ColorMap = new ArgumentColorMap()

  commonInit()

  private def commonInit(): Unit = {
    initCommonUI()
    redraw()
  }

  private def uninitializedDomainModel(): DomainMappingModel =
    DomainMappingModel(
      function, theN, domainViewport, increment, domainColorMode,
      domainColorMap, showKnownZeros = isZetaFamily(function)
    )

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createResetButton)
    controls
  }

  def setFunction(func: ComplexFunction): Unit = {
    function = func
    redraw()
  }

  def setOriginalGridBounds(bounds: Box): Unit = {
    origGridBounds = bounds
    rebuildGrid()
    redraw()
  }

  def setInterpolation(v: Double): Unit = {
    interpolationValue = v
    redraw()
  }

  def setN(n: Int): Unit = {
    theN = n
    redraw()
  }

  def setMeshDetailIncrement(inc: Double): Unit = {
    increment = inc
    rebuildGrid()
    redraw()
  }

  def setDomainColorMode(mode: DomainColorMode): Unit = {
    domainColorMode = mode
    redraw()
  }

  def getDomainColorMode: DomainColorMode = domainColorMode

  private def rebuildGrid(): Unit = {
    grid = new Grid(origGridBounds, increment, increment)
  }

  /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
  def getFunction: ComplexFunction = function

  override protected def reset(): Unit = {
    options.foreach(_.reset())
    redraw()
  }

  def redraw(): Unit = {
    model = MeshMappingModel(grid, function, theN, interpolationValue, colorMap)
    domainModel = DomainMappingModel(
      function, theN, domainViewport, increment, domainColorMode,
      domainColorMap, showKnownZeros = isZetaFamily(function)
    )
    this.repaint()
  }

  override protected def createOptionsDialog = new ComplexMappingOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = 1
  override def timeStep: Double = tStep

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    if (g != null) {
      val w = this.getWidth
      val h = this.getHeight
      val half = Math.max(1, w / 2)
      val domainImg = domainModel.getImage(half, h)
      val meshImg = model.getImage(w - half, h)
      g.drawImage(domainImg, 0, 0, null)
      g.drawImage(meshImg, half, 0, null)
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setColor(new Color(240, 240, 245))
      g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12))
      g2.drawString("Mapped mesh", half + 10, 16)
      g2.setColor(new Color(80, 80, 90))
      g2.drawLine(half, 0, half, h)
    }
  }

  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    val panel = new DynamicOptions(this)
    options = Some(panel)
    panel
  }

  def getColorMap: ColorMap = model.getColorMap

  def domainColorMap: ColorMap = domainColorMode match {
    case DomainColorMode.Magnitude => magnitudeColorMap
    case DomainColorMode.Argument => argumentColorMap
  }

  def getMagnitudeColorMap: ColorMap = magnitudeColorMap
  def getArgumentColorMap: ColorMap = argumentColorMap
}
