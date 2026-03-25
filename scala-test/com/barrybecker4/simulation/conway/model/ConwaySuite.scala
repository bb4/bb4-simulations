// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import org.scalatest.funsuite.AnyFunSuite

class ConwaySuite extends AnyFunSuite {

  test("setValue with zero removes cell from sparse map") {
    val c = new Conway
    c.clearForTesting()
    c.setWrapping(false, 10, 10)
    val loc = IntLocation(1, 1)
    c.setValue(loc, 1)
    assert(c.isAlive(loc))
    c.setValue(loc, 0)
    assert(!c.isAlive(loc))
    assert(c.getValue(loc) == 0)
  }

  test("torus: corner cell sees neighbor wrapped from opposite corner") {
    val c = new Conway
    c.clearForTesting()
    c.setWrapping(true, 3, 3)
    c.setValue(IntLocation(2, 2), 1)
    assert(c.getNumNeighbors(IntLocation(0, 0)) == 1)
  }

  test("getCandidates includes live cells and their eight neighbors") {
    val c = new Conway
    c.clearForTesting()
    c.setWrapping(false, 10, 10)
    c.setValue(IntLocation(5, 5), 1)
    val cand = c.getCandidates
    assert(cand.contains(IntLocation(5, 5)))
    assert(cand.contains(IntLocation(4, 4)))
    assert(cand.contains(IntLocation(6, 6)))
    assert(cand.size == 9)
  }

  test("when wrap is on but width invalid, coordinates are not folded (no bogus mod)") {
    val c = new Conway
    c.clearForTesting()
    c.setWrapping(true, 0, 3)
    c.setValue(IntLocation(0, 0), 1)
    c.setValue(IntLocation(0, 2), 1)
    assert(c.getNumNeighbors(IntLocation(0, 0)) == 0)
  }

  test("solid block has eight live neighbors in the interior") {
    val c = new Conway
    c.clearForTesting()
    c.setWrapping(false, 10, 10)
    Seq((4, 4), (4, 5), (4, 6), (5, 4), (5, 5), (5, 6), (6, 4), (6, 5), (6, 6)).foreach { case (r, col) =>
      c.setValue(IntLocation(r, col), 1)
    }
    assert(c.getNumNeighbors(IntLocation(5, 5)) == 8)
  }
}
