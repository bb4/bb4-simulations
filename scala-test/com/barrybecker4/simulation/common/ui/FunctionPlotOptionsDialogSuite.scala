// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.math.interpolation.{COSINE, CUBIC, HERMITE, LINEAR, STEP, InterpolationMethod}
import org.scalatest.funsuite.AnyFunSuite


class FunctionPlotOptionsDialogSuite extends AnyFunSuite {

  test("indexOfInterpolationMethod round-trips VALUES") {
    for (m <- InterpolationMethod.VALUES) {
      val idx = FunctionPlotOptionsDialog.indexOfInterpolationMethod(m)
      assert(idx === m.ordinal)
      assert(InterpolationMethod.VALUES(idx) === m)
    }
  }

  test("indexOfInterpolationMethod covers known methods") {
    val methods = Seq(STEP, LINEAR, CUBIC, COSINE, HERMITE)
    for (m <- methods) {
      val idx = FunctionPlotOptionsDialog.indexOfInterpolationMethod(m)
      assert(idx >= 0 && idx < InterpolationMethod.VALUES.length)
      assert(InterpolationMethod.VALUES(idx) === m)
    }
  }
}
