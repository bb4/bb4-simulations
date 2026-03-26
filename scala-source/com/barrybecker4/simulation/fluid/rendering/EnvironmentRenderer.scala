// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.simulation.common.rendering.RectangularModelImage
import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import java.awt.Color
import java.awt.Graphics2D
import EnvironmentRenderer._
import scala.collection.parallel.CollectionConverters._

/**
  * Renders a specified fluid environment.
  * @author Barry Becker
  */
object EnvironmentRenderer {
  val ContentOffsetPixels: Int = 10
  private val GRID_COLOR = new Color(30, 30, 30, 10)
  private val VECTOR_COLOR = new Color(200, 60, 30, 50)
  private val VECTOR_SCALE = 40.0
  private val PRESSURE_COLOR_MAP = new PressureColorMap
}

final class EnvironmentRenderer(var env: FluidEnvironment, var options: RenderingOptions) {

  private val modelImage = new RectangularModelImage(env, PRESSURE_COLOR_MAP, options.getScale.toInt)

  def getColorMap: PressureColorMap = PRESSURE_COLOR_MAP
  def getOptions: RenderingOptions = options

  /** Render the Environment on the screen. */
  def render(g: Graphics2D): Unit = {
    // draw the cells colored by ---pressure--- val
    if (options.getShowPressures) concurrentRenderPressures(g)
    // outer boundary
    val scale = options.getScale
    g.drawRect(ContentOffsetPixels,
      ContentOffsetPixels, (env.getWidth * scale).toInt, (env.getHeight * scale).toInt)

    // draw the ---velocity--- field (and status)
    if (options.getShowVelocities) drawVectors(g)
    if (options.getShowGrid) drawGrid(g)
  }

  /** If the render options say to use parallelism, then we will render the pressures concurrently. */
  private def concurrentRenderPressures(g2: Graphics2D): Boolean = {
    val height = env.getHeight
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

    g2.drawImage(modelImage.getImage, ContentOffsetPixels, ContentOffsetPixels, null)
  }

  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(GRID_COLOR)
    val scale = options.getScale
    val rightEdgePos = (scale * env.getWidth).toInt
    val bottomEdgePos = (scale * env.getHeight).toInt
    for (j <- 0 until env.getHeight) { //  -----
      val ypos = (j * scale).toInt
      g.drawLine(ContentOffsetPixels, ypos + ContentOffsetPixels, rightEdgePos + ContentOffsetPixels, ypos + ContentOffsetPixels)
    }
    for (i <- 0 until env.getWidth) { //  ||||
      val xpos = (i * scale).toInt
      g.drawLine(xpos + ContentOffsetPixels,
        ContentOffsetPixels, xpos + ContentOffsetPixels, bottomEdgePos + ContentOffsetPixels)
    }
  }

  private def drawVectors(g: Graphics2D): Unit = {
    g.setColor(VECTOR_COLOR)
    val scale = options.getScale
    for (j <- 0 until env.getHeight) {
      for (i <- 0 until env.getWidth) {
        val u = env.getU(i, j)
        val v = env.getV(i, j)
        val x = (scale * i).toInt + ContentOffsetPixels
        val y = (scale * j).toInt + ContentOffsetPixels
        g.drawLine(x, y,
          (scale * i + VECTOR_SCALE * u).toInt + ContentOffsetPixels,
          (scale * j + VECTOR_SCALE * v).toInt + ContentOffsetPixels)
      }
    }
  }
}
