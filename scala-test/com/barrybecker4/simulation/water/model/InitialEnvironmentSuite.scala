package com.barrybecker4.simulation.water.model

import org.scalatest.funsuite.AnyFunSuite

class InitialEnvironmentSuite extends AnyFunSuite {

  private def filled(ie: InitialEnvironment, width: Int = 64, height: Int = 200)
      : (Array[Double], Array[Double], Array[Double]) = {
    val h0 = Array.ofDim[Double](width)
    val h1 = Array.ofDim[Double](width)
    val floor = Array.ofDim[Double](width)
    ie.fill(h0, h1, floor, width, height)
    (h0, h1, floor)
  }

  test("all presets fill finite values with water not above floor") {
    for (ie <- InitialEnvironment.values) {
      val (h0, h1, floor) = filled(ie)
      for (i <- h1.indices) {
        assert(h0(i).isFinite, s"$ie h0 at $i")
        assert(h1(i).isFinite, s"$ie h1 at $i")
        assert(floor(i).isFinite, s"$ie floor at $i")
        assert(h1(i) <= floor(i) + 1e-9, s"$ie water above floor at $i")
        assert(math.abs(h0(i) - h1(i)) < 1e-12, s"$ie h0 != h1 at $i")
      }
    }
  }

  test("SlopingBeach scales linearly with height") {
    val h = 200
    val (_, h1a, floorA) = filled(InitialEnvironment.SlopingBeach, 64, h)
    val (_, h1b, floorB) = filled(InitialEnvironment.SlopingBeach, 64, 2 * h)
    for (i <- h1a.indices) {
      assert(math.abs(h1b(i) - 2.0 * h1a(i)) < 1e-9, s"h1 at $i")
      assert(math.abs(floorB(i) - 2.0 * floorA(i)) < 1e-9, s"floor at $i")
    }
  }

  test("FlatPool has constant floor and water") {
    val (_, h1, floor) = filled(InitialEnvironment.FlatPool)
    assert(floor.forall(_ == floor(0)))
    assert(h1.forall(_ == h1(0)))
    assert(h1(0) < floor(0))
  }

  test("Basin is deeper in the middle than at the edges") {
    val width = 65
    val (_, h1, floor) = filled(InitialEnvironment.Basin, width)
    val mid = width / 2
    assert(floor(mid) > floor(0), "mid floor should be deeper (larger y) than left edge")
    assert(floor(mid) > floor(width - 1), "mid floor should be deeper than right edge")
    assert(h1(mid) < floor(mid))
    assert(h1(0) < floor(0) || math.abs(h1(0) - floor(0)) < 1e-6)
  }

  test("SteppedTerraces has discrete plateaus") {
    val width = 64
    val (_, h1, floor) = filled(InitialEnvironment.SteppedTerraces, width)
    val uniqueFloors = floor.distinct.sorted
    assert(uniqueFloors.length == 4, s"expected 4 terrace levels, got ${uniqueFloors.length}")
    // Within each plateau, floor is constant
    val stepW = width / 4
    for (step <- 0 until 4) {
      val start = step * stepW
      val end = if (step == 3) width else (step + 1) * stepW
      val level = floor(start)
      for (i <- start until end)
        assert(math.abs(floor(i) - level) < 1e-12, s"terrace $step at $i")
      assert(h1(start) < floor(start))
    }
    assert(uniqueFloors(0) < uniqueFloors(3), "terraces should deepen left to right")
  }

  test("display names are unique and non-empty") {
    val names = InitialEnvironment.values.map(_.displayName)
    assert(names.forall(_.nonEmpty))
    assert(names.distinct.length == names.length)
  }
}
