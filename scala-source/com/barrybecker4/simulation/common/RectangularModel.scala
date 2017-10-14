// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common

/**
  * Interface for models that exist on a cartesian grid.
  * @author Barry Becker
  */
trait RectangularModel {

  /** @return model value which we will create a color for*/
  def getValue(x: Int, y: Int): Double

  /** @return the row that we have calculated up to.*/
  def getCurrentRow: Int

  /** @return the row that used to be the current row the last time.*/
  def getLastRow: Int
  def getWidth: Int
  def getHeight: Int
}
