// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.poisson.placement

import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.PoissonParams
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import scala.util.Random


/**
  * Everything we need to know to compute the Voronoi diagram.
  *
  * @author Barry Becker
  */
class PoissonPlacement(
    val width: Int, val height: Int, margin: Int,
    var params: PoissonParams, var numPoints: Int, rnd: Random) extends PlacementMethod {

  private var grid: PoissonGrid = _
  private var activeList: ActiveList = _
  private var activePoint: Point = _

  rnd.setSeed(0)
  grid = PoissonGrid(width, height, margin, params.radius)

  //assert(numPoints > params.k, "numPoints " + numPoints + " must be larger than " + params.k)
  activeList = ActiveList(numPoints + params.k)
  println("width = " + width + " height = " + height)

  // Select the initial x0 point.
  activePoint = new Point(width * rnd.nextDouble(), height * rnd.nextDouble())
  private val activeIndex = grid.addSample(activePoint)
  activeList.addElement(activeIndex)


  def getSamples: IndexedSeq[Point] = grid.samples

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    var count: Int = 0
    
    while (!activeList.isEmpty && count < numPoints) {
      // get random index, generate k points around it, add one of the grid if possible, else delete it.
      val index = activeList.removeRandomElement()
      assert(index >= 0)
      val newIndex = grid.addNewElementIfPossible(index, params.k)
      if (newIndex >= 0) {
        activeList.addElement(newIndex)
        activeList.addElement(index) // add it back
      }
      count += 1
    }
  }

}
