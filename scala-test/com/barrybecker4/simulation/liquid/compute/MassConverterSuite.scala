// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model._
import javax.vecmath.Vector2d

import org.junit.Assert.assertEquals
import org.scalatest.FunSuite
import MassConserverSuite.{DT, DIM, EPS, TOL}
import com.barrybecker4.simulation.liquid.model.UniformGrid


/**
  * @author Barry Becker
  */
object MassConserverSuite extends FunSuite {
  /** delta time */
  val DT = 0.1
  val EPS = 0.0000000001
  val DIM = 6
  val TOL = 0.000000000001
}

class MassConserverTest extends FunSuite {

  /** instance under test. */
  private var conserver: MassConserver = _

  test("MassConservationForEmptyCell") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    val cell = grid.getCell(2, 2)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.0, div, TOL)
  }

  test("MassConservationForFullCellInUniformFlow") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    val cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.0, div, EPS)
    assertEquals("Unexpected cell pressure", 0.9, cell.getPressure, EPS)
    verifyResult(cell, neighbors, new Vector2d(1.0, 1.0), new Vector2d(1.0, 1.0))
  }

  test("MassConservationForFullCellInNonUniformFlow") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    val cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.032088732160504604, div, EPS)
    // @@ seems wrong
    assertEquals("Unexpected cell pressure", -7.122183040126151, cell.getPressure, EPS)
    verifyResult(cell, neighbors, new Vector2d(0.49120674102731, 0.49120674102731), new Vector2d(0.49120674102731, 0.49120674102731))
  }

  test("MassConservationForNonUniformFlowHighB") {
    val b0 = 2.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    var cell = grid.getCell(1, 2)
    cell.setU(0.5) // slower from west

    cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.05, div, TOL)
    assertEquals("Unexpected cell pressure", -24.1, cell.getPressure, TOL)
    verifyResult(cell, neighbors, new Vector2d(0.75, 0.75), new Vector2d(0.75, 1.25))
  }

  test("MassConservationForNonUniformFlowSlowWest") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    var cell = grid.getCell(1, 2)
    cell.setU(0.5)
    cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.05, div, EPS)
    assertEquals("Unexpected cell pressure", -11.6, cell.getPressure, EPS)
    verifyResult(cell, neighbors, new Vector2d(0.875, 0.875), new Vector2d(0.625, 1.125))
  }

  test("MassConservationForNonUniformFlowSlowEast") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    val cell = grid.getCell(2, 2)
    cell.setU(0.5) // slower from east

    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.05, div, EPS)
    assertEquals("Unexpected cell pressure", 13.4, cell.getPressure, EPS)
    verifyResult(cell, neighbors, new Vector2d(0.625, 1.125), new Vector2d(0.875, 0.875))
  }

  test("MassConservationForNonUniformFlowSlowSouth") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    var cell = grid.getCell(2, 1)
    cell.setV(0.5) // slower from south

    cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.05, div, EPS)
    assertEquals("Unexpected cell pressure", -11.6, cell.getPressure, EPS)
    verifyResult(cell, neighbors, new Vector2d(0.875, 0.875), new Vector2d(1.125, 0.625))
  }

  test("MassConservationForNonUniformFlowSlowNorth") {
    val b0 = 1.0
    conserver = new MassConserver(b0, DT)
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0))
    val cell = grid.getCell(2, 2)
    cell.setU(0.5)
    cell.setStatus(CellStatus.FULL)
    val neighbors = grid.getNeighbors(2, 2)
    val div = conserver.updateMassConservation(cell, neighbors)
    assertEquals("Unexpected divergence", 0.05, div, EPS)
    assertEquals("Unexpected cell pressure", 13.4, cell.getPressure, EPS)
    verifyResult(cell, neighbors, new Vector2d(0.625, 1.125), new Vector2d(0.875, 0.875))
  }

  private def verifyResult(cell: Cell, neighbors: CellNeighbors, expUV: Vector2d, expBottomLeftUV: Vector2d) = {
    assertEquals("Unexpected UV", expUV, new Vector2d(cell.getU, cell.getV))
    val bottomLeftUV = new Vector2d(neighbors.getLeft.getU, neighbors.getBottom.getV)
    assertEquals("Unexpected bottom left UV", expBottomLeftUV, bottomLeftUV)
  }
}

