// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagators

import com.barrybecker4.simulation.waveFunctionCollapse.model.{ByteArray, IntArray}
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave.{DX, DY}

import scala.collection.mutable.ListBuffer


class OverlappingPropagatorState(tCounter: Int, patterns: Array[ByteArray], N: Int) extends PropagatorState {

  init()

  private def init(): Unit = {
    val list = ListBuffer[Int]()
    for (d <- 0 until 4) {
      state(d) = Array.fill(tCounter)(null)
      for (t <- 0 until tCounter) {
        list.clear()
        for (t2 <- 0 until tCounter) {
          if (agrees(patterns(t), patterns(t2), DX(d), DY(d)))
            list.append(t2)
        }

        state(d)(t) = new IntArray(list.size)
        for (i <- list.indices)
          state(d)(t)(i) = list(i)
      }
    }
  }

  private def agrees(p1: ByteArray, p2: ByteArray, dx: Int, dy: Int): Boolean = {
    val xMin = if (dx < 0) 0 else dx
    val xMax = if (dx < 0) dx + N else N
    val yMin = if (dy < 0) 0 else dy
    val yMax = if (dy < 0) dy + N else N

    for (y <- yMin until yMax) {
      for (x <- xMin until xMax) {
        if (p1(x + N * y) != p2(x - dx + N * (y - dy))) return false
      }
    }
    true
  }
}
