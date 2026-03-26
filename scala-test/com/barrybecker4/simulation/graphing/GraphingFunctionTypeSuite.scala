// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.{ArrayFunction, ErrorFunction}
import com.barrybecker4.math.interpolation.{COSINE, LINEAR}
import org.scalatest.funsuite.AnyFunSuite

class GraphingFunctionTypeSuite extends AnyFunSuite {

  test("every graphing FunctionType has non-empty unique name and non-null function") {
    val names = scala.collection.mutable.Set[String]()
    for (ft <- FunctionType.values) {
      assert(ft.name.nonEmpty, s"empty name for $ft")
      assert(ft.function != null, s"null function for $ft")
      assert(!names.contains(ft.name), s"duplicate name: ${ft.name}")
      names += ft.name
    }
  }

  test("FunctionType ordinal round-trips with combo-aligned names") {
    val vals = FunctionType.values
    for (i <- vals.indices) {
      val ft = FunctionType.fromOrdinal(i)
      assert(ft.ordinal === i)
      assert(vals(i) === ft)
      assert(vals(i).name === ft.name)
    }
  }

  test("GraphInterpolation preserves ArrayFunction sample arrays for different methods") {
    val base = FunctionType.DIAGONAL.function.asInstanceOf[ArrayFunction]
    val withLinear = GraphInterpolation(base, LINEAR).asInstanceOf[ArrayFunction]
    val withCosine = GraphInterpolation(base, COSINE).asInstanceOf[ArrayFunction]
    assert(withLinear.functionMap eq base.functionMap)
    assert(withCosine.functionMap eq base.functionMap)
    assert(withLinear ne base)
    assert(withCosine ne base)
  }

  test("GraphInterpolation leaves ErrorFunction unchanged") {
    val err = new ErrorFunction
    assert(GraphInterpolation(err, LINEAR) eq err)
  }

  test("sampled diagonal interpolates across domain (visible curve depends on this)") {
    val f = FunctionType.DIAGONAL.function.asInstanceOf[ArrayFunction]
    assert(math.abs(f.getValue(0.0)) < 1e-9)
    assert(math.abs(f.getValue(1.0) - 1.0) < 1e-9)
    assert(math.abs(f.getValue(0.5) - 0.5) < 1e-9)
  }

  test("GraphInterpolation with COSINE yields sensible values for two-point diagonal") {
    val base = FunctionType.DIAGONAL.function.asInstanceOf[ArrayFunction]
    val cosInterp = GraphInterpolation(base, COSINE).asInstanceOf[ArrayFunction]
    assert(math.abs(cosInterp.getValue(0.5) - 0.5) < 0.01,
      s"midpoint should be ~0.5, got ${cosInterp.getValue(0.5)}")
  }
}
