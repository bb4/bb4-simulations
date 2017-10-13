// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering.bumps

/**
  * @author Barry Becker
  */
trait HeightField {

  def getWidth: Int
  def getHeight: Int

  /** @return the heigt value for the specified coordinates */
  def getValue(x: Int, y: Int): Double
}
