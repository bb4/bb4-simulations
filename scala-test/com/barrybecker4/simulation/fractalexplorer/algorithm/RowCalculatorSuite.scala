// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import org.scalatest.funsuite.AnyFunSuite

class RowCalculatorSuite extends AnyFunSuite {

  /** Run-length optimization can differ slightly from a full scan on some pixels. */
  private val OPT_PARITY_EPS = 0.02

  private def fillGrid(alg: MandelbrotAlgorithm, useOpt: Boolean): Unit = {
    alg.setSize(48, 48)
    alg.setMaxIterations(80)
    alg.setParallelized(false)
    val rc = new RowCalculator(alg)
    rc.setUseRunLengthOptimization(useOpt)
    val w = alg.getModel.getWidth
    var y = 0
    while (y < alg.getModel.getHeight) {
      rc.calculateRow(w, y)
      y += 1
    }
  }

  private def gridSnapshot(m: FractalModel): IndexedSeq[Double] =
    for (y <- 0 until m.getHeight; x <- 0 until m.getWidth) yield m.getValue(x, y)

  test("run length optimization is close to naive row scan on most pixels") {
    val simple = new MandelbrotAlgorithm()
    fillGrid(simple, useOpt = false)
    val a = gridSnapshot(simple.getModel)

    val opt = new MandelbrotAlgorithm()
    fillGrid(opt, useOpt = true)
    val b = gridSnapshot(opt.getModel)

    assert(a.length == b.length)
    val diffs = a.zip(b).map { case (x, y) => math.abs(x - y) }
    val (close, far) = diffs.partition(_ < OPT_PARITY_EPS)
    assert(
      far.length <= 8,
      s"expected at most 8 outlier pixels, got ${far.length}, max diff ${if (far.isEmpty) 0.0 else far.max}"
    )
  }
}
