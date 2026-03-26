// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.model

import Environment._
import scala.math.{cos, sin}


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
  private var viscosity: Double = 0.0
  private val triSolver = TriDiagonalMatrixSolver(EPS)
  private var paused = false

  /** Number of pixels in x direction for every discrete bin */
  var xStep: Int = 1
  reset()

  def reset(): Unit = initBoundary()
  def setViscosity(v: Double): Unit = { viscosity = v }
  def pause(): Unit = { paused = true }
  def resume(): Unit = { paused = false }

  private def initBoundary(): Unit = {
    val yrat = height / 400
    for (i <- 0 until width) {
      val ht = yrat * (50.0 + 20.0 * sin(0.018 * i) + i / 5.0)
      h0(i) = ht
      h1(i) = ht
      floor(i) = yrat * (180.0 + 42.0 * cos(0.01 * i) + i / 8.0)
    }
  }

  def integrate(dt: Double): Unit = {
    if (paused) return
    val array = Array.fill[Double](width, 3)(0)
    val rhs, d, dist = Array.fill[Double](width)(0)

    computeDistAndRhs(rhs, dist)
    val c = 0.5 * GRAVITY * (dt * dt) / (DX * DX)
    fillDFromDist(c, dist, d)
    fillTridiagonalCoefficients(d, array)
    copyH1ToH0()
    triSolver.solve(array, rhs, h1)
    conserveVolume()
  }

  private def computeDistAndRhs(rhs: Array[Double], dist: Array[Double]): Unit = {
    for (i <- 0 until width) {
      dist(i) = floor(i) - h1(i)
      if (dist(i) < 0) dist(i) = 0
      rhs(i) = h1(i) + (1.0 - viscosity) * (h1(i) - h0(i))
    }
  }

  private def fillDFromDist(c: Double, dist: Array[Double], d: Array[Double]): Unit = {
    for (i <- 0 until width - 1)
      d(i) = c * (dist(i) + dist(i + 1))
  }

  private def fillTridiagonalCoefficients(d: Array[Double], array: Array[Array[Double]]): Unit = {
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
    a(0) = -d(width - 2)   // right boundary
    a(1) = 1.0 + d(width - 2)
    a(2) = 1.0            // unused
  }

  private def copyH1ToH0(): Unit = {
    for (i <- 0 until width)
      h0(i) = h1(i)
  }

  /** Exposed for tests in `com.barrybecker4.simulation.water` (same semantics as internal conservation pass). */
  private[water] def runConservationStep(): Unit = conserveVolume()

  /** conserve volume over the connected regions  */
  private def conserveVolume(): Unit = {
    val oldVolume, newVolume = Array.fill[Double](MAX_CON)(0)
    val ct, start, stop = Array.fill[Int](MAX_CON)(0)

    val connect = scanConnectedRegions(oldVolume, newVolume, ct, start, stop)
    applyVolumeAdjustments(connect, oldVolume, newVolume, ct, start, stop)
    clampWaterAboveFloor()
  }

  private def scanConnectedRegions(
      oldVolume: Array[Double],
      newVolume: Array[Double],
      ct: Array[Int],
      start: Array[Int],
      stop: Array[Int]
  ): Int = {
    var i = 0
    var connect = 0
    while (i < width) {
      while (i < width && h1(i) >= floor(i))
        i += 1

      start(connect) = i
      while (i < width && h1(i) < floor(i)) {
        oldVolume(connect) = oldVolume(connect) + floor(i) - h0(i)
        newVolume(connect) = newVolume(connect) + floor(i) - h1(i)
        ct(connect) += 1
        i += 1
      }
      stop(connect) = i - 1
      connect += 1
    }
    connect
  }

  private def applyVolumeAdjustments(
      connect: Int,
      oldVolume: Array[Double],
      newVolume: Array[Double],
      ct: Array[Int],
      start: Array[Int],
      stop: Array[Int]
  ): Unit = {
    for (j <- 0 until connect if ct(j) > 0) {
      val adjust = (newVolume(j) - oldVolume(j)) / ct(j)
      for (i <- start(j) to stop(j))
        h1(i) += adjust
    }
  }

  private def clampWaterAboveFloor(): Unit = {
    for (i <- 0 until width) {
      if (h1(i) > floor(i)) {
        h1(i) = floor(i) + EPS
        h0(i) = floor(i) + EPS
      }
    }
  }
}
