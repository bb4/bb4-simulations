// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model._
import org.junit.Test
import javax.vecmath.Vector2d
import org.junit.Assert.assertEquals
import org.scalatest.funsuite.AnyFunSuite
import VelocityInterpolatorSuite._


/**
  * @author Barry Becker
  */
object VelocityInterpolatorSuite {
  private val DIM = 6
  private val TEST_COORDS = Array(Array(2.1, 3.7), Array(2.0, 2.0), Array(2.5, 2.0), Array(2.0, 2.5), Array(2.1, 2.9), Array(3.5, 2.0))
}

class VelocityInterpolatorSuite extends AnyFunSuite {

  /** instance under test. */
  private var interpolator: VelocityInterpolator = _

  private def verifyUniformField(expectedVelocity: Vector2d) = {
    val grid = new UniformGrid(DIM, DIM, expectedVelocity)
    interpolator = new VelocityInterpolator(grid)
    for (testCoord <- TEST_COORDS) {
      val x = testCoord(0)
      val y = testCoord(1)
      val cell = grid.getCell(x.toInt, y.toInt)
      val point = new Particle(x, y, cell)
      val vel = interpolator.findVelocity(point)
      assertEquals("Unexpected interpolated velocity for point " + point, expectedVelocity, vel)
    }
  }

  test("InterpolateVelocities") {
    val cb = new CellBlock
    cb.setPressures(1.0)
    cb.setAllCellParticles(5)
    val cell = cb.get(0, 0)

    //particle = new Particle(1.1, 1.1, cell);
    //verifyParticleVelocity(particle, cb,  new Vector2d(0.0, 0.0));
    cb.get(-1, 1).initializeVelocity(1.0, 0.0) // upper left
    cb.get(0, 1).initializeVelocity(.9, 0.0) // upper middle
    cb.get(-1, 0).initializeVelocity(.91, 1.0) // middle left
    cb.get(0, 0).initializeVelocity(0.7, 0.7) // center
    cb.get(1, 0).initializeVelocity(0.0, 0.4) // right middle
    cb.get(-1, -1).initializeVelocity(0.5, 0.6) // left bottom
    cb.get(0, -1).initializeVelocity(0.3, 0.3) // middle bottom
    cb.get(1, -1).initializeVelocity(.1, 0.0) // right bottom

    var particle: Particle = new Particle(1.1, 1.1, cell) // lower left

    verifyParticleVelocity(particle, cb, new Vector2d(0.7254, 0.46))
    particle = new Particle(1.9, 1.1, cell) // lower right

    verifyParticleVelocity(particle, cb, new Vector2d(0.5606, 0.22))
    particle = new Particle(1.1, 1.9, cell)
    verifyParticleVelocity(particle, cb, new Vector2d(0.9294, 0.78))
    particle = new Particle(1.9, 1.9, cell) // upper right

    verifyParticleVelocity(particle, cb, new Vector2d(0.7966, 0.54))
    particle = new Particle(1.5, 1.5, cell)
    verifyParticleVelocity(particle, cb, new Vector2d(0.805, 0.5))
    cb.setVelocities(0.6, 0.7)
    particle = new Particle(1.1, 1.1, cell)
    verifyParticleVelocity(particle, cb, new Vector2d(0.6, 0.7))
  }

  private def verifyParticleVelocity(particle: Particle, cb: CellBlock, expectedVelocity: Vector2d) = {
    val cell = cb.getAbsolute(1, 1)
    val i = particle.x.toInt
    val j = particle.y.toInt
    if (i > 2 || j > 2) System.out.println("i=" + i + " j=" + j)
    assert(i < 3 && j < 3, "i=" + i + " j=" + j)
    val ii = if ((particle.x - i) > 0.5) i + 1
    else i - 1
    val jj = if ((particle.y - j) > 0.5) j + 1
    else j - 1
    if (ii > 2 || jj > 2) System.out.println("ii=" + ii + " jj=" + jj)
    System.out.println("i=" + i + " j=" + j + "    ii=" + ii + " jj=" + jj)
    val interpolator = new VelocityInterpolator(null)
    val vel = interpolator.interpolateVelocity(particle, cell,
      cb.getAbsolute(ii, j), cb.getAbsolute(i, jj),
      cb.getAbsolute(i - 1, j), cb.getAbsolute(i - 1, jj), // u
      cb.getAbsolute(i, j - 1), cb.getAbsolute(ii, j - 1)) // v
    if (!vel.epsilonEquals(expectedVelocity, 0.00000000001)) System.out.println("vel for " + particle + " was " + vel)
    //Assert.assertTrue("vel for particle "+particle +" was "+
    // vel, vel.epsilonEquals(expectedVelocity, 0.00000000001));
  }

  private def getRandomCoord = 1 + Math.random * (DIM - 2)
}

