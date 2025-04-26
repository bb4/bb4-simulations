// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

import com.barrybecker4.simulation.liquid.model.{LegacyEnvironment, LegacyParticle}
import com.barrybecker4.ui.util.GUIUtil
import java.awt.*


/**
  * Renders a specified liquid environment.
  * @author Barry Becker
  */
object LegacyEnvironmentRenderer {
  // rendering style attributes
  private val GRID_COLOR = new Color(20, 20, 20, 15)
  private val PARTICLE_VELOCITY_COLOR = new Color(225, 0, 35, 20)
  private val PARTICLE_VELOCITY_STROKE = new BasicStroke(0.2f)
  private val FACE_VELOCITY_COLOR = new Color(205, 90, 25, 110)
  private val FACE_VELOCITY_STROKE = new BasicStroke(2.0f)
  private val VELOCITY_SCALE = 8.0
  private val WALL_COLOR = new Color(100, 210, 170, 150)
  private val TEXT_COLOR = new Color(10, 10, 170, 200)
  /** scales the size of everything */
  private val DEFAULT_SCALE = 30
  /* grid offset  */ private val OFFSET = 10
  private val BASE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12)
}

final class LegacyEnvironmentRenderer(var env: LegacyEnvironment) {
  private var scale: Double = LegacyEnvironmentRenderer.DEFAULT_SCALE
  private var wallLineWidth: Float = 0.0F
  private var particleSize = 0
  private val options = new RenderingOptions

  def setScale(scale: Double): Unit = {
    this.scale = scale
    wallLineWidth = (scale / 5.0).toFloat + 1
    particleSize = (scale / 6.0).toInt + 1
  }

  /** Figure out the biggest scale based in on which dimension is bumped up to first */
  private def determineScaling(width: Int, height: Int): Unit = {
    val grid = env.getGrid
    val proposedXScale = width / grid.getXDimension
    val proposedYScale = height / grid.getYDimension
    setScale(Math.min(proposedXScale, proposedYScale))
  }

  def getScale: Double = scale
  def getRenderingOptions: RenderingOptions = options

  /** Render the Environment on the screen. */
  def render(g: Graphics2D, width: Int, height: Int): Unit = {
    val time = System.currentTimeMillis
    determineScaling(width, height)
    // make sure all the cell statuses are in a consistent state
    env.getGrid.updateCellStatus()
    drawGrid(g)
    drawWalls(g)
    drawParticles(g)
    val duration = (System.currentTimeMillis - time) / 100.0
  }

  private def getMaxY = scale * env.getGrid.getYDimension + LegacyEnvironmentRenderer.OFFSET

  /** Draw the cells/grid */
  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(LegacyEnvironmentRenderer.GRID_COLOR)
    val grid = env.getGrid
    val xDim = grid.getXDimension
    val yDim = grid.getYDimension
    val rightEdgePos = (scale * xDim).toInt
    val bottomEdgePos = (scale * yDim).toInt
    val maxY = getMaxY.toInt
    for (j <- 0 until yDim) { //  -----
      val ypos = (j * scale).toInt
      g.drawLine(LegacyEnvironmentRenderer.OFFSET, maxY - ypos, rightEdgePos + LegacyEnvironmentRenderer.OFFSET, maxY - ypos)
    }
    for (i <- 0 until xDim) { //  ||||
      val xpos = (i * scale).toInt
      g.drawLine(xpos + LegacyEnvironmentRenderer.OFFSET, maxY, xpos + LegacyEnvironmentRenderer.OFFSET, maxY - bottomEdgePos)
    }
  }

  /** Draw the particles in the liquid in the environment. */
  private def drawParticles(g: Graphics2D): Unit = { // draw the ---particles--- of liquid
    val a: Array[Double] = new Array[Double](2)
    val maxY = getMaxY

    for (p <- env.getParticles) {
      p.get(a)
      g.setColor(getColorForParticle(p))
      val offset = -particleSize / 2.0
      val y = (maxY - (scale * a(1) - offset)).toInt
      g.fillOval((scale * a(0) + offset + LegacyEnvironmentRenderer.OFFSET).toInt, y, particleSize, particleSize)
    }
  }

  private def getColorForParticle(part: LegacyParticle) = {
    val green = if ((part.y.toInt % 2) == 0) 150
    else 50
    var comp = (256.0 * part.getAge / 20.0).toInt
    comp = if (comp > 255) 255
    else comp
    new Color(comp, green, 255 - comp, 80)
  }

  private def drawParticleVelocities(g: Graphics2D): Unit = {
    g.setStroke(LegacyEnvironmentRenderer.PARTICLE_VELOCITY_STROKE)
    g.setColor(LegacyEnvironmentRenderer.PARTICLE_VELOCITY_COLOR)
    val a: Array[Double] = new Array[Double](2)
    val grid = env.getGrid
    //val interpolator = new VelocityInterpolator(grid)
    val maxY = getMaxY
  }

  /** Draw walls and boundary.  */
  private def drawWalls(g: Graphics2D): Unit = {
    val wallStroke = new BasicStroke(wallLineWidth)
    g.setStroke(wallStroke)
    g.setColor(LegacyEnvironmentRenderer.WALL_COLOR)
    // outer boundary
    g.drawRect(LegacyEnvironmentRenderer.OFFSET, LegacyEnvironmentRenderer.OFFSET,
      (env.getGrid.getXDimension * scale).toInt, (env.getGrid.getYDimension * scale).toInt)
  }

  /** Draw text representing internal state for debug purposes. */
  private def drawCellSymbols(g: Graphics2D): Unit = {
    val grid = env.getGrid
    g.setColor(LegacyEnvironmentRenderer.TEXT_COLOR)
    g.setFont(LegacyEnvironmentRenderer.BASE_FONT)
    val strBuf = new StringBuilder("12")
    val maxY = getMaxY

    for (j <- 0 until grid.getYDimension) {
      for (i <- 0 until grid.getXDimension) {
        val x = (scale * i).toInt + LegacyEnvironmentRenderer.OFFSET
        val y = (maxY - scale * (j + 1)).toInt
        strBuf.append(0, grid.getCell(i, j).getStatus.toString)
        strBuf.setLength(1)
        g.drawString(strBuf.toString, x + 6, y + 18)
      }
    }
  }

  /** There is a velocity vector in the center of each cell face. */
  private def drawCellFaceVelocities(g: Graphics2D): Unit = {
    g.setStroke(LegacyEnvironmentRenderer.FACE_VELOCITY_STROKE)
    g.setColor(LegacyEnvironmentRenderer.FACE_VELOCITY_COLOR)
    val grid = env.getGrid
    val maxY = getMaxY
    for (j <- 0 until grid.getYDimension) {
      for (i <- 0 until grid.getXDimension) {
        val cell = grid.getCell(i, j)
        val u = cell.getU
        val v = cell.getV
        val x = (scale * i).toInt + LegacyEnvironmentRenderer.OFFSET
        val xMid = (scale * (i + 0.5)).toInt + LegacyEnvironmentRenderer.OFFSET
        val xLen = (scale * i + LegacyEnvironmentRenderer.VELOCITY_SCALE * u).toInt + LegacyEnvironmentRenderer.OFFSET
        val y = (maxY - scale * j).toInt
        val yMid = (maxY - (scale * (j + 0.5))).toInt
        val yLen = (maxY - (scale * j + LegacyEnvironmentRenderer.VELOCITY_SCALE * v)).toInt
        g.drawLine(xMid, y, xMid, yLen)
        g.drawLine(x, yMid, xLen, yMid)
      }
    }
  }
}
