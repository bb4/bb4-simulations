// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import com.barrybecker4.math.function.ArrayFunction
import com.barrybecker4.math.interpolation.LINEAR
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author Barry Becker
  */
object FuncInverseSuite {
  private val EPS = 1e-12
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

  test("FunctionType sample arrays have length >= 2 and end at 1") {
    for (ft <- FunctionType.values) {
      val arr = ft.func
      assert(arr.length >= 2)
      assert(arr.last === 1.0)
      if (ft != FunctionType.RANDOM_UP) {
        assert(arr.head === 0.0)
      }
    }
  }

  test("deterministic FunctionType arrays are strictly increasing") {
    val skip = Set(FunctionType.RANDOM_UP)
    for (ft <- FunctionType.values if !skip.contains(ft)) {
      assert(isStrictlyIncreasingExceptLast(ft.func.toIndexedSeq),
        s"expected strictly increasing: $ft")
    }
  }

  test("ArrayFunction inverse map matches domain length for LINEAR") {
    val f = new ArrayFunction(FunctionType.LINEAR.func, LINEAR)
    val inv = f.inverseFunctionMap
    assert(inv.length === f.functionMap.length)
    assert(inv.head === 0.0)
    assert(inv.last === 1.0)
  }

  test("indexOfInterpolationMethod round-trips known methods") {
    import com.barrybecker4.math.interpolation.{COSINE, CUBIC, HERMITE, LINEAR, STEP}
    val methods = Seq(LINEAR, COSINE, CUBIC, HERMITE, STEP)
    for (m <- methods) {
      val idx = FunctionOptionsDialog.indexOfInterpolationMethod(m)
      assert(idx >= 0 && idx < 5)
    }
    assert(FunctionOptionsDialog.indexOfInterpolationMethod(LINEAR) === 0)
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
