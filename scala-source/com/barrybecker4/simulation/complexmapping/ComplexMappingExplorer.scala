/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import java.awt.Graphics

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.complexmapping.algorithm.{Box, FunctionType, Grid, MeshMappingModel}
import com.barrybecker4.simulation.complexmapping.algorithm.functions.{ComplexFunction, IdentityFunction}
import com.barrybecker4.ui.util.ColorMap
import javax.swing.JPanel
import javax.vecmath.Point2d

object ComplexMappingExplorer {
  val DEFAULT_FUNCTION: FunctionType.Val = FunctionType.IDENTITY
}

/**
  * Interactively explores what happens when a specified function is applied to a grid of points in the complex plane.
  * @author Barry Becker.
  */
class ComplexMappingExplorer extends Simulator("Complex Mapping Explorer") {

  private var function: ComplexFunction = IdentityFunction()

  private val grid = new Grid(Box(new Point2d(1.0, 3.0), new Point2d(3.0, -3.0)), 0.2, 0.2)
  private var model: MeshMappingModel = new MeshMappingModel(grid, IdentityFunction())
  private var options: DynamicOptions = _
  private var useFixedSize: Boolean = false

  commonInit()

  /** @param fixed if true then the render area does not resize automatically.*/
  def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  def getUseFixedSize: Boolean = useFixedSize

  private def commonInit(): Unit = {
    initCommonUI()
    reset()
  }

  def setFunction(func: ComplexFunction): Unit = {
    function = func
    reset()
  }

  /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
  def getFunction: ComplexFunction = function

  override protected def reset(): Unit = {
    if (options != null) options.reset()
  }

  override protected def createOptionsDialog = new ComplexMappingOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = 1

  override def timeStep: Double = {
    tStep
  }

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    if (g != null) {
      val viewport = Box(new Point2d(0, 4), new Point2d(5, -4))
      g.drawImage(model.getImage(viewport, this.getWidth, this.getHeight), 0, 0, null)
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
