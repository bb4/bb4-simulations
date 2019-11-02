// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.liquid.Logger
import com.barrybecker4.simulation.liquid.compute.VelocityInterpolator
import com.barrybecker4.simulation.liquid.model.LiquidEnvironment
import com.barrybecker4.simulation.liquid.model.Particle
import com.barrybecker4.ui.util.GUIUtil
import java.awt._


/**
  * Renders a specified liquid environment.
  * @author Barry Becker
  */
object EnvironmentRenderer {
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
  private val pressureColorMap: ColorMap = new PressureColorMap
  private val BASE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12)
}

final class EnvironmentRenderer(var env: LiquidEnvironment) {
  private var scale: Double = EnvironmentRenderer.DEFAULT_SCALE
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
    // draw the cells colored by ---pressure--- val
    if (options.getShowPressures) renderPressure(g)
    // draw the ---walls---
    drawWalls(g)
    drawParticles(g)
    if (options.getShowCellStatus) drawCellSymbols(g)
    // draw the ---velocity--- field (and status)
    if (options.getShowVelocities) drawCellFaceVelocities(g)
    val duration = (System.currentTimeMillis - time) / 100.0
    Logger.log(1, "time to render:  (" + duration + ") ")
  }

  private def getMaxY = scale * env.getGrid.getYDimension + EnvironmentRenderer.OFFSET

  /** Draw the cells/grid */
  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(EnvironmentRenderer.GRID_COLOR)
    val grid = env.getGrid
    val xDim = grid.getXDimension
    val yDim = grid.getYDimension
    val rightEdgePos = (scale * xDim).toInt
    val bottomEdgePos = (scale * yDim).toInt
    val maxY = getMaxY.toInt
    for (j <- 0 until yDim) { //  -----
      val ypos = (j * scale).toInt
      g.drawLine(EnvironmentRenderer.OFFSET, maxY - ypos, rightEdgePos + EnvironmentRenderer.OFFSET, maxY - ypos)
    }
    for (i <- 0 until xDim) { //  ||||
      val xpos = (i * scale).toInt
      g.drawLine(xpos + EnvironmentRenderer.OFFSET, maxY, xpos + EnvironmentRenderer.OFFSET, maxY - bottomEdgePos)
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
      g.fillOval((scale * a(0) + offset + EnvironmentRenderer.OFFSET).toInt, y, particleSize, particleSize)
    }
    if (options.getShowVelocities) drawParticleVelocities(g)
  }

  private def getColorForParticle(part: Particle) = {
    val green = if ((part.y.toInt % 2) == 0) 150
    else 50
    var comp = (256.0 * part.getAge / 20.0).toInt
    comp = if (comp > 255) 255
    else comp
    new Color(comp, green, 255 - comp, 80)
  }

  private def drawParticleVelocities(g: Graphics2D): Unit = {
    g.setStroke(EnvironmentRenderer.PARTICLE_VELOCITY_STROKE)
    g.setColor(EnvironmentRenderer.PARTICLE_VELOCITY_COLOR)
    val a: Array[Double] = new Array[Double](2)
    val grid = env.getGrid
    val interpolator = new VelocityInterpolator(grid)
    val maxY = getMaxY

    for (p <- env.getParticles) {
      if (options.getShowVelocities) {
        val vel = interpolator.findVelocity(p)
        p.get(a)
        val x = (scale * a(0)) + EnvironmentRenderer.OFFSET
        val xLen = x + EnvironmentRenderer.VELOCITY_SCALE * vel.x
        val y = maxY - scale * a(1)
        val yLen = y - EnvironmentRenderer.VELOCITY_SCALE * vel.y
        g.drawLine(x.toInt, y.toInt, xLen.toInt, yLen.toInt)
      }
    }
  }

  /** PathColor the squares according to the pressure in that discrete region. */
  private def renderPressure(g: Graphics2D): Unit = {
    val grid = env.getGrid
    val maxY = getMaxY

    for (j <- 0 until grid.getYDimension) {
      for (i <- 0 until grid.getXDimension) {
        g.setColor(EnvironmentRenderer.pressureColorMap.getColorForValue(grid.getCell(i, j).getPressure))
        g.fillRect((scale * i).toInt + EnvironmentRenderer.OFFSET, (maxY - scale * j).toInt, scale.toInt, scale.toInt)
      }
    }
  }

  /** Draw walls and boundary.  */
  private def drawWalls(g: Graphics2D): Unit = {
    val wallStroke = new BasicStroke(wallLineWidth)
    g.setStroke(wallStroke)
    g.setColor(EnvironmentRenderer.WALL_COLOR)
    /*
            //Stroke stroke = new BasicStroke(wall.getThickness(), BasicStroke.CAP_BUTT,
            //                                BasicStroke.JOIN_ROUND, 10);
            for (i=0; i<walls_.size(); i++)  {
                Wall wall = (Wall)walls_.elementAt(i);
                g.drawLine( (int)(wall.getStartPoint().getX()*rat+OFFSET),
                            (int)(maxY - (wall.getStartPoint().getY()*rat+OFFSET)),
                            (int)(wall.getStopPoint().getX()*rat+OFFSET),
                            (int)(maxY - (wall.getStopPoint().getY()*rat+OFFSET)) );
            }*/
    // outer boundary
    g.drawRect(EnvironmentRenderer.OFFSET, EnvironmentRenderer.OFFSET,
      (env.getGrid.getXDimension * scale).toInt, (env.getGrid.getYDimension * scale).toInt)
  }

  /** Draw text representing internal state for debug purposes. */
  private def drawCellSymbols(g: Graphics2D): Unit = {
    val grid = env.getGrid
    g.setColor(EnvironmentRenderer.TEXT_COLOR)
    g.setFont(EnvironmentRenderer.BASE_FONT)
    val strBuf = new StringBuilder("12")
    val maxY = getMaxY

    for (j <- 0 until grid.getYDimension) {
      for (i <- 0 until grid.getXDimension) {
        val x = (scale * i).toInt + EnvironmentRenderer.OFFSET
        val y = (maxY - scale * (j + 1)).toInt
        strBuf.append(0, grid.getCell(i, j).getStatus.toString)
        strBuf.setLength(1)
        //int nump = grid.getCell(i, j).getNumParticles();
        //if ( nump > 0 )
        //    strBuf.append( String.valueOf( nump ) );
        g.drawString(strBuf.toString, x + 6, y + 18)
      }
    }
  }

  /** There is a velocity vector in the center of each cell face. */
  private def drawCellFaceVelocities(g: Graphics2D): Unit = {
    g.setStroke(EnvironmentRenderer.FACE_VELOCITY_STROKE)
    g.setColor(EnvironmentRenderer.FACE_VELOCITY_COLOR)
    val grid = env.getGrid
    val maxY = getMaxY
    for (j <- 0 until grid.getYDimension) {
      for (i <- 0 until grid.getXDimension) {
        val cell = grid.getCell(i, j)
        val u = cell.getU
        val v = cell.getV
        val x = (scale * i).toInt + EnvironmentRenderer.OFFSET
        val xMid = (scale * (i + 0.5)).toInt + EnvironmentRenderer.OFFSET
        val xLen = (scale * i + EnvironmentRenderer.VELOCITY_SCALE * u).toInt + EnvironmentRenderer.OFFSET
        val y = (maxY - scale * j).toInt
        val yMid = (maxY - (scale * (j + 0.5))).toInt
        val yLen = (maxY - (scale * j + EnvironmentRenderer.VELOCITY_SCALE * v)).toInt
        g.drawLine(xMid, y, xMid, yLen)
        g.drawLine(x, yMid, xLen, yMid)
      }
    }
  }
}
