// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import javax.vecmath.Point2d


/**
  * the Particle is the base entity in the liquid simulation.
  * Ordinarily we would have setter and getter methods for all the data variables
  * but since this is the lowest level primitive, so performance is tantamount.
  * Ideas:
  *   - Have varying radii for particles (  private double radius_)
  *   - Have different types of liquids and use for coloration (private int materialType_)
  * @param cell the cell that the particle belongs to
  * @author Barry Becker
  */
class Particle(x: Double, y: Double, var cell: Cell) extends Point2d(x, y) {

  private var age = 0.0  // Age of the particle. May be used to color flow by age.

  def setCell(cell: Cell): Unit = {this.cell = cell}
  def getCell: Cell = cell

  /** increment the age of the particle */
  def incAge(timeStep: Double): Unit = { age += timeStep }
  def getAge: Double = age
}
