// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.common.math.LinearUtil
import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.simulation.liquid.model._
import org.junit.Test
import javax.vecmath.Vector2d

import com.barrybecker4.simulation.liquid.compute.ParticleAdvector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import ParticleAdvectorSuite._
import org.scalatest.FunSuite


/**
  * @author Barry Becker
  */
object ParticleAdvectorSuite {
  /** delta time */
  private val DT = 0.1
  private val DIM = 6
  private val TOL = 0.0000000001
}

class ParticleAdvectorSuite extends FunSuite {

  /** instance under test. */
  private var particleAdvector: ParticleAdvector = _

  test("AdvectInUpdateInAbsenceOfFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val part = particles.iterator.next
    particleAdvector.advectParticles(DT, particles)
    assertEquals("Unexpected age for particle", 0.1, part.getAge, TOL)
    val pos = new Vector2d(part.x, part.y)
    assertTrue("Unexpected new particle position: " + pos,
      LinearUtil.appxVectorsEqual(new Vector2d(2.1, 2.5), pos, MathUtil.EPS_MEDIUM))
  }

  test("AdvectInUpdateInNorthEastFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val part = particles.iterator.next
    val newDt = particleAdvector.advectParticles(DT, particles)
    assertEquals("Unexpected age for particle", 0.1, part.getAge, TOL)
    val pos = new Vector2d(part.x, part.y)
    assertTrue("Unexpected new particle position: " + pos,
      LinearUtil.appxVectorsEqual(new Vector2d(2.105, 2.505), pos, MathUtil.EPS_MEDIUM))
    assertEquals("Timestep unexpectedly changed", DT, newDt, TOL)
  }

  test("AdvectInUpdateInNorthEastNonUniformFlow(") {
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val part = particles.iterator.next
    val newDt = particleAdvector.advectParticles(DT, particles)
    assertEquals("Unexpected age for particle", 0.1, part.getAge, TOL)
    val pos = new Vector2d(part.x, part.y)
    assertTrue("Unexpected new particle position: " + pos,
      LinearUtil.appxVectorsEqual(new Vector2d(2.1021351463835316, 2.5021703194194225), pos, MathUtil.EPS_MEDIUM))
    assertEquals("Timestep unexpectedly changed", DT, newDt, TOL)
  }

  private def createParticles(x: Double, y: Double, grid: Grid) = {
    val particles = new Particles
    particles.addParticle(x, y, grid)
    particles
  }
}
