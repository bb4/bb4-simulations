// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import org.scalatest.funsuite.AnyFunSuite

class AlgorithmEnumSuite extends AnyFunSuite {

  test("createInstance returns MandelbrotAlgorithm for MANDELBROT") {
    assert(AlgorithmEnum.MANDELBROT.createInstance().isInstanceOf[MandelbrotAlgorithm])
  }

  test("createInstance returns JuliaAlgorithm for JULIA") {
    assert(AlgorithmEnum.JULIA.createInstance().isInstanceOf[JuliaAlgorithm])
  }

  test("VALUES lists both algorithms") {
    assert(AlgorithmEnum.VALUES.toSet == Set(AlgorithmEnum.MANDELBROT, AlgorithmEnum.JULIA))
  }
}
