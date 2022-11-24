// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import java.awt.*
import java.awt.image.BufferedImage
import javax.vecmath.Point2d
import scala.util.Random

object VoronoiModel {
  private val RND = new Random(0)
}

/**
  * Everything we need to know to compute the Voronoi diagram.
  * @author Barry Becker
  */
class VoronoiModel private[algorithm](
  val width: Int, val height: Int,
  var params: PoissonParams, val usePoissonDistribution: Boolean, val connectPoints: Boolean,
  var numPoints: Int, var cmap: ColorMap, rnd: Random = RND) {

  private var grid: PoissonGrid = _
  private var activeList: ActiveList = _
  private var activePoint: Point2d = _

  /** offline rendering is fast  */
  final private var offlineGraphics = new OfflineGraphics(new Dimension(width, height), Color.BLACK)
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get
  def getWidth: Int = width
  def getHeight: Int = height


  def reset(): Unit = synchronized {
    rnd.setSeed(0)
    grid = PoissonGrid(width, height, params.radius)
    //assert(numPoints > params.k, "numPoints " + numPoints + " must be larger than " + params.k)
    activeList = ActiveList(numPoints + params.k)
    println("width = " + width + " height = " + height)

    // Select the initial x0 point.
    activePoint = new Point2d(width * rnd.nextDouble(), height * rnd.nextDouble())
    val activeIndex = grid.addSample(activePoint)
    activeList.addElement(activeIndex)
  }

  /** @param numSteps number of steps to increment each traveler */
  def increment(numSteps: Int): Unit = synchronized {

    var count: Int = 0
    if (activeList == null) {
      println("Warning: activeList null")
    }
    while (!activeList.isEmpty && count < numSteps) {
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
    //println("inc count="+ count + " activeList size = " + activeList.getSize + " num samples = " + grid.getNumSamples)

    // TODO: split out renderer
    var lastPoint = grid.samples(0)
    for (point <- grid.samples) {
      offlineGraphics.setColor(Color.WHITE)
      val xpos = point.x.toInt
      val ypos = point.y.toInt
      if (connectPoints) {
        val xposLast = lastPoint.x.toInt
        val yposLast = lastPoint.y.toInt
        offlineGraphics.drawLine(xposLast, yposLast, xpos, ypos)
      }
      else offlineGraphics.fillCircle(xpos, ypos, 1) // RAD const
      lastPoint = point
    }

  }
}
