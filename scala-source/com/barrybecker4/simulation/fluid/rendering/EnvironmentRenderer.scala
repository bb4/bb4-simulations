// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.simulation.common.rendering.ModelImage
import com.barrybecker4.simulation.fluid.model.Grid
import java.awt.Color
import java.awt.Graphics2D


/**
  * Renders a specified fluid environment.
  * @author Barry Becker
  */
object EnvironmentRenderer { // rendering attributes
  private val GRID_COLOR = new Color(30, 30, 30, 10)
  private val VECTOR_COLOR = new Color(200, 60, 30, 50)
  private val VECTOR_SCALE = 40.0
  private val OFFSET = 10
  private val PRESSURE_COLOR_MAP = new PressureColorMap
}

final class EnvironmentRenderer(var grid: Grid, var options: RenderingOptions) {

  private val modelImage = new ModelImage(grid, EnvironmentRenderer.PRESSURE_COLOR_MAP, options.getScale.toInt)

  def getColorMap: PressureColorMap = EnvironmentRenderer.PRESSURE_COLOR_MAP
  def getOptions: RenderingOptions = options

  /** Render the Environment on the screen. */
  def render(g: Graphics2D) {
    // draw the cells colored by ---pressure--- val
    if (options.getShowPressures) concurrentRenderPressures(g)
    // outer boundary
    val scale = options.getScale
    g.drawRect(EnvironmentRenderer.OFFSET,
      EnvironmentRenderer.OFFSET, (grid.getWidth * scale).toInt, (grid.getHeight * scale).toInt)

    // draw the ---velocity--- field (and status)
    if (options.getShowVelocities) drawVectors(g)
    if (options.getShowGrid) drawGrid(g)
  }

  /** If the render options say to use parallelism, then we will render the pressures concurrently. */
  private def concurrentRenderPressures(g2: Graphics2D) = {
    val height = grid.getHeight
    modelImage.setUseLinearInterpolation(options.getUseLinearInterpolation)
    val numProcs = Runtime.getRuntime.availableProcessors()

    val workers = Array.ofDim[Runnable](numProcs)
    val range = height / numProcs
    for (i <- 0 until numProcs - 1) {
      val offset = i * range
      workers(i) = new RenderWorker(modelImage, offset, offset + range)
    }
    // leftover in the last strip, or all of it if only one processor.
    val currentRow = range * (numProcs - 1)
    workers(numProcs - 1)  = new RenderWorker(modelImage, currentRow, height)

    // blocks until all Callables are done running.
    if (options.isParallelized) workers.par.foreach(_.run())
    else workers.foreach(_.run())

    g2.drawImage(modelImage.getImage, EnvironmentRenderer.OFFSET, EnvironmentRenderer.OFFSET, null)
  }

  private def drawGrid(g: Graphics2D) = {
    g.setColor(EnvironmentRenderer.GRID_COLOR)
    val scale = options.getScale
    val rightEdgePos = (scale * grid.getWidth).toInt
    val bottomEdgePos = (scale * grid.getHeight).toInt
    for (j <- 0 until grid.getHeight) { //  -----
      val ypos = (j * scale).toInt
      g.drawLine(EnvironmentRenderer.OFFSET,
        ypos + EnvironmentRenderer.OFFSET, rightEdgePos + EnvironmentRenderer.OFFSET, ypos + EnvironmentRenderer.OFFSET)
    }
    for (i <- 0 until grid.getWidth) { //  ||||
      val xpos = (i * scale).toInt
      g.drawLine(xpos + EnvironmentRenderer.OFFSET,
        EnvironmentRenderer.OFFSET, xpos + EnvironmentRenderer.OFFSET, bottomEdgePos + EnvironmentRenderer.OFFSET)
    }
  }

  private def drawVectors(g: Graphics2D) = {
    g.setColor(EnvironmentRenderer.VECTOR_COLOR)
    val scale = options.getScale
    for (j <- 0 until grid.getHeight) {
      for (i <- 0 until grid.getWidth) {
        val u = grid.getU(i, j)
        val v = grid.getV(i, j)
        val x = (scale * i).toInt + EnvironmentRenderer.OFFSET
        val y = (scale * j).toInt + EnvironmentRenderer.OFFSET
        g.drawLine(x, y,
          (scale * i + EnvironmentRenderer.VECTOR_SCALE * u).toInt + EnvironmentRenderer.OFFSET,
          (scale * j + EnvironmentRenderer.VECTOR_SCALE * v).toInt + EnvironmentRenderer.OFFSET)
      }
    }
  }
}
