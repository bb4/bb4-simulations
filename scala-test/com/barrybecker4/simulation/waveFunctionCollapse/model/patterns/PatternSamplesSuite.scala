// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.patterns.PatternSamples
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil
import org.scalatest.funsuite.AnyFunSuite



class PatternSamplesSuite extends AnyFunSuite {

  test("patternSamples initialization when N=2 and few colors in image") {
    val bitmap = FileUtil.readImage("samples/Angular.png")
    val patternSamples = PatternSamples(bitmap, 2)

    assertResult(3) { patternSamples.colors.length}
  }

  test("patternSamples initialization when N=2 and many colors in image") {
    val bitmap = FileUtil.readImage("samples/Eye.png")
    val patternSamples = PatternSamples(bitmap, 2)

    assertResult(64) { patternSamples.colors.length}
  }

  test("getPatternSymmetries") {
    val bitmap = FileUtil.readImage("samples/Angular.png")
    val patternSamples = PatternSamples(bitmap, 3)

    assertResult(
      Array(
        Array(0, 0, 0, 0, 0, 0, 1, 1, 1),
        Array(0, 0, 0, 0, 0, 0, 1, 1, 1),
        Array(0, 0, 1, 0, 0, 1, 0, 0, 1),
        Array(1, 0, 0, 1, 0, 0, 1, 0, 0),
        Array(1, 1, 1, 0, 0, 0, 0, 0, 0),
        Array(1, 1, 1, 0, 0, 0, 0, 0, 0),
        Array(1, 0, 0, 1, 0, 0, 1, 0, 0),
        Array(0, 0, 1, 0, 0, 1, 0, 0, 1)
      ), "pos 2, 3") {
      patternSamples.getPatternSymmetries(2, 3)
    }

    assertResult(
      Array(
        Array(0, 0, 0, 1, 1, 1, 2, 2, 1),
        Array(0, 0, 0, 1, 1, 1, 1, 2, 2),
        Array(0, 1, 1, 0, 1, 2, 0, 1, 2),
        Array(1, 1, 0, 2, 1, 0, 2, 1, 0),
        Array(1, 2, 2, 1, 1, 1, 0, 0, 0),
        Array(2, 2, 1, 1, 1, 1, 0, 0, 0),
        Array(2, 1, 0, 2, 1, 0, 1, 1, 0),
        Array(0, 1, 2, 0, 1, 2, 0, 1, 1)
      ), "pos 7, 11") {
      patternSamples.getPatternSymmetries(7, 11)
    }
  }
}
