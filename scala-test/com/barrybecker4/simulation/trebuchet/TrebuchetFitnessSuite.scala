// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.optimization.parameter.types.DoubleParameter
import com.barrybecker4.simulation.trebuchet.model.Trebuchet
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Component
import javax.swing.{JButton, JLabel}
import scala.util.Random

/** Headless trebuchet objective: six dialog parameters, and a throw that finishes. */
class TrebuchetFitnessSuite extends AnyFunSuite {

  private val Bounds = List(
    ("counterweight lever length", 1.0, 3.0),
    ("counterweight mass", 2.0, 60.0),
    ("projectile mass", 0.2, 5.0),
    ("sling length", 0.2, 3.0),
    ("sling lever length", 1.0, 5.0),
    ("sling release angle", 0.0, math.Pi / 2)
  )

  test("search parameters use the options-dialog bounds and the trebuchet's current values") {
    val trebuchet = new Trebuchet
    val params = TrebuchetParameters.initialArray(trebuchet, new Random(1))
    assert(params.size == 6)
    val expectedValues = List(
      trebuchet.getCounterWeightLeverLength,
      trebuchet.getCounterWeightMass,
      trebuchet.getProjectileMass,
      trebuchet.getSlingLength,
      trebuchet.getSlingLeverLength,
      trebuchet.getSlingReleaseAngle
    )
    Bounds.zip(expectedValues).zipWithIndex.foreach { case (((name, min, max), value), i) =>
      val p = params.get(i).asInstanceOf[DoubleParameter]
      assert(p.name == name)
      assert(p.minValue == min)
      assert(p.maxValue == max)
      assert(p.getValue == value)
    }
  }

  test("a throw that never releases is a penalty, and a finite range is negative distance") {
    assert(TrebuchetFitness.score(released = false, x = 10, y = 0) == TrebuchetFitness.UnstablePenalty)
    assert(TrebuchetFitness.score(released = true, x = 12.5, y = 0) == -12.5)
    assert(TrebuchetFitness.score(released = true, x = Double.NaN, y = 0) == TrebuchetFitness.UnstablePenalty)
    assert(TrebuchetFitness.score(released = true, x = 1, y = Double.PositiveInfinity) == TrebuchetFitness.UnstablePenalty)
  }

  test("evaluating the default design finishes, applies the parameters, and returns a finite fitness") {
    val trebuchet = new Trebuchet
    val params = TrebuchetParameters.initialArray(trebuchet, new Random(1))
    val shot = TrebuchetFitness.shoot(trebuchet, params)
    assert(shot.steps > 0)
    assert(shot.steps <= TrebuchetFitness.MaxSteps)
    assert(shot.fitness.isFinite)
    assert(trebuchet.getProjectileMass == params.get(2).getValue)
    if (shot.released) assert(shot.fitness == -shot.range)
    else assert(shot.fitness == TrebuchetFitness.UnstablePenalty)
  }

  test("Optimize button runs a short search and shows the fitness") {
    val simulator = new TrebuchetSimulator
    simulator.optimizationEvaluations = 1
    val controls = simulator.createTopControls
    val button = find[JButton](controls, _.getName == "optimize")
    val status = find[JLabel](controls, _.getName == "optimizeStatus")
    button.doClick()
    val deadline = System.currentTimeMillis() + 60000
    while (!button.isEnabled && System.currentTimeMillis() < deadline)
      Thread.sleep(20)
    assert(button.isEnabled)
    assert(button.getText == "Optimize")
    assert(status.getText.startsWith("fitness ") || status.getText.startsWith("no throw"))
    val mass = simulator.getTrebuchet.getProjectileMass
    assert(mass >= TrebuchetParameters.ProjectileMass.min && mass <= TrebuchetParameters.ProjectileMass.max)
  }

  private def find[T <: Component](root: Component, pred: T => Boolean)(using ct: scala.reflect.ClassTag[T]): T = {
    val found = walk(root).collect { case c: T if pred(c) => c }
    assert(found.nonEmpty)
    found.head
  }

  private def walk(component: Component): List[Component] =
    component match {
      case container: java.awt.Container =>
        component :: container.getComponents.toList.flatMap(walk)
      case _ => List(component)
    }
}
