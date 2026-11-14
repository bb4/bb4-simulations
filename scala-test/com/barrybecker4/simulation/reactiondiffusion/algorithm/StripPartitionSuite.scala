/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

class StripPartitionSuite extends AnyFunSuite {

  test("ranges cover [0, length) without gaps or overlap") {
    val parts = StripPartition.ranges(24, 5)
    assert(parts.length == 5)
    assert(parts.head._1 == 0)
    assert(parts.last._2 == 24)
    for (i <- 0 until parts.length - 1)
      assert(parts(i)._2 == parts(i + 1)._1)
  }

  test("single strip covers entire length") {
    assert(StripPartition.ranges(10, 1).sameElements(Array((0, 10))))
  }

  test("more strips than length yields one cell per strip") {
    val parts = StripPartition.ranges(3, 8)
    assert(parts.length == 3)
    assert(parts.sameElements(Array((0, 1), (1, 2), (2, 3))))
  }

  test("empty length yields empty ranges") {
    assert(StripPartition.ranges(0, 4).isEmpty)
  }
}
