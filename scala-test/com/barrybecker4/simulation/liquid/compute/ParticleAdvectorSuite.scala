// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.math.linear.LinearUtil
import com.barrybecker4.math.MathUtil
import com.barrybecker4.simulation.liquid.model._
import javax.vecmath.Vector2d
import org.junit.jupiter.api.Assertions.{assertEquals, assertTrue}
import ParticleAdvectorSuite._
import org.scalatest.funsuite.AnyFunSuite


/**
  * @author Barry Becker
  */
object ParticleAdvectorSuite {
  /** delta time */
  private val DT = 0.1
  private val DIM = 6
  private val TOL = 0.0000000001
}

class ParticleAdvectorSuite extends AnyFunSuite {

  /** instance under test. */
  private var particleAdvector: ParticleAdvector = _

  test("AdvectInUpdateInAbsenceOfFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val part = particles.iterator.next()
    particleAdvector.advectParticles(DT, particles)
    assertEquals(0.1, part.getAge, TOL, "Unexpected age for particle")
    val pos = new Vector2d(part.x, part.y)
    assertTrue(LinearUtil.appxVectorsEqual(new Vector2d(2.1, 2.5), pos, MathUtil.EPS_MEDIUM),
      "Unexpected new particle position: " + pos)
  }

  test("AdvectInUpdateInNorthEastFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val part = particles.iterator.next()
    val newDt = particleAdvector.advectParticles(DT, particles)
    assertEquals(0.1, part.getAge, TOL, "Unexpected age for particle")
    val pos = new Vector2d(part.x, part.y)
    assertTrue(LinearUtil.appxVectorsEqual(new Vector2d(2.105, 2.505), pos, MathUtil.EPS_MEDIUM),
      "Unexpected new particle position: " + pos)
    assertEquals(DT, newDt, TOL, "Timestep unexpectedly changed")
  }

  test("AdvectInUpdateInNorthEastNonUniformFlow") {
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val part = particles.iterator.next()
    val newDt = particleAdvector.advectParticles(DT, particles)
    assertEquals(0.1, part.getAge, TOL, "Unexpected age for particle")
    val pos = new Vector2d(part.x, part.y)
    assertTrue(LinearUtil.appxVectorsEqual(new Vector2d(2.1021351463835316, 2.5021703194194225), pos, MathUtil.EPS_MEDIUM),
      "Unexpected new particle position: " + pos)
    assertEquals(DT, newDt, TOL, "Timestep unexpectedly changed")
  }

  /** CFL: increment = dt * max|v| must not exceed CELL_SIZE/10; large speed forces halving dt. */
  test("AdvectHalvesTimeStepWhenCFLExceeded") {
    val smallDt = 0.01
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1200.0, 0.0), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val newDt = particleAdvector.advectParticles(smallDt, particles)
    assertEquals(smallDt / 2.0, newDt, TOL, "Expected halved timestep when displacement per step is too large")
  }

  /** When flow is very weak, timestep is doubled (unless max |v| is zero). */
  test("AdvectDoublesTimeStepWhenCFLWellBelowMinimum") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.0001, 0.0001), CellStatus.FULL)
    particleAdvector = new ParticleAdvector(grid)
    val particles = createParticles(2.1, 2.5, grid)
    val newDt = particleAdvector.advectParticles(DT, particles)
    assertEquals(DT * 2.0, newDt, TOL, "Expected doubled timestep for very small particle displacement")
  }

  private def createParticles(x: Double, y: Double, grid: Grid) = {
    val particles = new Particles
    particles.addParticle(x, y, grid)
    particles
  }
}
