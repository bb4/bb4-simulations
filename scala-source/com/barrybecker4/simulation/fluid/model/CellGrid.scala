// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import com.barrybecker4.simulation.fluid1.model
import com.barrybecker4.simulation.fluid1.model.CellProperty

//import CellProperty._

/**
  * Data behind the Fluid.
  * @author Barry Becker
  */
class CellGrid(var dimX: Int, var dimY: Int) {
  var u = Array.ofDim[Double](dimX + 2, dimY + 2)
  var v = Array.ofDim[Double](dimX + 2, dimY + 2)
  private[model] var density = Array.ofDim[Double](dimX + 2, dimY + 2)
  addInitialInkDensity()


  def getProperty(prop: CellProperty): Array[Array[Double]] = {
    prop match {
      case model.CellProperty.U => u
      case model.CellProperty.V => v
      case model.CellProperty.DENSITY => density
    }
  }

  def setProperty(prop: CellProperty, values: Array[Array[Double]]): Unit = {
    prop match {
      case model.CellProperty.U => u = values
      case model.CellProperty.V => v = values
      case model.CellProperty.DENSITY => density = values
    }
  }

  def addInitialInkDensity() {
    for (i <- 2 until dimX / 2) {
      for (j <- 2 until dimY / 2) {
        u(i)(j) = 0.01 + (Math.cos(0.4 * i) + Math.sin(0.3 * j)) / 10.0
        v(i)(j) = 0.1 - (Math.sin(0.2 * i) + Math.sin(0.1 * j) / 10.0)
        density(i)(j) = 0.1 - Math.sin((i + j) / 4.0) / 10.0
      }
    }
  }

}
