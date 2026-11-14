// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.simulation.common.rendering.RectangularModelImage
import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import java.awt.Color
import java.awt.Graphics2D
import scala.collection.parallel.CollectionConverters._
import EnvironmentRenderer._


/**
  * Renders a specified fluid environment.
  */
object EnvironmentRenderer {
  val ContentOffsetPixels: Int = 10
  private val GRID_COLOR = new Color(30, 30, 30, 10)
  private val VECTOR_COLOR = new Color(200, 60, 30, 50)
  private val VECTOR_SCALE = 40.0
  private val PRESSURE_COLOR_MAP = new PressureColorMap
}

final class EnvironmentRenderer(var env: FluidEnvironment, var options: RenderingOptions) {

  private var modelImage = new RectangularModelImage(env, PRESSURE_COLOR_MAP, options.scale.toInt)
  private var workers: Array[RenderWorker] = Array.empty
  private var workersImage: RectangularModelImage = modelImage

  def getColorMap: PressureColorMap = PRESSURE_COLOR_MAP
  def getOptions: RenderingOptions = options

  def setEnvironment(env: FluidEnvironment): Unit = {
    this.env = env
    modelImage = new RectangularModelImage(env, PRESSURE_COLOR_MAP, options.scale.toInt)
    workers = Array.empty
  }

  /** Render the Environment on the screen. */
  def render(g: Graphics2D): Unit = {
    if (options.showPressures) renderPressures(g)
    val scale = options.scale
    g.drawRect(ContentOffsetPixels,
      ContentOffsetPixels, (env.getWidth * scale).toInt, (env.getHeight * scale).toInt)
    if (options.showVelocities) drawVectors(g)
    if (options.showGrid) drawGrid(g)
  }

  private def renderPressures(g2: Graphics2D): Unit = {
    val height = env.getHeight
    modelImage.setUseLinearInterpolation(options.useLinearInterpolation)

    if (options.isParallelized) {
      val numProcs = math.max(1, Runtime.getRuntime.availableProcessors())
      ensureWorkers(numProcs)
      val range = height / numProcs
      var i = 0
      while (i < numProcs - 1) {
        val offset = i * range
        workers(i).setRange(offset, offset + range)
        i += 1
      }
      workers(numProcs - 1).setRange(range * (numProcs - 1), height)
      workers.par.foreach(_.run())
    } else {
      modelImage.updateImage(0, height)
    }

    g2.drawImage(modelImage.getImage, ContentOffsetPixels, ContentOffsetPixels, null)
  }

  private def ensureWorkers(n: Int): Unit = {
    if (workers.length != n || (workersImage ne modelImage)) {
      workers = Array.tabulate(n)(_ => new RenderWorker(modelImage))
      workersImage = modelImage
    }
  }

  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(GRID_COLOR)
    val scale = options.scale
    val rightEdgePos = (scale * env.getWidth).toInt
    val bottomEdgePos = (scale * env.getHeight).toInt
    var j = 0
    while (j <= env.getHeight) {
      val ypos = (j * scale).toInt
      g.drawLine(ContentOffsetPixels, ypos + ContentOffsetPixels, rightEdgePos + ContentOffsetPixels, ypos + ContentOffsetPixels)
      j += 1
    }
    var i = 0
    while (i <= env.getWidth) {
      val xpos = (i * scale).toInt
      g.drawLine(xpos + ContentOffsetPixels,
        ContentOffsetPixels, xpos + ContentOffsetPixels, bottomEdgePos + ContentOffsetPixels)
      i += 1
    }
  }

  private def drawVectors(g: Graphics2D): Unit = {
    g.setColor(VECTOR_COLOR)
    val scale = options.scale
    var j = 1
    while (j <= env.getHeight) {
      var i = 1
      while (i <= env.getWidth) {
        val u = env.getU(i, j)
        val v = env.getV(i, j)
        // Map solver cells 1..N onto the same 0-based pixel plane as getValue/render
        val px = i - 1
        val py = j - 1
        val x = (scale * px).toInt + ContentOffsetPixels
        val y = (scale * py).toInt + ContentOffsetPixels
        g.drawLine(x, y,
          (scale * px + VECTOR_SCALE * u).toInt + ContentOffsetPixels,
          (scale * py + VECTOR_SCALE * v).toInt + ContentOffsetPixels)
        i += 1
      }
      j += 1
    }
  }
}
