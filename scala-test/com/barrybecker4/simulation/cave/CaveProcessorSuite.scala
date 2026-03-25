/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.CaveProcessor
import org.scalatest.funsuite.AnyFunSuite

class CaveProcessorSuite extends AnyFunSuite {

  test("NextPhase") {
    val processor = new CaveProcessor(5, 5, 0.1, 0.9, 3.0, 2.0, CaveProcessor.KernelType.BASIC, false)
    processor.nextPhase()

    val expected = List.fill(5)(" " * 5).mkString("", "\n", "\n")
    assertResult(expected)(processor.toString)
  }

  test("NextPhaseParallelMatchesSequential") {
    def snapshot(p: CaveProcessor): String = p.toString
    def newMatchingPair(): (CaveProcessor, CaveProcessor) = {
      import com.barrybecker4.simulation.cave.model.Cave
      Cave.reseedRandom(424242L)
      val seq =
        new CaveProcessor(12, 11, 0.22, 0.85, 0.55, 0.12, CaveProcessor.KernelType.RADIAL5, false)
      Cave.reseedRandom(424242L)
      val par =
        new CaveProcessor(12, 11, 0.22, 0.85, 0.55, 0.12, CaveProcessor.KernelType.RADIAL5, true)
      (seq, par)
    }
    val (seq, par) = newMatchingPair()
    assert(snapshot(seq) == snapshot(par))
    seq.nextPhase()
    par.nextPhase()
    assert(snapshot(seq) == snapshot(par))
  }

  test("NextPhaseVisitsWideGridWithParallelism") {
    // Regression: all columns must be updated (no truncation from width / numThreads).
    val w = math.max(Runtime.getRuntime.availableProcessors(), 2) * 3 + 2
    val proc =
      new CaveProcessor(w, 4, 0.15, 0.88, 0.5, 0.2, CaveProcessor.KernelType.BASIC, useParallel = true)
    val before = proc.toString
    proc.nextPhase()
    val after = proc.toString
    assert(before != after)
    assert(after.length == before.length)
  }
}
