// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.liquid.compute.MassConserver
import com.barrybecker4.simulation.liquid.compute.SurfaceVelocityUpdater
import com.barrybecker4.simulation.liquid.compute.VelocityUpdater
import junit.framework.TestCase
import javax.vecmath.Vector2d

import com.barrybecker4.simulation.liquid.model.{Cell, CellBlock}
import org.scalatest.FunSuite
import CellSuite._


/**
  * @author Barry Becker
  */
object CellSuite {
  private val VISCOSITY = 0.001
  private val DT = 0.1
}

class CellSuite extends FunSuite {
  test("CellStatus1") {

    val cb = new CellBlock
    val cell = cb.get(0, 0)
    cb.updateCellStatuses()
    assertResult(true) { cell.isEmpty }
    cell.incParticles()
    cb.updateCellStatuses()
    assertResult(true) { cell.isIsolated }
    cell.incParticles()
    cell.incParticles()
    cb.updateCellStatuses()
    assertResult(true) {cell.isIsolated}
    cb.get(1, 0).incParticles()
    cb.updateCellStatuses()
    assert(cell.isSurface, "unexpected status" + cell.getStatus)
    cb.get(-1, 0).incParticles()
    cb.updateCellStatuses()
    assert(cell.isSurface, "unexpected status" + cell.getStatus)
    cb.get(0, 1).incParticles()
    cb.updateCellStatuses()
    assert(cell.isSurface, "unexpected status" + cell.getStatus)
    cb.get(0, -1).incParticles()
    cb.updateCellStatuses()
    assert(cell.isFull, "unexpected status" + cell.getStatus)
  }

  test("TildeVelocities") {
    val cb = new CellBlock
    val cell = cb.get(0, 0)
    checkTildeVelocities(cb, 0.0, 0.0)
    cell.initializeVelocity(1.0, 0)
    checkTildeVelocities(cb, 1.0, 0.0) // was 1.1, 0.1

    cell.initializeVelocity(0.0, 1.0)
    cell.setPressure(1.0)
    checkTildeVelocities(cb, 0.0, 1.0) // was 0.1, 1.1

    cell.initializeVelocity(1.0, 0)
    cb.setAllCellParticles(5)
    cb.setPressures(1.0)
    cell.setPressure(10.0)
    cb.updateCellStatuses()
    checkTildeVelocities(cb, 1.0, 0.0) //2.1996, 1.0);

    cell.initializeVelocity(1.0, 0)
    cb.setAllCellParticles(5)
    cb.get(0, 1).setPressure(0.8)
    cb.get(1, 0).initializeVelocity(0.6, 0.3)
    cb.updateCellStatuses()
    checkTildeVelocities(cb, 1.0, 0.0) // 2.15316, 1.01253);
  }

  private def checkTildeVelocities(cb: CellBlock, expectedU: Double, expectedV: Double) = {
    val cell = cb.get(0, 0)
    val force = new Vector2d(1, 1)
    val updater = new VelocityUpdater
    updater.updateTildeVelocities(cell, cb.getCenterNeighbors, cb.get(-1, 1), cb.get(1, -1), DT, force, VISCOSITY)
    cell.swap()
    assert((cell.getU == expectedU) && (cell.getV == expectedV),
      "Unxepected values Uip=" + cell.getU + ",  Vjp=" + cell.getV)
  }

  test("MassConservation") {
    val cb = new CellBlock
    val cell = cb.get(0, 0)
    val b = 1.7
    val conserver = new MassConserver(b, DT)
    cell.initializeVelocity(1.0, 0)
    cb.setAllCellParticles(5)
    cb.setPressures(1.0)
    cell.setPressure(2.0)
    cb.updateCellStatuses()
    var divergence = conserver.updateMassConservation(cell, cb.getCenterNeighbors)
    assertResult(0.0) { divergence }

    cell.initializeVelocity(1.0, 0)
    cb.setAllCellParticles(5)
    cb.setPressures(1.0)
    cb.get(1, 0).setPressure(2.0)
    cell.setPressure(1.5)
    cb.updateCellStatuses()
    divergence = conserver.updateMassConservation(cell, cb.getCenterNeighbors)
    assertResult(0.0) { divergence } // 0.1499999999999999
  }

  /** This test dissipateOverflow as well as updateSurfaceVelocities. */
  test("UpdateSurfaceVelocitiesTrivial") {
    val pressure = 1.0
    val cb = new CellBlock
    cb.setPressures(pressure)
    verifySurfaceVelocities(cb, pressure, 1.0, 0.0, 0.0, 0.0, 0.0)
  }

  /** This test dissipateOverflow as well as updateSurfaceVelocities. */
  test("UpdateSurfaceVelocitiesUniformX") {
    val pressure = 1.0
    val cb = new CellBlock
    cb.setPressures(pressure)
    cb.setVelocities(1.0, 0.0)
    verifySurfaceVelocities(cb, pressure, 1.0, 1.0, 1.0, 0.0, 0.0)
  }

  private def verifySurfaceVelocities(cb: CellBlock,
                  pressure: Double, expectedPressure: Double,
                  expRightXVel: Double, expLeftXVel: Double,
                  expTopYVel: Double, expBottomYVel: Double) = {
    val cell = cb.get(0, 0)
    val updater = new SurfaceVelocityUpdater(pressure)
    updater.updateSurfaceVelocities(cell, cb.getCenterNeighbors)
    assertResult(expectedPressure) { cell.getPressure }
    assertResult(expRightXVel) { cell.getU }
    assertResult(expLeftXVel) { cb.get(-1, 0).getU }
    assertResult(expTopYVel) { cell.getV }
    assertResult(expBottomYVel) { cb.get(0, 1).getV }
  }
}
