// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagation

import com.barrybecker4.simulation.waveFunctionCollapse.model.{ByteArray, IntArray}
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave.{DX, DY}

import scala.collection.mutable.ListBuffer


object OverlappingPropagatorState {

  /** Whether two `N`×`N` patterns match when `p2` is offset by `(dx, dy)` from `p1`. */
  def agrees(p1: ByteArray, p2: ByteArray, N: Int, dx: Int, dy: Int): Boolean = {
    val xMin = if (dx < 0) 0 else dx
    val xMax = if (dx < 0) dx + N else N
    val yMin = if (dy < 0) 0 else dy
    val yMax = if (dy < 0) dy + N else N

    var matches = true
    var y = yMin
    while (matches && y < yMax) {
      var x = xMin
      while (matches && x < xMax) {
        if (p1(x + N * y) != p2(x - dx + N * (y - dy))) matches = false
        x += 1
      }
      y += 1
    }
    matches
  }
}

class OverlappingPropagatorState(tCounter: Int, patterns: Array[ByteArray], N: Int) extends PropagatorState {

  init()

  private def init(): Unit = {
    val list = ListBuffer[Int]()
    for (d <- 0 until 4) {
      state(d) = Array.fill(tCounter)(null)
      for (t <- 0 until tCounter) {
        list.clear()
        for (t2 <- 0 until tCounter) {
          if (OverlappingPropagatorState.agrees(patterns(t), patterns(t2), N, DX(d), DY(d)))
            list.append(t2)
        }

        state(d)(t) = new IntArray(list.size)
        for (i <- list.indices)
          state(d)(t)(i) = list(i)
      }
    }
  }
}
