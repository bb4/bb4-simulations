/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import com.barrybecker4.simulation.complexmapping.algorithm.model.Grid.createMesh


object Grid {
  private def computeQuadrantValue(x: Double, y: Double): Double = {
      if (x * y < 0) {
        if (x > 0) 0.33 else 0.66
      } else {
        if (x > 0) 0.01 else 0.99
      }
  }

  def createMesh(bounds: Box, incX: Double = 0.1, incY: Double = 0.1): Array[Array[MeshPoint]] = {
    val xdim: Int = (bounds.width / incX).toInt
    val ydim: Int = (bounds.height / incY).toInt
    if (xdim <= 0 || ydim <= 0) Array.empty[Array[MeshPoint]]
    else Array.tabulate(xdim, ydim) { (i, j) =>
      val x = bounds.leftX + i * incX
      val y = bounds.bottomY + j * incY
      new MeshPoint(x, y, computeQuadrantValue(x, y))
    }
  }
}

case class Grid(mesh: Array[Array[MeshPoint]]) {

  def this(bounds: Box, incX: Double = 0.1, incY: Double = 0.1) = {
    this(createMesh(bounds, incX, incY))
  }

  def width: Int = mesh.length
  def height: Int = if (mesh.isEmpty) 0 else mesh(0).length
  def apply(i: Int, j: Int): MeshPoint = mesh(i)(j)

  def transform(func: ComplexFunction, n: Int, interpolationVal: Double = 1.0): Grid = {
    if (interpolationVal <= 0) this
    else if (interpolationVal >= 1.0) doTransform(mpt => mpt.transform(func, n))
    else doTransform { mpt =>
      val mapped = mpt.transform(func, n)
      mpt + (mapped - mpt).scale(interpolationVal)
    }
  }

  private def doTransform(tfunc: MeshPoint => MeshPoint): Grid = {
    val transformedMesh: Array[Array[MeshPoint]] = Array.ofDim[MeshPoint](width, height)
    for (i <- 0 until width)
      for (j <- 0 until height)
        transformedMesh(i)(j) = tfunc(mesh(i)(j))
    Grid(transformedMesh)
  }
}
