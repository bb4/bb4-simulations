// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.model

/**
  * Parameters specific to the input image in the OverlappingModel
  * @param N the size of the N*N kernel used in the Overlapping model
  * @param symmetry the amount of symmetry to use. [1 - 8]:
  *                 1: no symmetry
  *                 2: left - right
  *                 4: left/right, top/down
  *                 6:
  *                 8: 8- fold symmetry
  * @param periodicInput if the input image wraps borders left/right and top/down
  * @param groundParam position of the ground [0, -4]
  */
case class OverlappingImageParams(N: Int, symmetry: Int, periodicInput: Boolean, groundParam: Int) {
  override def toString: String = s"N=$N periodicInput=$periodicInput symmetry=$symmetry ground=$groundParam"
}
