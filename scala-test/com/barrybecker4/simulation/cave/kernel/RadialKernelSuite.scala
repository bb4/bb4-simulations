// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.kernel

import com.barrybecker4.simulation.cave.model.Cave
import com.barrybecker4.simulation.cave.model.kernel.RadialKernel
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class RadialKernelSuite extends AnyFunSuite {

  private def cave5(): Cave = {
    val rnd = new Random(42L)
    val c = new Cave(5, 5, 0.1, 0.9, rnd)
    c.setValue(2, 2, 0.55)
    c
  }

  test("Radial5IsNormalizedRoughlyToNeighborAverage") {
    val cave = cave5()
    val ker = new RadialKernel(cave, 5)
    val c = ker.countNeighbors(2, 2)
    assert(c > 0 && c < 1)
  }

  test("RadialSizesDiffer") {
    val cave = cave5()
    val k3 = new RadialKernel(cave, 3)
    val k9 = new RadialKernel(cave, 9)
    val a = k3.countNeighbors(2, 2)
    val b = k9.countNeighbors(2, 2)
    assert(math.abs(a - b) > 1e-9)
  }

  test("CornerUsesOffEdgeWeighting") {
    val cave = new Cave(4, 4, 0.2, 0.8, new Random(0L))
    val ker = new RadialKernel(cave, 3)
    val v = ker.countNeighbors(0, 0)
    assert(v >= 0 && v <= 1)
  }
}
