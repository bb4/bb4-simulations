// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.optimization.parameter.{NumericParameterArray, ParameterArray}
import com.barrybecker4.optimization.parameter.types.DoubleParameter
import com.barrybecker4.simulation.trebuchet.model.Trebuchet

import scala.util.Random

/**
  * The six trebuchet design parameters from the options dialog, and the search settings
  * shared by the Optimize button and the benchmark.
  */
object TrebuchetParameters {

  /** Same scale the app has always passed to the optimizer. */
  val FitnessRange = 0.3

  /** Evaluations per Optimize click, matching the benchmark default so a click returns. */
  val MaxEvaluations = 40

  final case class Bound(name: String, min: Double, max: Double)

  val CounterweightLever: Bound = Bound("counterweight lever length", 1.0, 3.0)
  val CounterweightMass: Bound = Bound("counterweight mass", 2.0, 60.0)
  val ProjectileMass: Bound = Bound("projectile mass", 0.2, 5.0)
  val SlingLength: Bound = Bound("sling length", 0.2, 3.0)
  val SlingLever: Bound = Bound("sling lever length", 1.0, 5.0)
  val ReleaseAngle: Bound = Bound("sling release angle", 0.0, math.Pi / 2)

  val All: List[Bound] = List(
    CounterweightLever, CounterweightMass, ProjectileMass, SlingLength, SlingLever, ReleaseAngle
  )

  /** Starting point is the trebuchet's current design, clamped into the dialog bounds. */
  def initialArray(trebuchet: Trebuchet, rnd: Random): NumericParameterArray = {
    val values = List(
      trebuchet.getCounterWeightLeverLength,
      trebuchet.getCounterWeightMass,
      trebuchet.getProjectileMass,
      trebuchet.getSlingLength,
      trebuchet.getSlingLeverLength,
      trebuchet.getSlingReleaseAngle
    )
    val params = All.zip(values).map { case (bound, value) =>
      val clamped = math.min(bound.max, math.max(bound.min, value))
      DoubleParameter(clamped, bound.min, bound.max, bound.name)
    }
    new NumericParameterArray(params.toIndexedSeq, 5, rnd)
  }

  /** Rebuild `trebuchet` from the six parameter values, in dialog order. */
  def apply(trebuchet: Trebuchet, params: ParameterArray): Unit = {
    trebuchet.configure(
      params.get(0).getValue,
      params.get(1).getValue,
      params.get(2).getValue,
      params.get(3).getValue,
      params.get(4).getValue,
      params.get(5).getValue
    )
  }
}
