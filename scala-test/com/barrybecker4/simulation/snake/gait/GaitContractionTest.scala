// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.math.function.SINE_WAVE
import com.barrybecker4.simulation.snake.LocomotionParameters
import org.scalatest.funsuite.AnyFunSuite

class GaitContractionTest extends AnyFunSuite {

  private def paramsWith(
      amplitude: Double = 0.1,
      period: Double = 4.0,
      speed: Double = 0.0,
      direction: Double = 0.0
  ): LocomotionParameters = {
    val p = new LocomotionParameters
    p.waveType = SINE_WAVE
    p.waveAmplitude = amplitude
    p.wavePeriod = period
    p.waveSpeed = speed
    p.direction = direction
    p
  }

  test("undulatory L/R are opposite and antisymmetric around 1.0") {
    val params = paramsWith()
    params.gaitType = GaitType.HORIZONTAL_UNDULATORY
    val segmentIndex = 4
    val time = 0.0
    val theta = segmentIndex.toDouble / params.wavePeriod - params.waveSpeed * time
    val wave = Math.sin(theta)
    val expectedOffset = params.waveAmplitude * (wave - params.direction)

    val (left, right) = params.gaitType.gait.sideContractions(segmentIndex, params, time)

    assert(math.abs(left - (1.0 + expectedOffset)) < 1e-12)
    assert(math.abs(right - (1.0 - expectedOffset)) < 1e-12)
    assert(math.abs(left + right - 2.0) < 1e-12)
  }

  test("undulatory frictionScale is 1.0") {
    val params = paramsWith()
    params.gaitType = GaitType.HORIZONTAL_UNDULATORY
    assert(params.gaitType.gait.frictionScale(4, params, 0.0) == 1.0)
  }

  test("rectilinear L and R contractions are equal when direction is 0") {
    val params = paramsWith(amplitude = 0.1, period = 4.0, direction = 0.0)
    params.gaitType = GaitType.RECTILINEAR
    val segmentIndex = 4
    val time = 0.0
    val wave = Math.sin(segmentIndex.toDouble / params.wavePeriod)
    val expected = 1.0 + RectilinearGait.AmplitudeScale * params.waveAmplitude * wave
    val (left, right) = params.gaitType.gait.sideContractions(segmentIndex, params, time)
    assert(math.abs(left - right) < 1e-12)
    assert(math.abs(left - expected) < 1e-12)
  }

  test("rectilinear direction biases L/R for steering without full undulation") {
    val params = paramsWith(amplitude = 0.1, period = 4.0, direction = 0.8)
    params.gaitType = GaitType.RECTILINEAR
    val (left, right) = params.gaitType.gait.sideContractions(4, params, 0.0)
    val steer = RectilinearGait.SteerScale * params.waveAmplitude * params.direction
    assert(math.abs((right - left) - 2.0 * steer) < 1e-12)
    assert(right > left)
  }

  test("rectilinear frictionScale is higher when shortening") {
    val params = paramsWith(amplitude = 0.1, period = 4.0, speed = 0.0)
    params.gaitType = GaitType.RECTILINEAR
    // segment 4, period 4 => theta = 1, sin(1) > 0 => elongating => scale <= 1
    val elongating = params.gaitType.gait.frictionScale(4, params, 0.0)
    // Choose index so sin(theta) < 0: theta = 5 for period ~4? 5/4 = 1.25, sin(1.25)>0
    // sin(theta)<0 for theta in (pi, 2pi): use segmentIndex such that index/period ≈ 4.0
    val shortening = params.gaitType.gait.frictionScale(16, params, 0.0) // 16/4=4, sin(4)<0
    assert(Math.sin(16.0 / 4.0) < 0.0)
    assert(Math.sin(4.0 / 4.0) > 0.0)
    assert(shortening > elongating)
    assert(shortening > 1.0)
    assert(elongating <= 1.0)
  }

  test("side-winding frictionScale peaks with positive wave") {
    val params = paramsWith(amplitude = 0.1, period = 4.0)
    params.gaitType = GaitType.SIDE_WINDING
    val atPeakish = params.gaitType.gait.frictionScale(4, params, 0.0) // sin(1) > 0
    val atTroughish = params.gaitType.gait.frictionScale(16, params, 0.0) // sin(4) < 0
    assert(atPeakish > atTroughish)
    assert(atPeakish > 1.0)
    assert(math.abs(atTroughish - 1.0) < 1e-12)
  }

  test("side-winding L/R are opposite like undulatory") {
    val params = paramsWith()
    params.gaitType = GaitType.SIDE_WINDING
    val (left, right) = params.gaitType.gait.sideContractions(4, params, 0.0)
    assert(math.abs(left + right - 2.0) < 1e-12)
  }

  test("concertina outside fold window is near rest") {
    val params = paramsWith(amplitude = 0.2, period = 4.0, speed = 0.0)
    params.gaitType = GaitType.CONCERTINA
    // With speed 0, fold window is fixed near low segment indices for frac of cycle
    val (leftOutside, rightOutside) = params.gaitType.gait.sideContractions(20, params, 0.0)
    assert(math.abs(leftOutside - 1.0) < 0.05)
    assert(math.abs(rightOutside - 1.0) < 0.05)
  }

  test("concertina inside fold window has elevated friction") {
    val params = paramsWith(amplitude = 0.2, period = 4.0, speed = 0.0)
    params.gaitType = GaitType.CONCERTINA
    val inFold = params.gaitType.gait.frictionScale(0, params, 0.0)
    val outFold = params.gaitType.gait.frictionScale(20, params, 0.0)
    assert(inFold > outFold)
    assert(inFold > 1.0)
  }
}
