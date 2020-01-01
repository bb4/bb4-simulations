/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import java.awt.Graphics
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.complexmapping.algorithm.{FunctionType, MeshColorMap}
import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import com.barrybecker4.ui.util.ColorMap
import javax.swing.JPanel
import javax.vecmath.Point2d
import ComplexMappingExplorer._
import com.barrybecker4.simulation.complexmapping.algorithm.model.{Box, Grid, MeshMappingModel}


object ComplexMappingExplorer {
  val DEFAULT_ORIG_GRID_BOUNDS: Box = Box(new Point2d(0.0, 1.0), new Point2d(2, -2))
  val DEFAULT_FUNCTION: FunctionType.Val = FunctionType.RIEMANN_ZETA
  val DEFAULT_N: Int = 2
  val DEFAULT_INTERPOLATION_VAL: Double = 1.0
  val DEFAULT_MESH_DETAIL: Double = 0.1
}

/**
  * Interactively explores what happens when a specified function is applied to a grid of points in the complex plane.
  * Ideas for improvement:
  *  - dropdown for color function, slider for scaling color
  *  - dropdown for complex mapping function and UI for its options
  *  - figure out how to analytically extend zeta function.
  * @author Barry Becker.
  */
class ComplexMappingExplorer extends Simulator("Complex Mapping Explorer") {

  private var function: ComplexFunction = DEFAULT_FUNCTION.function
  private var origGridBounds = DEFAULT_ORIG_GRID_BOUNDS
  private var increment = DEFAULT_MESH_DETAIL
  private var grid = new Grid(origGridBounds, increment, increment)
  private var theN = DEFAULT_N
  private var model: MeshMappingModel = MeshMappingModel(grid, function, theN, DEFAULT_INTERPOLATION_VAL)
  private var options: DynamicOptions = _
  private var useFixedSize: Boolean = false
  private var interpolationValue = DEFAULT_INTERPOLATION_VAL
  private var colorMap: ColorMap = new MeshColorMap()

  commonInit()

  /** @param fixed if true then the render area does not resize automatically.*/
  def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  def getUseFixedSize: Boolean = useFixedSize

  private def commonInit(): Unit = {
    initCommonUI()
    redraw()
  }

  def setFunction(func: ComplexFunction): Unit = {
    function = func
    redraw()
  }

  def setOriginalGridBounds(bounds: Box): Unit = {
    origGridBounds = bounds
    grid = new Grid(origGridBounds, increment, increment)
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
    grid = new Grid(origGridBounds, increment, increment)
    redraw()
  }

  /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
  def getFunction: ComplexFunction = function

  override protected def reset(): Unit = {
    if (options != null) options.reset()
    redraw()
  }

  def redraw(): Unit = {
    model = MeshMappingModel(grid, function, theN, interpolationValue, colorMap)
    this.repaint()
  }

  override protected def createOptionsDialog = new ComplexMappingOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = 1
  override def timeStep: Double = tStep

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    if (g != null) {
      g.drawImage(model.getImage(this.getWidth, this.getHeight), 0, 0, null)
    }
  }

  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  def getColorMap: ColorMap = model.getColorMap
}
