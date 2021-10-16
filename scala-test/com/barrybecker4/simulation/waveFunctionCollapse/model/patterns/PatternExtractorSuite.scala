// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.{OverlappingImageParams, OverlappingModel}
import com.barrybecker4.simulation.waveFunctionCollapse.patterns.{PatternExtractor, PatternSamples}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil
import org.scalatest.funsuite.AnyFunSuite

class PatternExtractorSuite extends AnyFunSuite {

  test("patternExtractor initialization when N=2 ") {
    val bitmap = FileUtil.readImage("samples/Angular.png")
    val patternExtractor = PatternExtractor(bitmap, OverlappingImageParams(2, 2, periodicInput = false, 0))

    assertResult(22) { patternExtractor.tCounter}
    assertResult(
      Array(32.0, 3.0, 3.0, 14.0, 15.0, 15.0, 3.0, 3.0, 10.0, 11.0, 11.0, 40.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 12.0, 2.0, 2.0, 16.0)
    ) {
      patternExtractor.weights
    }

    assertResult(
      Array(
        Array(0, 0, 0, 0), Array(0, 0, 0, 1), Array(0, 0, 1, 0), Array(0, 0, 1, 1), Array(0, 1, 0, 1),
        Array(1, 0, 1, 0), Array(1, 1, 1, 2), Array(1, 1, 2, 1), Array(1, 1, 2, 2), Array(1, 2, 1, 2),
        Array(2, 1, 2, 1), Array(2, 2, 2, 2), Array(0, 1, 1, 1), Array(1, 0, 1, 1), Array(1, 2, 2, 2),
        Array(2, 1, 2, 2), Array(1, 2, 1, 1), Array(2, 1, 1, 1), Array(2, 2, 1, 1), Array(0, 1, 0, 0),
        Array(1, 0, 0, 0), Array(1, 1, 0, 0)
      )
    ) {
      patternExtractor.patterns
    }
  }

}
