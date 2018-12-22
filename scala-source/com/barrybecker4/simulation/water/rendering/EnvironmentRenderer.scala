// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.rendering

import java.awt.{Color, Graphics2D}
import com.barrybecker4.simulation.water.model.Grid
import EnvironmentRenderer._


/**
  * Renders the moving water in its current state.
  * @author Barry Becker
  */
object EnvironmentRenderer {
  private val VECTOR_COLOR = new Color(200, 60, 30, 50)
  private val VECTOR_SCALE = 40.0
  private val OFFSET = 10
  private val NUM_STEPS_PER_FRAME = 1

  private val MAX = 320;  // there are MAX control points
  private val MAX_CON = 31  // max number of connected regions
  private val GRAVITY = 9.8
  private val DX = 0.02
  private val EPS = 0.000000123

  private var GROUND_COLOR = new Color(70, 50, 40)
  private var WATER_COLOR = new Color(100, 200, 255)
}

final class EnvironmentRenderer(var grid: Grid, var options: RenderingOptions) {

  private val h0, h1, floor = Array.ofDim[Double](MAX)
  private var x, y, t, df: Double = 0
  private var viscosity: Double = 0.0
  private var xStep: Int = 1  // 320 div MAX;
  private var sel: Char = 'a'
  private var done = false

  initBoundary()

  private def initBoundary(): Unit = {
    for (i <- 0 until MAX) {
      val ht = 70.0 + 25.0 * Math.sin(0.06 * i)  + i / 3.0
      h0(i) = ht
      h1(i) = ht
      floor(i) = 160.0 + 42.0 * Math.cos(0.045 * i)
    }
  }

  private def drawGround(g: Graphics2D): Unit = {
    g.setColor(GROUND_COLOR)
    for (i <- 0 until MAX) {
      val j = Math.round(floor(i)).toInt
      g.drawRect(xStep * i, j - 1, xStep - 1, 2)
    }
  }

  private def drawWater(g: Graphics2D): Unit = {
    g.setColor(WATER_COLOR)
    for (i <- 0 until MAX) {
      val top = Math.round(h1(i)).toInt
      val bot = Math.round(floor(i)).toInt - 2
      if (top < bot) {
        g.drawRect(xStep * i, top, xStep - 1, bot - top)
      }
    }
  }

  /**
    * Solve for x in x = A * rhs
    * @param array the tri-diagonal N*3 matrix
    * @param rhs right hand side vector
    * @param x the solution
    */
  private def solve(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {

    for (i <- 0 until MAX - 1) {
      val ip = i + 1
      if (array(i)(1) < EPS)
        array(i)(1) = EPS
      val temp = array(ip)(0) / array(i)(1)
      rhs(ip) -= rhs(i) * temp
      array(ip)(1) -= array(i)(2) * temp
      //println(s" b rhs($ip)=${rhs(ip)} a2=${array(ip)(2)} x(${i+1}) = ${x(i + 1)} " +
      //  s"temp=$temp den=${array(ip)(1)}")
    }
    x(MAX - 1) = rhs(MAX - 1) / array(MAX - 1)(1)
    for (i <- MAX - 2 to 0 by -1) {
      val a = array(i)
      if (a(1) < EPS)
        a(1) = EPS
      //println(s"rhs($i)=${rhs(i)} a2=${a(2)} x(${i+1}) = ${x(i + 1)}  den=${a(1)}")
      x(i) = (rhs(i) - a(2) * x(i + 1)) / a(1)
      //println(s"x($i) = ${x(i)}")
    }
  }

  private def integrate(dt: Double): Unit = {
    val array = Array.fill[Double](MAX, 3)(0)
    val rhs, d, dist = Array.fill[Double](MAX)(0)
    val ct, start, stop = Array.fill[Int](MAX_CON)(0)
    val oldVolume, newVolume = Array.fill[Double](MAX_CON)(0)

    for (i <- 0 until MAX) {
      dist(i) = floor(i) - h1(i)
      // oldVolume = oldVolume.dist(i)
      if (dist(i) < 0)
        dist(i) = 0
      rhs(i) = h1(i) + (1.0 - viscosity) * (h1(i) - h0(i))
    }

    val c = 0.5 * GRAVITY * (dt * dt) / (DX * DX)
    //println("  --  c = "+ c)
    for (i <- 0 until MAX - 1) {
      d(i) = c * (dist(i) + dist(i + 1))
    }
    var a = array(0)
    a(0) = 1.0         // unused
    a(1) = 1.0 + d(0)  // left boundary
    a(2) = -d(0)
    for (i <- 1 until MAX - 1) {
      a = array(i)
      a(0) = -d(i - 1)
      a(1) = 1.0 + d(i - 1) + d(i)
      a(2) = -d(i)
    }
    a = array(MAX - 1)
    a(0) = -d(MAX - 2)    // right boundary
    a(1) = 1.0 + d(MAX - 2)
    a(2) = 1.0           // unused
    for (i <- 0 until MAX) {
      h0(i) = h1(i)
    }

    solve(array, rhs, h1)

    // conserve volume over the connected regions
    for (i <- 0 until MAX_CON) {
      newVolume(i) = 0
      oldVolume(i) = 0
      ct(i) = 0
    }

    var i: Int = 0
    var connect: Int = 0
    while (i < MAX) {
      // determine the connected regions and their volumes
      while (i < MAX && h1(i) >= floor(i)) {  // should not need i < MAX &&
        i += 1 // step over empty region
      }

      //if (h1(i)<=floor(i)) && ((i==0) or (h1(i-1)>floor(i-1))) then...
      //println("connect = " + connect + " i=" + i + " h1=" + h1(i) + " fl=" + floor(i))
      start(connect) = i  // valid region
      while (i < MAX && h1(i) < floor(i)) {   // shoui < MAX &&
        oldVolume(connect) = oldVolume(connect) + floor(i) - h0(i)
        newVolume(connect) = newVolume(connect) + floor(i) - h1(i)
        ct(connect) += 1
        i += 1
        //println("2 i=" + i)
      }
      stop(connect) = i - 1
      connect += 1
    }

    for (j <- 0 until connect) {
      val adjust = (newVolume(j) - oldVolume(j)) / ct(j)
      for (i <- start(j) to stop(j)) {
        h1(i) += adjust
      }
    }

    for (i <- 0 until MAX) {
      if (h1(i) > floor(i)) {
        h1(i) = floor(i) + EPS
        h0(i) = floor(i) + EPS
      }
    }
  }

  def getOptions: RenderingOptions = options

  def stepForward(dt: Double): Double = {
    for (i <- 0 until NUM_STEPS_PER_FRAME) {
      integrate(dt)
    }
    dt
  }

  /** Render the Environment on the screen. */
  def render(g: Graphics2D) {
    g.drawRect(EnvironmentRenderer.OFFSET, EnvironmentRenderer.OFFSET, grid.width, grid.height)
    drawGround(g)
    drawWater(g)
    //if (options.getShowVelocities) drawVectors(g)
  }

  private def drawVectors(g: Graphics2D): Unit = {
    g.setColor(EnvironmentRenderer.VECTOR_COLOR)
    for (j <- 0 until grid.height) {
      for (i <- 0 until grid.width) {
        //val u = grid.getU(i, j)
        //val v = grid.getV(i, j)
        val x = i + EnvironmentRenderer.OFFSET
        val y = j + EnvironmentRenderer.OFFSET
        g.drawLine(x, y,
          (i + EnvironmentRenderer.VECTOR_SCALE).toInt + EnvironmentRenderer.OFFSET,
          (j + EnvironmentRenderer.VECTOR_SCALE).toInt + EnvironmentRenderer.OFFSET)
      }
    }
  }
}
