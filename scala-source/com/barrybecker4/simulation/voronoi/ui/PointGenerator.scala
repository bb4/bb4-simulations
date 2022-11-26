// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.ui

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point

import java.util
import java.util.{ArrayList, List, Random}


class PointGenerator {
  def generatePoints(N: Int): IndexedSeq[Point] = {
    var points: IndexedSeq[Point] = IndexedSeq()
    val rnd = new Random
    for (i <- 0 until N) {
      points :+= new Point(rnd.nextDouble, rnd.nextDouble)
    }
    points
  }
}
