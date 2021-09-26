// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagators

import com.barrybecker4.simulation.waveFunctionCollapse.model.IntArray


trait PropagatorState {

  val state: Array[Array[IntArray]] = Array.fill(4)(null)

  def get(x: Int, y: Int): IntArray = state(x)(y)

}
