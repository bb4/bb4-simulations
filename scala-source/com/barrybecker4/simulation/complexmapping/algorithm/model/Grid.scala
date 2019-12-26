/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import com.barrybecker4.simulation.complexmapping.algorithm.model.Grid.createMesh


object Grid {

  // this should be a function passed in
  private def computeValue(x: Double, y: Double): Double = {
    val base = Math.sqrt(x * x + y * y) / 4.0
    base - base.toInt
  }

  def createMesh(bounds: Box, incX: Double = 0.1, incY: Double = 0.1): Array[Array[MeshPoint]] = {
    val xdim: Int = (bounds.width / incX).toInt
    val ydim: Int = (bounds.height / incY).toInt
    val mesh: Array[Array[MeshPoint]] = Array.ofDim[MeshPoint](xdim, ydim)

    for (i <- 0 until xdim) {
      for (j <- 0 until ydim) {
        val x = bounds.leftX + i * incX
        val y = bounds.bottomY + j * incY
        mesh(i)(j) = new MeshPoint(x, y, computeValue(x, y))
      }
    }
    mesh
  }
}

case class Grid(mesh: Array[Array[MeshPoint]]) {

  def this(bounds: Box, incX: Double = 0.1, incY: Double = 0.1) {
    this(createMesh(bounds, incX, incY))
  }

  def width: Int = mesh.length
  def height: Int = mesh(0).length
  def apply(i: Int, j: Int): MeshPoint = mesh(i)(j)

  def transform(func: ComplexFunction, n: Int, interpolationVal: Double = 1.0): Grid = {
    if (interpolationVal <= 0) this
    else if (interpolationVal >= 1.0) doTransform(mpt => mpt.transform(func, n))
    else doTransform(mpt => mpt + (mpt.transform(func, n) - mpt).scale(interpolationVal))
  }

  private def doTransform(tfunc: MeshPoint => MeshPoint): Grid = {
    val transformedMesh: Array[Array[MeshPoint]] = Array.ofDim[MeshPoint](width, height)
    for (i <- 0 until width)
      for (j <- 0 until height)
        transformedMesh(i)(j) = tfunc(mesh(i)(j))
    Grid(transformedMesh)
  }
}
