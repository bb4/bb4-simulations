// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import com.barrybecker4.simulation.common.{Profiler, RectangularModel}


object FluidEnvironment {
  /**
    * Light diffusion / viscosity. Both enter Stam's solve as a = dt·param·W·H, so on
    * window-sized grids the useful range is roughly 0–0.01 (former defaults of 0 / 10
    * were tuned around the old under-diffusing hybrid solver).
    */
  val DEFAULT_DIFFUSION_RATE = 0.0
  val DEFAULT_VISCOSITY = 0.001
  val DEFAULT_NUM_SOLVER_ITERATIONS = 20
}

/**
  * Global fluid space: M×N Stam stable-fluids grid.
  * X increases right; Y increases downward (Java graphics).
  * @author Jos Stam, ported to scala by Barry Becker and enhanced.
  */
class FluidEnvironment(initialWidth: Int, initialHeight: Int) extends RectangularModel {

  /** Exposed for model-package tests. */
  private[model] var grid = new Grid(initialWidth, initialHeight)

  private var diffusionRate: Double = FluidEnvironment.DEFAULT_DIFFUSION_RATE
  private var viscosity: Double = FluidEnvironment.DEFAULT_VISCOSITY
  private var numSolverIterations = FluidEnvironment.DEFAULT_NUM_SOLVER_ITERATIONS

  def setSize(width: Int, height: Int): Unit = {
    if (width != grid.getWidth || height != grid.getHeight) {
      grid = new Grid(width, height)
    }
  }

  def reset(): Unit = { grid = new Grid(grid.getWidth, grid.getHeight) }
  def getWidth: Int = grid.getWidth
  def getHeight: Int = grid.getHeight
  def getCurrentRow: Int = getHeight
  def getLastRow: Int = 0

  /** Render-facing sample: map 0-based image coords onto interior cells 1..N. */
  def getValue(i: Int, j: Int): Double = grid.getDensity(i + 1, j + 1)

  def setDiffusionRate(rate: Double): Unit = { diffusionRate = rate }
  def setViscosity(v: Double): Unit = { viscosity = v }
  def setNumSolverIterations(numIterations: Int): Unit = { numSolverIterations = numIterations }

  def getU(i: Int, j: Int): Double = grid.getU(i, j)
  def getV(i: Int, j: Int): Double = grid.getV(i, j)
  def getDensity(i: Int, j: Int): Double = grid.getDensity(i, j)
  def incrementU(i: Int, j: Int, fu: Double): Unit = grid.incrementU(i, j, fu)
  def incrementV(i: Int, j: Int, fv: Double): Unit = grid.incrementV(i, j, fv)
  def incrementDensity(i: Int, j: Int, densityInc: Double): Unit = grid.incrementDensity(i, j, densityInc)

  /**
    * Advance a timeStep
    * @return the new timeStep (does not change in this case)
    */
  def stepForward(timeStep: Double): Double = {
    Profiler.getInstance.startCalculationTime()
    velocityStep(viscosity, timeStep)
    densityStep(CellProperty.DENSITY, timeStep)
    Profiler.getInstance.stopCalculationTime()
    timeStep
  }

  private def velocityStep(visc: Double, dt: Double): Unit = {
    grid.swap(CellProperty.U)
    diffuse(Boundary.VERTICAL, CellProperty.U, visc, dt)
    grid.swap(CellProperty.V)
    diffuse(Boundary.HORIZONTAL, CellProperty.V, visc, dt)
    project(grid.u1, grid.v1, grid.u0, grid.v0)
    grid.swap(CellProperty.U)
    grid.swap(CellProperty.V)
    advect(Boundary.VERTICAL, CellProperty.U, grid.u0, grid.v0, dt)
    advect(Boundary.HORIZONTAL, CellProperty.V, grid.u0, grid.v0, dt)
    project(grid.u1, grid.v1, grid.u0, grid.v0)
  }

  private def densityStep(prop: CellProperty, dt: Double): Unit = {
    grid.swap(prop)
    diffuse(Boundary.NEITHER, prop, diffusionRate, dt)
    grid.swap(prop)
    advect(Boundary.NEITHER, prop, grid.u1, grid.v1, dt)
  }

  /** Make the velocity field divergence-free (rectangular cell spacing). */
  private def project(u: Array[Double], v: Array[Double], p: Array[Double], div: Array[Double]): Unit = {
    val width = grid.getWidth
    val height = grid.getHeight
    val hx = 1.0 / width
    val hy = 1.0 / height
    var i = 1
    while (i <= width) {
      var j = 1
      while (j <= height) {
        val ij = grid.idx(i, j)
        div(ij) = -0.5 * (hx * (u(grid.idx(i + 1, j)) - u(grid.idx(i - 1, j)))
          + hy * (v(grid.idx(i, j + 1)) - v(grid.idx(i, j - 1))))
        p(ij) = 0
        j += 1
      }
      i += 1
    }

    grid.setBoundary(Boundary.NEITHER, div)
    grid.setBoundary(Boundary.NEITHER, p)
    linearSolve(Boundary.NEITHER, p, div, 1, 4)
    i = 1
    while (i <= width) {
      var j = 1
      while (j <= height) {
        val ij = grid.idx(i, j)
        u(ij) -= 0.5 * width * (p(grid.idx(i + 1, j)) - p(grid.idx(i - 1, j)))
        v(ij) -= 0.5 * height * (p(grid.idx(i, j + 1)) - p(grid.idx(i, j - 1)))
        j += 1
      }
      i += 1
    }
    grid.setBoundary(Boundary.VERTICAL, u)
    grid.setBoundary(Boundary.HORIZONTAL, v)
  }

  private def diffuse(boundary: Boundary, prop: CellProperty, diff: Double, dt: Double): Unit = {
    val a = dt * diff * grid.getWidth * grid.getHeight
    linearSolve(boundary, grid.property1(prop), grid.property0(prop), a, 1 + 4 * a)
  }

  /** Advect the field; rectangular grids use separate dtx / dty. */
  private def advect(bound: Boundary, prop: CellProperty, u: Array[Double], v: Array[Double], dt: Double): Unit = {
    val width = grid.getWidth
    val height = grid.getHeight
    val d0 = grid.property0(prop)
    val d1 = grid.property1(prop)
    val dtx = dt * width
    val dty = dt * height
    var i = 1
    while (i <= width) {
      var j = 1
      while (j <= height) {
        val ij = grid.idx(i, j)
        var x = i - dtx * u(ij)
        var y = j - dty * v(ij)
        if (x < 0.5) x = 0.5
        if (x > width + 0.5) x = width + 0.5
        val i0 = x.toInt
        val i1 = i0 + 1
        if (y < 0.5) y = 0.5
        if (y > height + 0.5) y = height + 0.5
        val j0 = y.toInt
        val j1 = j0 + 1
        val s1 = x - i0
        val s0 = 1.0 - s1
        val t1 = y - j0
        val t0 = 1.0 - t1
        d1(ij) = s0 * (t0 * d0(grid.idx(i0, j0)) + t1 * d0(grid.idx(i0, j1))) +
          s1 * (t0 * d0(grid.idx(i1, j0)) + t1 * d0(grid.idx(i1, j1)))
        j += 1
      }
      i += 1
    }
    grid.setBoundary(bound, d1)
  }

  /** Gauss–Seidel diffusion / pressure solve (Stam: all four neighbors from x). */
  private def linearSolve(bound: Boundary, x: Array[Double], x0: Array[Double], a: Double, c: Double): Unit = {
    val width = grid.getWidth
    val height = grid.getHeight
    var k = 0
    while (k < numSolverIterations) {
      var i = 1
      while (i <= width) {
        var j = 1
        while (j <= height) {
          val ij = grid.idx(i, j)
          x(ij) = (x0(ij) + a * (x(grid.idx(i - 1, j)) + x(grid.idx(i + 1, j))
            + x(grid.idx(i, j - 1)) + x(grid.idx(i, j + 1)))) / c
          j += 1
        }
        i += 1
      }
      grid.setBoundary(bound, x)
      k += 1
    }
  }
}
