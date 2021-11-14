// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import com.barrybecker4.simulation.fluid.model
import com.barrybecker4.simulation.fluid.model.CellProperty
import com.barrybecker4.simulation.fluid.model.CellProperty._


/**
  * Data behind the Fluid.
  * @author Barry Becker
  */
class CellGrid(var dimX: Int, var dimY: Int) {

  var u: TwoDArray = Array.ofDim[Double](dimX + 2, dimY + 2)
  var v: TwoDArray = Array.ofDim[Double](dimX + 2, dimY + 2)
  private[model] var density = Array.ofDim[Double](dimX + 2, dimY + 2)

  addInitialInkDensity()

  def getProperty(prop: CellProperty): TwoDArray = {
    prop match {
      case U => u
      case V => v
      case DENSITY => density
    }
  }

  def setProperty(prop: CellProperty, values: TwoDArray): Unit = {
    prop match {
      case U => u = values
      case V => v = values
      case DENSITY => density = values
    }
  }

  def addInitialInkDensity(): Unit = {
    for (i <- 2 until dimX / 2) {
      for (j <- 2 until dimY / 2) {
        u(i)(j) = 0.01 + (Math.cos(0.4 * i) + Math.sin(0.3 * j)) / 10.0
        v(i)(j) = 0.1 - (Math.sin(0.2 * i) + Math.sin(0.1 * j) / 10.0)
        density(i)(j) = 0.1 - Math.sin((i + j) / 4.0) / 10.0
      }
    }
  }
}
