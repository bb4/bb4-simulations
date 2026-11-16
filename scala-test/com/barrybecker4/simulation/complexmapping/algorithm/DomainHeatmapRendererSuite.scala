/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.algorithm.functions.ZetaAnalyticExtensionFunction
import com.barrybecker4.simulation.complexmapping.algorithm.model.Box
import javax.vecmath.Point2d
import org.scalatest.funsuite.AnyFunSuite


class DomainHeatmapRendererSuite extends AnyFunSuite {

  private val zeta = ZetaAnalyticExtensionFunction()
  private val n = 80

  test("logMagnitude is low near the first non-trivial zero") {
    val nearZero = ComplexNumber(0.5, 14.134725)
    val away = ComplexNumber(0.5, 10.0)
    val nearMag = DomainHeatmapRenderer.logMagnitude(zeta.compute(nearZero, n))
    val awayMag = DomainHeatmapRenderer.logMagnitude(zeta.compute(away, n))
    assert(nearMag < awayMag)
    assert(nearMag < 0.0)
  }

  test("render produces a non-empty image for the critical strip") {
    val viewport = Box(new Point2d(0.0, 30.0), new Point2d(1.0, 0.0))
    val renderer = DomainHeatmapRenderer(
      zeta, n, viewport, sampleInc = 0.2,
      DomainColorMode.Magnitude, new MagnitudeColorMap(), showKnownZeros = true
    )
    val img = renderer.render(200, 300)
    assert(img.getWidth == 200)
    assert(img.getHeight == 300)
  }

  test("niceTicks returns increasing values within range") {
    val ticks = DomainHeatmapRenderer.niceTicks(0, 52, 8)
    assert(ticks.size >= 2)
    assert(ticks.head >= 0)
    assert(ticks.last <= 52 + 1e-9)
    assert(ticks == ticks.sorted)
  }
}
