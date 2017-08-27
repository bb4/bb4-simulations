// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.common.geometry.ByteLocation
import javax.vecmath.Vector2d
import org.scalatest.FunSuite
import com.barrybecker4.simulation.liquid.config.SourceSuite.{LOCATION_START, LOCATION_STOP, VELOCITY1}


object SourceSuite extends FunSuite {
  val LOCATION_START = new ByteLocation(1, 1)
  val LOCATION_STOP = new ByteLocation(1, 1)
  val VELOCITY1 = new Vector2d(1, 1)
}

class TestSource extends FunSuite {
  /** class under test. */
  private var src: Source = _

  test("OnTimeNoRepeat") {
    src = new Source(LOCATION_START, VELOCITY1)
    verifyStatus(src, 0.0, true)
    verifyStatus(src, 0.1, true)
    src = new Source(LOCATION_START, LOCATION_STOP, VELOCITY1, 2.0, -1, -1)
    verifyStatus(src, 0.0, false)
    verifyStatus(src, 1.9, false)
    verifyStatus(src, 2.0, true)
    verifyStatus(src, 20.0, true)
  }

  test("OnTimeDuration") {
    src = new Source(LOCATION_START, LOCATION_STOP, VELOCITY1, 2.0, 1.0, -1)
    verifyStatus(src, 0.0, false)
    verifyStatus(src, 1.9, false)
    verifyStatus(src, 2.0, true)
    verifyStatus(src, 2.5, true)
    verifyStatus(src, 3.0, false)
    verifyStatus(src, 3.3, false)
    verifyStatus(src, 20.0, false)
  }

  test("OnTimeRepeat") {
    src = new Source(LOCATION_START, LOCATION_STOP, VELOCITY1, 2.0, 1.0, 3.0)
    // starts at 2.0, continues to 3.0, off until 5.0, on again until 6.0, off until 8.0,...
    verifyStatus(src, 0.0, false)
    verifyStatus(src, 1.9, false)
    verifyStatus(src, 2.0, true)
    verifyStatus(src, 2.5, true)
    verifyStatus(src, 3.0, false)
    verifyStatus(src, 3.3, false)
    verifyStatus(src, 4.3, false)
    verifyStatus(src, 5.3, true)
    verifyStatus(src, 6.3, false)
    verifyStatus(src, 10.1, false)
    verifyStatus(src, 11.1, true)
    verifyStatus(src, 11.2, true)
  }

  private def verifyStatus(src: Source, t: Double, expectedValue: Boolean) = {
    val isOn = src.isOn(t)
    val err = if (isOn == expectedValue) ""
    else " : ERROR"
    ////System.out.println("checking t="+t +" exp="+expectedValue +" got=" + isOn  + err);
    assertResult(expectedValue) { isOn }
  }
}
