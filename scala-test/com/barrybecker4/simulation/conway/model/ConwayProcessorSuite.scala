// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.ConwayProcessor.RuleType
import org.scalatest.funsuite.AnyFunSuite

class ConwayProcessorSuite extends AnyFunSuite {

  test("TraditionalB3S23 blinker oscillates to horizontal") {
    val p = new ConwayProcessor(useParallel = false)
    p.clearGridForTesting()
    p.setWrap(false, 5, 5)
    p.setRuleType(RuleType.TraditionalB3S23)

    p.setAlive(1, 2)
    p.setAlive(2, 2)
    p.setAlive(3, 2)

    p.nextPhase()

    assert(p.getValue(IntLocation(2, 1)) == 1)
    assert(p.getValue(IntLocation(2, 2)) == 2)
    assert(p.getValue(IntLocation(2, 3)) == 1)
    assert(p.getValue(IntLocation(1, 2)) == 0)
    assert(p.getValue(IntLocation(3, 2)) == 0)
  }

  test("setDead clears live cell") {
    val p = new ConwayProcessor(useParallel = false)
    p.clearGridForTesting()
    p.setWrap(false, 5, 5)
    p.setRuleType(RuleType.TraditionalB3S23)
    p.setAlive(2, 2)
    assert(p.getValue(IntLocation(2, 2)) == 1)
    p.setDead(2, 2)
    assert(p.getValue(IntLocation(2, 2)) == 0)
  }

  test("HighlifeB36S23: dead cell with six neighbors is born") {
    val p = new ConwayProcessor(useParallel = false)
    p.clearGridForTesting()
    p.setWrap(false, 6, 6)
    p.setRuleType(RuleType.HighlifeB36S23)
    // Center (2,2) dead with six live neighbors — birth in B6.
    p.setAlive(1, 2)
    p.setAlive(3, 2)
    p.setAlive(2, 1)
    p.setAlive(2, 3)
    p.setAlive(1, 1)
    p.setAlive(1, 3)
    assert(!p.isAlive(IntLocation(2, 2)))
    p.nextPhase()
    assert(p.isAlive(IntLocation(2, 2)))
  }

  test("SwarmB356S23: dead cell with five neighbors is born") {
    val p = new ConwayProcessor(useParallel = false)
    p.clearGridForTesting()
    p.setWrap(false, 6, 6)
    p.setRuleType(RuleType.SwarmB356S23)
    // Center (2,2) has exactly five live neighbors — Swarm births on 5 (not standard Life).
    p.setAlive(1, 1)
    p.setAlive(1, 2)
    p.setAlive(2, 1)
    p.setAlive(2, 3)
    p.setAlive(3, 2)
    assert(!p.isAlive(IntLocation(2, 2)))
    p.nextPhase()
    assert(p.isAlive(IntLocation(2, 2)))
  }

  test("AmoebaB34S456: still life-like cross survives on four neighbors") {
    val p = new ConwayProcessor(useParallel = false)
    p.clearGridForTesting()
    p.setWrap(false, 5, 5)
    p.setRuleType(RuleType.AmoebaB34S456)
    p.setAlive(2, 1)
    p.setAlive(2, 2)
    p.setAlive(2, 3)
    p.setAlive(1, 2)
    p.setAlive(3, 2)
    p.nextPhase()
    assert(p.isAlive(IntLocation(2, 2)))
    assert(p.getValue(IntLocation(2, 2)) >= 2)
  }

  test("parallel and sequential runs produce same timestep result") {
    def stateAfter(steps: Int, useParallel: Boolean): Map[Location, Int] = {
      val p = new ConwayProcessor(useParallel = useParallel)
      p.clearGridForTesting()
      p.setWrap(false, 12, 12)
      p.setRuleType(RuleType.TraditionalB3S23)
      p.setAlive(5, 5)
      p.setAlive(5, 6)
      p.setAlive(5, 7)
      p.setAlive(6, 6)
      p.setAlive(7, 7)
      for (_ <- 0 until steps) p.nextPhase()
      p.getPoints.map(loc => loc -> p.getValue(loc)).toMap
    }

    for (s <- 1 to 5) {
      val seq = stateAfter(s, useParallel = false)
      val par = stateAfter(s, useParallel = true)
      assert(seq == par, s"Mismatch after $s steps: seq $seq vs par $par")
    }
  }
}
