// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.model

import Environment._


object Environment {
  private val GRAVITY = 9.8
  private val DX = 0.02
  private val EPS = 0.000000123

  /** Upper bound on wet runs for width columns (alternating + trailing empty slot). */
  private def maxRegions(width: Int): Int = (width / 2) + 2
}

/**
  * Dimensions of the environment
  * @author Barry Becker
  */
class Environment(
    val width: Int,
    val height: Int,
    private var initialEnvironment: InitialEnvironment = InitialEnvironment.SlopingBeach) {

  val h0, h1, floor = Array.ofDim[Double](width)
  private var viscosity: Double = 0.0
  private val triSolver = TriDiagonalMatrixSolver(EPS)
  private var paused = false

  // Reused integrate workspaces (avoid per-step allocation)
  private val triArray = Array.fill[Double](width, 3)(0)
  private val rhs = Array.ofDim[Double](width)
  private val d = Array.ofDim[Double](width)
  private val dist = Array.ofDim[Double](width)

  // Reused conservation region buffers
  private val regionCap = maxRegions(width)
  private val oldVolume = Array.ofDim[Double](regionCap)
  private val newVolume = Array.ofDim[Double](regionCap)
  private val ct = Array.ofDim[Int](regionCap)
  private val start = Array.ofDim[Int](regionCap)
  private val stop = Array.ofDim[Int](regionCap)

  /** Number of pixels in x direction for every discrete bin */
  var xStep: Int = 1
  reset()

  def getInitialEnvironment: InitialEnvironment = initialEnvironment

  def setInitialEnvironment(ie: InitialEnvironment): Unit = { initialEnvironment = ie }

  def reset(): Unit =
    initialEnvironment.fill(h0, h1, floor, width, height)

  def setViscosity(v: Double): Unit = { viscosity = v }
  def pause(): Unit = { paused = true }
  def resume(): Unit = { paused = false }

  def adjustFloor(i: Int, delta: Double): Unit = { floor(i) += delta }
  def adjustWaterSurface(i: Int, delta: Double): Unit = { h1(i) += delta }

  def integrate(dt: Double): Unit = {
    if (paused) return

    computeDistAndRhs()
    val c = 0.5 * GRAVITY * (dt * dt) / (DX * DX)
    fillDFromDist(c)
    fillTridiagonalCoefficients()
    copyH1ToH0()
    triSolver.solve(triArray, rhs, h1)
    conserveVolume()
  }

  private def computeDistAndRhs(): Unit = {
    for (i <- 0 until width) {
      dist(i) = floor(i) - h1(i)
      if (dist(i) < 0) dist(i) = 0
      rhs(i) = h1(i) + (1.0 - viscosity) * (h1(i) - h0(i))
    }
  }

  private def fillDFromDist(c: Double): Unit = {
    for (i <- 0 until width - 1)
      d(i) = c * (dist(i) + dist(i + 1))
  }

  private def fillTridiagonalCoefficients(): Unit = {
    var a = triArray(0)
    a(0) = 1.0         // unused
    a(1) = 1.0 + d(0)  // left boundary
    a(2) = -d(0)
    for (i <- 1 until width - 1) {
      a = triArray(i)
      a(0) = -d(i - 1)
      a(1) = 1.0 + d(i - 1) + d(i)
      a(2) = -d(i)
    }
    a = triArray(width - 1)
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
    java.util.Arrays.fill(oldVolume, 0.0)
    java.util.Arrays.fill(newVolume, 0.0)
    java.util.Arrays.fill(ct, 0)
    java.util.Arrays.fill(start, 0)
    java.util.Arrays.fill(stop, 0)

    val connect = scanConnectedRegions()
    applyVolumeAdjustments(connect)
    clampWaterAboveFloor()
  }

  private def scanConnectedRegions(): Int = {
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

  private def applyVolumeAdjustments(connect: Int): Unit = {
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
