// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.model

import Environment._

/**
  * Renders the moving water in its current state.
  *
  * @author Barry Becker
  */
object Environment {
  private val MAX_CON = 31  // max number of connected regions
  private val GRAVITY = 9.8
  private val DX = 0.02
  private val EPS = 0.000000123
}
/**
  * Dimensions of the environment
  * @author Barry Becker
  */
class Environment(val width: Int, val height: Int) {

  val h0, h1, floor = Array.ofDim[Double](width)
  private var x, y, t, df: Double = 0
  private var viscosity: Double = 0.0
  var xStep: Int = 1  // 320 div MAX;
  private var done = false
  reset()

  def reset(): Unit = {
    initBoundary()
  }

  private def initBoundary(): Unit = {
    for (i <- 0 until width) {
      val ht = 70.0 + 25.0 * Math.sin(0.06 * i)  + i / 3.0
      h0(i) = ht
      h1(i) = ht
      floor(i) = 160.0 + 42.0 * Math.cos(0.045 * i)
    }
  }

  /**
    * Solve for x in x = A * rhs
    * @param array the tri-diagonal N*3 matrix
    * @param rhs right hand side vector
    * @param x the solution
    */
  private def solve(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {

    for (i <- 0 until width - 1) {
      val ip = i + 1
      if (array(i)(1) < EPS)
        array(i)(1) = EPS
      val temp = array(ip)(0) / array(i)(1)
      rhs(ip) -= rhs(i) * temp
      array(ip)(1) -= array(i)(2) * temp
      //println(s" b rhs($ip)=${rhs(ip)} a2=${array(ip)(2)} x(${i+1}) = ${x(i + 1)} " +
      //  s"temp=$temp den=${array(ip)(1)}")
    }
    x(width - 1) = rhs(width - 1) / array(width - 1)(1)
    for (i <- width - 2 to 0 by -1) {
      val a = array(i)
      if (a(1) < EPS)
        a(1) = EPS
      //println(s"rhs($i)=${rhs(i)} a2=${a(2)} x(${i+1}) = ${x(i + 1)}  den=${a(1)}")
      x(i) = (rhs(i) - a(2) * x(i + 1)) / a(1)
      //println(s"x($i) = ${x(i)}")
    }
  }

  def integrate(dt: Double): Unit = {
    val array = Array.fill[Double](width, 3)(0)
    val rhs, d, dist = Array.fill[Double](width)(0)
    val ct, start, stop = Array.fill[Int](MAX_CON)(0)
    val oldVolume, newVolume = Array.fill[Double](MAX_CON)(0)

    for (i <- 0 until width) {
      dist(i) = floor(i) - h1(i)
      // oldVolume = oldVolume.dist(i)
      if (dist(i) < 0)
        dist(i) = 0
      rhs(i) = h1(i) + (1.0 - viscosity) * (h1(i) - h0(i))
    }

    val c = 0.5 * GRAVITY * (dt * dt) / (DX * DX)
    //println("  --  c = "+ c)
    for (i <- 0 until width - 1) {
      d(i) = c * (dist(i) + dist(i + 1))
    }
    var a = array(0)
    a(0) = 1.0         // unused
    a(1) = 1.0 + d(0)  // left boundary
    a(2) = -d(0)
    for (i <- 1 until width - 1) {
      a = array(i)
      a(0) = -d(i - 1)
      a(1) = 1.0 + d(i - 1) + d(i)
      a(2) = -d(i)
    }
    a = array(width - 1)
    a(0) = -d(width - 2)    // right boundary
    a(1) = 1.0 + d(width - 2)
    a(2) = 1.0           // unused
    for (i <- 0 until width) {
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
    while (i < width) {
      // determine the connected regions and their volumes
      while (i < width && h1(i) >= floor(i)) {  // should not need i < width &&
        i += 1 // step over empty region
      }

      //if (h1(i)<=floor(i)) && ((i==0) or (h1(i-1)>floor(i-1))) then...
      //println("connect = " + connect + " i=" + i + " h1=" + h1(i) + " fl=" + floor(i))
      start(connect) = i  // valid region
      while (i < width && h1(i) < floor(i)) {   // shoui < width &&
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

    for (i <- 0 until width) {
      if (h1(i) > floor(i)) {
        h1(i) = floor(i) + EPS
        h0(i) = floor(i) + EPS
      }
    }
  }
}
