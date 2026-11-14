// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import com.barrybecker4.math.function.ArrayFunction
import com.barrybecker4.math.interpolation.{CUBIC, LINEAR}
import com.barrybecker4.simulation.common.ui.FunctionPlotOptionsDialog
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author Barry Becker
  */
object FuncInverseSuite {
  private val EPS = 1e-12
  /** Dense samples (SINE) support a tighter round-trip check. */
  private val DenseRoundTripEps = 0.05
  /** Coarse sample arrays (e.g. QUADRATIC) are only accurate near knots. */
  private val CoarseYRoundTripEps = 0.15
}

class FuncInverseSuite extends AnyFunSuite {

  import FuncInverseSuite.*

  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("sinusoidalFunc ends at 1 and is non-decreasing") {
    val arr = sinusoidalFunc(3 * java.lang.Math.PI, 50)
    assert(arr.nonEmpty)
    assert(arr.last === 1.0)
    assert(arr.head === 0.0)
    assert(isNonDecreasing(arr.toIndexedSeq))
  }

  test("randomIncreasingArray is monotonic with last 1") {
    val rng = new java.util.Random(42L)
    val arr = randomIncreasingArray(100, rng)
    assert(arr.length === 100)
    assert(arr.last === 1.0)
    assert(isStrictlyIncreasingExceptLast(arr.toIndexedSeq))
  }

  test("RANDOM_UP is deterministic with fixed seed") {
    assert(InverseFunctionType.RANDOM_UP.func sameElements
      randomIncreasingArray(100, new java.util.Random(42L)))
  }

  test("InverseFunctionType sample arrays have length >= 2 and end at 1") {
    for (ft <- InverseFunctionType.values) {
      val arr = ft.func
      assert(arr.length >= 2)
      assert(arr.last === 1.0)
      if (ft != InverseFunctionType.RANDOM_UP) {
        assert(arr.head === 0.0)
      }
    }
  }

  test("deterministic InverseFunctionType arrays are strictly increasing") {
    val skip = Set(InverseFunctionType.RANDOM_UP)
    for (ft <- InverseFunctionType.values if !skip.contains(ft)) {
      assert(isStrictlyIncreasingExceptLast(ft.func.toIndexedSeq),
        s"expected strictly increasing: $ft")
    }
  }

  test("ArrayFunction inverse map matches domain length for LINEAR") {
    val f = new ArrayFunction(InverseFunctionType.LINEAR.func, LINEAR)
    val inv = f.inverseFunctionMap
    assert(inv.length === f.functionMap.length)
    assert(inv.head === 0.0)
    assert(inv.last === 1.0)
  }

  test("fromSamples of inverse map does not re-invert endpoints") {
    val f = new ArrayFunction(InverseFunctionType.QUADRATIC.func, LINEAR)
    val plottedInverse = ArrayFunction.fromSamples(f.inverseFunctionMap, LINEAR)
    assert(plottedInverse.functionMap eq f.inverseFunctionMap)
    assert(plottedInverse.functionMap.head === 0.0)
    assert(plottedInverse.functionMap.last === 1.0)
    // Identity placeholder inverse — not a second FunctionInverter pass
    assert(plottedInverse.inverseFunctionMap sameElements ArrayFunction.identityMap(f.inverseFunctionMap.length))
  }

  test("dense SINE round-trips getValue/getInverseValue under LINEAR and CUBIC") {
    val xs = Seq(0.0, 0.25, 0.5, 0.75, 1.0)
    for (method <- Seq(LINEAR, CUBIC)) {
      val f = new ArrayFunction(InverseFunctionType.SINE.func, method)
      for (x <- xs) {
        val y = f.getValue(x)
        val xBack = f.getInverseValue(y)
        assert(math.abs(xBack - x) < DenseRoundTripEps,
          s"SINE/$method: f^{-1}(f($x))=$xBack (y=$y)")
        val y2 = f.getValue(f.getInverseValue(y))
        assert(math.abs(y2 - y) < DenseRoundTripEps,
          s"SINE/$method: f(f^{-1}($y))=$y2")
      }
    }
  }

  test("coarse QUADRATIC preserves f(f^{-1}(y))≈y under LINEAR and CUBIC") {
    val ys = Seq(0.0, 0.25, 0.5, 0.75, 1.0)
    for (method <- Seq(LINEAR, CUBIC)) {
      val f = new ArrayFunction(InverseFunctionType.QUADRATIC.func, method)
      assert(math.abs(f.getValue(0.0)) < DenseRoundTripEps)
      assert(math.abs(f.getValue(1.0) - 1.0) < DenseRoundTripEps)
      for (y <- ys) {
        val y2 = f.getValue(f.getInverseValue(y))
        assert(math.abs(y2 - y) < CoarseYRoundTripEps,
          s"QUADRATIC/$method: f(f^{-1}($y))=$y2")
      }
    }
  }

  test("indexOfInterpolationMethod round-trips via shared helper") {
    import com.barrybecker4.math.interpolation.{COSINE, CUBIC, HERMITE, LINEAR, STEP}
    val methods = Seq(LINEAR, COSINE, CUBIC, HERMITE, STEP)
    for (m <- methods) {
      val idx = FunctionPlotOptionsDialog.indexOfInterpolationMethod(m)
      assert(idx === m.ordinal)
    }
  }

  private def isNonDecreasing(xs: IndexedSeq[Double]): Boolean =
    xs.iterator.sliding(2).forall { case Seq(a, b) => a <= b + EPS; case _ => true }

  private def isStrictlyIncreasingExceptLast(xs: IndexedSeq[Double]): Boolean =
    if (xs.length < 2) true
    else {
      val prefix = xs.init
      prefix.iterator.sliding(2).forall {
        case Seq(a, b) => a < b
        case _         => true
      } && prefix.last < xs.last + EPS
    }
}
