/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.CaveProcessor
import org.scalatest.FunSuite


class CaveProcessorSuite extends FunSuite {

  test("NextPhase") {
    val processor = new CaveProcessor(5, 5, 0.1, 0.9, 3.0, 2.0, CaveProcessor.KernelType.BASIC, false)
    processor.nextPhase()

    assertResult("WWWCC\nWWWWW\nWCWWW\nWW WW\nWCWWW\n") { processor.toString }
  }
}

