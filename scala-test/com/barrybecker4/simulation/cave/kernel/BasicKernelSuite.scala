// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.kernel

import com.barrybecker4.simulation.cave.model.Cave
import com.barrybecker4.simulation.cave.model.kernel.BasicKernel
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite
import BasicKernelSuite.*

import scala.compiletime.uninitialized
import scala.util.Random

/**
  * @author Barry Becker
  */
object BasicKernelSuite {
  private val EPS = 0.0000000000001
  private val FLOOR = 0.1
  private val CEILING = 0.9
}

class BasicKernelSuite extends AnyFunSuite {

  private var kernel: BasicKernel = uninitialized
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("NeighborCount") {
    val rnd = new Random()
    rnd.setSeed(0)

    val cave = new Cave(5, 5, FLOOR, CEILING, rnd)
    cave.setValue(0, 1, 0.2)
    cave.setValue(1, 0, 0.5)
    cave.setValue(2, 0, 0.9)
    cave.setValue(2, 1, 0.8)
    cave.setValue(2, 2, 0.1)
    cave.setValue(2, 3, 0.7)
    cave.setValue(3, 1, 0.6)
    kernel = new BasicKernel(cave)

    val expected = "0.7606486480925898, 0.7520196215146719, 0.5960481515908456, 0.796974165870401"
    val act = Array(
      kernel.countNeighbors(0, 0),
      kernel.countNeighbors(1, 0),
      kernel.countNeighbors(1, 1),
      kernel.countNeighbors(4, 3)
    )
    assertResult(expected)(act.mkString(", "))
  }
}
