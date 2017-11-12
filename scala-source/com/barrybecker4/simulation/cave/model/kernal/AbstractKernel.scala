// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model.kernal

import com.barrybecker4.simulation.cave.model.Cave


/**
  * Looks only at immediate neighbors
  * @author Barry Becker
  */
abstract class AbstractKernel(var cave: Cave) extends Kernel {
  private[kernal] def isOffEdge(x: Int, y: Int) = x < 0 || y < 0 || x >= cave.getWidth || y >= cave.getLength
}
