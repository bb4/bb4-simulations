// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

/**
  * Double-buffered Stam fluid fields as flat arrays for better cache locality.
  * Interior fluid cells are indices 1..dimX and 1..dimY; 0 and dim+1 are boundaries.
  */
class Grid(val dimX: Int, val dimY: Int) {

  private[model] val rowStride: Int = dimX + 2
  private val fieldLen: Int = rowStride * (dimY + 2)

  private[model] var u0: Array[Double] = new Array[Double](fieldLen)
  private[model] var u1: Array[Double] = new Array[Double](fieldLen)
  private[model] var v0: Array[Double] = new Array[Double](fieldLen)
  private[model] var v1: Array[Double] = new Array[Double](fieldLen)
  private[model] var density0: Array[Double] = new Array[Double](fieldLen)
  private[model] var density1: Array[Double] = new Array[Double](fieldLen)

  addInitialInkDensity()

  def getWidth: Int = dimX
  def getHeight: Int = dimY

  private[model] def idx(i: Int, j: Int): Int = ix(i, j, rowStride)

  def getU(i: Int, j: Int): Double = u1(idx(i, j))
  def getV(i: Int, j: Int): Double = v1(idx(i, j))
  def getDensity(i: Int, j: Int): Double = density1(idx(i, j))

  /** Apply sources to the current (grid1) fields — Stam add_source target before swap. */
  def incrementU(i: Int, j: Int, value: Double): Unit = { u1(idx(i, j)) += value }
  def incrementV(i: Int, j: Int, value: Double): Unit = { v1(idx(i, j)) += value }
  def incrementDensity(i: Int, j: Int, value: Double): Unit = { density1(idx(i, j)) += value }

  def swap(prop: CellProperty): Unit = {
    prop match {
      case CellProperty.U =>
        val t = u0; u0 = u1; u1 = t
      case CellProperty.V =>
        val t = v0; v0 = v1; v1 = t
      case CellProperty.DENSITY =>
        val t = density0; density0 = density1; density1 = t
    }
  }

  private[model] def property0(prop: CellProperty): Array[Double] = prop match {
    case CellProperty.U => u0
    case CellProperty.V => v0
    case CellProperty.DENSITY => density0
  }

  private[model] def property1(prop: CellProperty): Array[Double] = prop match {
    case CellProperty.U => u1
    case CellProperty.V => v1
    case CellProperty.DENSITY => density1
  }

  /** Zero all fields (for tests and clean restarts). */
  private[model] def clear(): Unit = {
    java.util.Arrays.fill(u0, 0.0)
    java.util.Arrays.fill(u1, 0.0)
    java.util.Arrays.fill(v0, 0.0)
    java.util.Arrays.fill(v1, 0.0)
    java.util.Arrays.fill(density0, 0.0)
    java.util.Arrays.fill(density1, 0.0)
  }

  /** Set a boundary to contain the liquid. */
  def setBoundary(boundary: Boundary, x: Array[Double]): Unit = {
    var i = 1
    while (i <= dimX) {
      x(idx(i, 0)) =
        if boundary == Boundary.HORIZONTAL then -x(idx(i, 1)) else x(idx(i, 1))
      x(idx(i, dimY + 1)) =
        if boundary == Boundary.HORIZONTAL then -x(idx(i, dimY)) else x(idx(i, dimY))
      i += 1
    }
    i = 1
    while (i <= dimY) {
      x(idx(0, i)) =
        if boundary == Boundary.VERTICAL then -x(idx(1, i)) else x(idx(1, i))
      x(idx(dimX + 1, i)) =
        if boundary == Boundary.VERTICAL then -x(idx(dimX, i)) else x(idx(dimX, i))
      i += 1
    }
    x(idx(0, 0)) = 0.5 * (x(idx(1, 0)) + x(idx(0, 1)))
    x(idx(0, dimY + 1)) = 0.5 * (x(idx(1, dimY + 1)) + x(idx(0, dimY)))
    x(idx(dimX + 1, 0)) = 0.5 * (x(idx(dimX, 0)) + x(idx(dimX + 1, 1)))
    x(idx(dimX + 1, dimY + 1)) = 0.5 * (x(idx(dimX, dimY + 1)) + x(idx(dimX + 1, dimY)))
  }

  private def addInitialInkDensity(): Unit = {
    var i = 2
    while (i < dimX / 2) {
      var j = 2
      while (j < dimY / 2) {
        val p = idx(i, j)
        u1(p) = 0.01 + (Math.cos(0.4 * i) + Math.sin(0.3 * j)) / 10.0
        v1(p) = 0.1 - (Math.sin(0.2 * i) + Math.sin(0.1 * j) / 10.0)
        density1(p) = 0.1 - Math.sin((i + j) / 4.0) / 10.0
        j += 1
      }
      i += 1
    }
  }
}
