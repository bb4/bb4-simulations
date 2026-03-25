// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model.kernel

/**
  * Uses a kernel of some sort to count neighbors in a 2D grid.
  * @author Barry Becker
  */
trait Kernel {
  def countNeighbors(x: Int, y: Int): Double
}
