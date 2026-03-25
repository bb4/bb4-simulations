// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.utils

import org.scalatest.funsuite.AnyFunSuite

class FileUtilSuite extends AnyFunSuite {

  test("getSampleTiledData throws clear error when resource missing") {
    val ex = intercept[IllegalArgumentException] {
      FileUtil.getSampleTiledData("__definitely_missing_sample__")
    }
    assert(ex.getMessage.contains("Resource not found"))
  }
}
