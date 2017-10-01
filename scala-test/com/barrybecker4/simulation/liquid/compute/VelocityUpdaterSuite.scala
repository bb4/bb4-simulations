// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import org.junit.Test
import javax.vecmath.Vector2d

import com.barrybecker4.simulation.liquid.model._
import org.junit.Assert.{assertEquals, assertTrue}
import org.scalatest.FunSuite
import VelocityUpdaterSuite._

/**
  *
  * @author Barry Becker
  */
object VelocityUpdaterSuite {
  /** delta time */
  private val DT = 0.1
  private val GRAVITY = 10.0
  private val VISCOSITY = 0.0
  private val FORCE = new Vector2d(0.0, GRAVITY)
  private val DIM = 6
}

class VelocityUpdaterSuite extends FunSuite {

  /** instance under test. */
  private var velocityUpdater: VelocityUpdater = _

  test("True") {
    assertTrue(true)
  }

/* not working yet
  test("UpdateInAbsenceOfFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.0, 1.0), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInSlightNorthEastFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.1, 1.1), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInSlightNorthEastNonUniformFlow") {
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.057110615322103674, 1.0571106153221037), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInViscousSlightNothEastFlowFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    val viscosity = 10.0
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, viscosity)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.1, 1.1), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInSlightNothEastFlowFlowUpperLeftTweak")  {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    grid.getCell(1, 3).setU(-0.4)
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.1, 1.0997500000000002), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInSlightNorthEastFlowFlowLoweRightTweak")  {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    grid.getCell(3, 1).setV(-0.4)
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.09975, 1.1), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInSlightNorthEastFlowFlowLeftPressureTweak") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    grid.getCell(1, 2).setPressure(ATMOSPHERIC_PRESSURE / 2)
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.10450000000000001, 1.1), new Vector2d(cell.getU, cell.getV))
  }

  test("UpdateInSlightNorthEastFlowFlowLowerPressureTweak") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL)
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    velocityUpdater = new VelocityUpdater
    grid.getCell(2, 1).setPressure(ATMOSPHERIC_PRESSURE / 2)
    velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1), DT, FORCE, VISCOSITY)
    cell.swap()
    assertEquals("Unexpected UV", new Vector2d(0.1, 1.1045), new Vector2d(cell.getU, cell.getV))
  }*/
}

