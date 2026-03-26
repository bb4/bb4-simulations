// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.spirograph.model

import org.scalatest.funsuite.AnyFunSuite

class ParametricEquationsSuite extends AnyFunSuite {

  test("radius 0 yields undefined equations") {
    val eq = ParametricEquations(0, 100, 50)
    assert(eq.xEquation === "x(t)=undefined")
    assert(eq.yEquation === "y(t)=undefined")
  }

  test("position 0 yields circle parametrics without epicycle terms") {
    val eq = ParametricEquations(60, 120, 0)
    assert(eq.xEquation === "x(t)=120cos(t)")
    assert(eq.yEquation === "y(t)=120sin(t)")
  }

  test("positive radius and position use default minus signs") {
    val eq = ParametricEquations(60, 120, 10)
    assert(eq.xEquation === "x(t)=120cos(t)-10cos(120t / 60)")
    assert(eq.yEquation === "y(t)=120sin(t)-10sin(120t / 60)")
  }

  test("negative position and negative radius flip actuals and sign1") {
    val eq = ParametricEquations(-5, 10, -3)
    assert(eq.xEquation === "x(t)=10cos(t)+3cos(10t / 5)")
    assert(eq.yEquation === "y(t)=10sin(t)-3sin(10t / 5)")
  }

  test("negative position and positive radius flip sign1 and sign2") {
    val eq = ParametricEquations(5, 10, -3)
    assert(eq.xEquation === "x(t)=10cos(t)+3cos(10t / 5)")
    assert(eq.yEquation === "y(t)=10sin(t)+3sin(10t / 5)")
  }

  test("positive position and negative radius flips actualRadius and sign2") {
    val eq = ParametricEquations(-5, 10, 3)
    assert(eq.xEquation === "x(t)=10cos(t)-3cos(10t / 5)")
    assert(eq.yEquation === "y(t)=10sin(t)+3sin(10t / 5)")
  }
}
