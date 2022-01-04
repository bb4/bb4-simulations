package com.barrybecker4.simulation.common.util

import org.scalatest.funsuite.AnyFunSuite
import com.barrybecker4.simulation.common.util.SkewedRandom

import scala.util.Random

class SkewedRandomSuite extends AnyFunSuite {

  private val rnd: Random = Random(0)
  private val skewedRandom = SkewedRandom(rnd)

  test("Typical gaussian") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 1, 0)
    assertResult("0.69, 0.28, 0.88, 0.68, 0.72, 0.15, 0.49, 0.52, 0.4, 0.34, 0.51, 0.62, 0.3, 0.56, 0.38, 0.8, 0.56, 0.49, 0.71, 0.7") {
      values.map(round).mkString(", ")
    }
  }

  test("High range gaussian") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(10, 20, 1, 0)
    assertResult("15.92, 15.98, 15.15, 17.19, 16.08, 13.24, 14.97, 14.6, 13.58, 14.48, 14.01, 17.09, 16.01, 12.36, 14.03, 16.2, 17.23, 16.79, 13.41, 16.55") {
      values.map(round).mkString(", ")
    }
  }

  test("Low range gaussian") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(-20, -10, 1, 0)
    assertResult("-15.78, -12.58, -13.55, -12.64, -19.13, -18.21, -12.34, -14.98, -15.34, -16.04, -15.04, -13.82, -14.97, -18.44, -16.13, -13.01, -15.6, -16.64, -18.71, -14.21") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 0 skew and 0 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 0, 0)
    assertResult("1.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0") {
      values.map(round).mkString(", ")
    }
  }

  // should be roughly uniform distribution
  test("gaussian with 0.5 skew and 0 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 0.5, 0)
    assertResult("0.1, 0.02, 0.84, 0.91, 0.86, 0.59, 0.55, 0.01, 0.51, 0.71, 0.53, 0.34, 0.94, 0.88, 0.81, 0.75, 0.88, 0.05, 0.76, 0.18") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 2 skew and 0 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 2, 0)
    assertResult("0.41, 0.54, 0.55, 0.47, 0.51, 0.34, 0.41, 0.54, 0.29, 0.65, 0.58, 0.5, 0.36, 0.41, 0.39, 0.6, 0.44, 0.46, 0.38, 0.42") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 1 skew and 1 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 1, 1)
    assertResult("0.74, 0.53, 0.74, 0.55, 0.91, 0.94, 0.31, 0.75, 0.44, 0.87, 0.69, 0.66, 0.64, 0.62, 0.84, 0.92, 0.38, 0.87, 0.61, 0.72") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 2 skew and -2 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 2, -2)
    assertResult("0.09, 0.13, 0.1, 0.2, 0.18, 0.23, 0.08, 0.04, 0.08, 0.17, 0.1, 0.09, 0.14, 0.13, 0.06, 0.13, 0.12, 0.11, 0.18, 0.15") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 0.5 skew and 5 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 0.5, 5)
    assertResult("0.41, 0.97, 0.99, 0.99, 0.99, 0.99, 0.99, 0.99, 0.96, 0.99, 0.99, 0.99, 0.99, 0.96, 0.98, 0.99, 0.99, 0.99, 0.99, 0.97") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 0.9 skew and 2 bias") {
    val values = for (i <- 1 to 20)
      yield skewedRandom.nextSkewedGaussian(0, 1, 0.9, 2)
    assertResult("0.88, 0.95, 0.7, 0.81, 0.53, 0.84, 0.83, 0.64, 0.52, 0.93, 0.68, 0.82, 0.56, 0.9, 0.69, 0.61, 0.95, 0.92, 0.61, 0.55") {
      values.map(round).mkString(", ")
    }
  }

  test("gaussian with 0.6 skew and 0 bias has uniform distribution") {
    val values = for (i <- 1 to 10000)
      yield skewedRandom.nextSkewedGaussian(0, 1, 0.6, 0)

    val dist = createDistribution(values)
    assertResult("(0.0,942), (0.1,1065), (0.2,1061), (0.3,981), (0.4,972), (0.5,936), (0.6,1025), (0.7,994), (0.8,1117), (0.9,907)") {
      dist.mkString(", ")
    }
  }

  private def round(v: Double): Float = roundToDecimal(v, 2)

  private def roundToDecimal(v: Double, decimals: Int): Float = {
    val n: Float = Math.pow(10, decimals).toFloat
    (v * n).toInt / n
  }

  private def createDistribution(values: IndexedSeq[Double]): Seq[(Float, Int)] =
    values.map(v => roundToDecimal(v, 1)).groupBy(x => x).map(x => x._1 -> x._2.size).toSeq.sortBy(x=>x._1)
}
