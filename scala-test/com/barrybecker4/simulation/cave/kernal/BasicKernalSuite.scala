// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.kernal

import com.barrybecker4.simulation.cave.model.Cave
import com.barrybecker4.simulation.cave.model.kernal.BasicKernel
import org.scalatest.funsuite.AnyFunSuite
import BasicKernalSuite._
import org.scalactic.{Equality, TolerantNumerics}

import scala.util.Random


/**
  * @author Barry Becker
  */
object BasicKernalSuite  {
  private val EPS = 0.0000000000001
  private val FLOOR = 0.1
  private val CEILING = 0.9
}

class BasicKernalSuite extends AnyFunSuite {

  /** instance under test */
  private var kernel: BasicKernel = _
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  /** assertEquals that we get correct neighbor counts for different positions within the 5x5 cave. */
  test("NeighborCount") {
    val RND = new Random()
    RND.setSeed(0)

    val cave = new Cave(5, 5, FLOOR, CEILING, RND)
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
    assertResult(expected) { act.mkString(", ")}
    //assert( kernel.countNeighbors(0, 0) === 0.7606486480925898) // 7.0
    //assert( kernel.countNeighbors(1, 0) === 0.7520196215146719) // 6.0
    //assert( kernel.countNeighbors(1, 1) === 0.5960481515908456) // 5.0
    //assert( kernel.countNeighbors(4, 3) === 0.796974165870401)
  }
}
