// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import com.barrybecker4.simulation.common.{Profiler, RectangularModel}
import com.barrybecker4.simulation.fluid.model.CellProperty.CellProperty
import com.barrybecker4.simulation.fluid.model.Boundary.Boundary


object FluidEnvironment {
  val DEFAULT_DIFFUSION_RATE = 0.0f
  val DEFAULT_VISCOSITY = 10.0f
  val DEFAULT_NUM_SOLVER_ITERATIONS = 20
}

/**
  * Represents the global space containing all the cells, walls, and fluid.
  * Assumes an M * N grid of cells. X axis increases to the left.
  * Y axis increases downwards to be consistent with java graphics.
  * @param dimX initial grid width
  * @param dimY initial grid height
  * @author Jos Stam, ported to scala by Barry Becker and enhanced.
  */
class FluidEnvironment(val dimX: Int, val dimY: Int) extends RectangularModel {

  private var grid = new Grid(dimX, dimY)

  /** the cells that the liquid will flow through. Contains two CellGrids for 2 steps - current and last. */
  private var diffusionRate: Double = FluidEnvironment.DEFAULT_DIFFUSION_RATE
  private var viscosity: Double = FluidEnvironment.DEFAULT_VISCOSITY
  private var numSolverIterations = FluidEnvironment.DEFAULT_NUM_SOLVER_ITERATIONS

  def setSize(width: Int, height: Int) {
    if (width != grid.getWidth || height != grid.getHeight) {
      grid = new Grid(width, height)
    }
  }

  /** reset to original state */
  def reset() { grid = new Grid(grid.getWidth, grid.getHeight) }
  def getWidth: Int = grid.getWidth
  def getHeight: Int = grid.getHeight
  def getCurrentRow: Int = getHeight
  def getLastRow: Int = 0
  def getValue(i: Int, j: Int): Double = grid.getValue(i, j)
  def setDiffusionRate(rate: Double) { diffusionRate = rate }
  def setViscosity(v: Double) { viscosity = v }
  def setNumSolverIterations(numIterations: Int) { numSolverIterations = numIterations }
  def getU(i: Int, j: Int): Double = grid.getU(i, j)
  def getV(i: Int, j: Int): Double = grid.getV(i, j)
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
    densityStep(CellProperty.DENSITY, grid.getGrid1.u, grid.getGrid1.v, timeStep)
    Profiler.getInstance.stopCalculationTime()
    timeStep
  }

  private def velocityStep(visc: Double, dt: Double): Unit = {
    val g0 = grid.getGrid0
    val g1 = grid.getGrid1
    grid.swap(CellProperty.U)
    diffuse(Boundary.VERTICAL, CellProperty.U, visc, dt)
    grid.swap(CellProperty.V)
    diffuse(Boundary.HORIZONTAL, CellProperty.V, visc, dt)
    project(g1.u, g1.v, g0.u, g0.v)
    grid.swap(CellProperty.U)
    grid.swap(CellProperty.V)
    advect(Boundary.VERTICAL, CellProperty.U, g0.u, g0.v, dt)
    advect(Boundary.HORIZONTAL, CellProperty.V, g0.u, g0.v, dt)
    project(g1.u, g1.v, g0.u, g0.v)
  }

  private def densityStep(prop: CellProperty, u: TwoDArray, v: TwoDArray, dt: Double): Unit = {
    grid.swap(prop)
    diffuse(Boundary.NEITHER, prop, diffusionRate, dt)
    grid.swap(prop)
    advect(Boundary.NEITHER, prop, u, v, dt)
  }

  /** project the fluid */
  private def project(u: TwoDArray, v: TwoDArray, p: TwoDArray, div: TwoDArray): Unit = {
    val width = grid.getWidth
    val height = grid.getHeight
    for (i <- 1 to width)
      for (j <- 1 to height) {
        div(i)(j) = -(u(i + 1)(j) - u(i - 1)(j) + v(i)(j + 1) - v(i)(j - 1)) / (width + height)
        p(i)(j) = 0
      }

    grid.setBoundary(Boundary.NEITHER, div)
    grid.setBoundary(Boundary.NEITHER, p)
    linearSolve(Boundary.NEITHER, p, div, 1, 4)
    for (i <- 1 to width) {
      for (j <- 1 to height) {
        u(i)(j) -= 0.5f * width * (p(i + 1)(j) - p(i - 1)(j))
        v(i)(j) -= 0.5f * height * (p(i)(j + 1) - p(i)(j - 1))
      }
    }
    grid.setBoundary(Boundary.VERTICAL, u)
    grid.setBoundary(Boundary.HORIZONTAL, v)
  }

  /** Diffuse the pressure.
    * @param prop the cell property to diffuse
    * @param diff either diffusion rate or viscosity.
    */
  private def diffuse(boundary: Boundary, prop: CellProperty, diff: Double, dt: Double): Unit = {
    val a = dt * diff * grid.getWidth * grid.getHeight
    linearSolve(boundary, grid.getGrid1.getProperty(prop), grid.getGrid0.getProperty(prop), a, 1 + 4 * a)
  }

  /** Advect the fluid in the field.  */
  private def advect(bound: Boundary, prop: CellProperty, u: TwoDArray, v: TwoDArray, dt: Double): Unit = {
    val width = grid.getWidth
    val height = grid.getHeight
    val d0 = grid.getGrid0.getProperty(prop)
    val d1 = grid.getGrid1.getProperty(prop)
    val dt0 = dt * width
    for (i <- 1 to width) {
      for (j <- 1 to height) {
        var x = i - dt0 * u(i)(j)
        var y = j - dt0 * v(i)(j)
        if (x < 0.5f) x = 0.5f
        if (x > width + 0.5f) x = width + 0.5f
        val i0 = x.toInt
        val i1 = i0 + 1
        if (y < 0.5f) y = 0.5f
        if (y > height + 0.5f) y = height + 0.5f
        val j0 = y.toInt
        val j1 = j0 + 1
        val s1 = x - i0
        val s0 = 1 - s1
        val t1 = y - j0
        val t0 = 1 - t1
        d1(i)(j) = s0 * (t0 * d0(i0)(j0) + t1 * d0(i0)(j1)) + s1 * (t0 * d0(i1)(j0) + t1 * d0(i1)(j1))
      }
    }
    grid.setBoundary(bound, d1)
  }

  /** Solve the system */
  private def linearSolve(bound: Boundary, x: TwoDArray, x0: TwoDArray, a: Double, c: Double): Unit = {
    for (k <- 0 until numSolverIterations) {
      for (i <- 1 to grid.getWidth)
        for (j <- 1 to grid.getHeight)
          x(i)(j) = (x0(i)(j) + a * (x(i - 1)(j) + x0(i + 1)(j) + x(i)(j - 1) + x(i)(j + 1))) / c
      grid.setBoundary(bound, x)
    }
  }
}
