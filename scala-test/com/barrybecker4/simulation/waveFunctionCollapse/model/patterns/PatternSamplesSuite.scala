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

    assertResult(124) { patternSamples.colors.length}
  }
}
