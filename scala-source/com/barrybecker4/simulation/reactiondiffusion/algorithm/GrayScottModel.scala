/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import GrayScottModel._
import java.awt._
import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration._


/**
  * Data structure for the Gray-Scott algorithm.
  * @author Barry Becker.
  */
object GrayScottModel {
  /** Feed rate */
  val F0: Double =  0.025

  /** Decay rate */
  val K0: Double =  0.079

  val H0: Double = 1.0

  /** Radius for area of effect when doing manual modification with click/drag brush */
  val DEFAULT_BRUSH_RADIUS = 2
  /** The amount of impact that the brush has. */
  val DEFAULT_BRUSH_STRENGTH = 0.3

  /** Periodic boundary conditions.
    * @return new x value taking into account wrapping boundaries.
    */
  def getPeriodicXValue(x: Int, max: Int): Int = {
    val xp = x % max
    if (xp < 0) xp + max else xp
  }
}

/**
  * @param width  width of computational space.
  * @param height height of computational space.
  */
final class GrayScottModel(var width: Int, var height: Int) extends InitializableGrid {

  var initializer: Initializer = Initializer.DEFAULT_INITIALIZER

  /** concentrations of the 2 chemicals, u and v. */
  var u: Array[Array[Double]] = _
  var v: Array[Array[Double]] = _

  private[algorithm] var tmpU: Array[Array[Double]] = _
  private[algorithm] var tmpV: Array[Array[Double]] = _

  private var k: Double = _
  private var f: Double = _
  resetState()

  def getWidth: Int = width
  def getHeight: Int = height

  def setSize(requestedNewSize: Dimension) {
    this.width = requestedNewSize.width
    this.height = requestedNewSize.height
  }

  def setInitializer(init: Initializer): Unit = {
    initializer = init
    resetState()
  }

  def setF(f: Double) { this.f = f }
  private[algorithm] def getF = f

  def setK(k: Double) {this.k = k}
  private[algorithm] def getK = k

  /** Exchange the u, v fields with the tmp versions.  */
  private[algorithm] def commitChanges()  {
    var temp = tmpU
    tmpU = u
    u = temp
    temp = tmpV
    tmpV = v
    v = temp
  }

  private[algorithm] def getNeighborSum(tmp: Array[Array[Double]], x: Int, y: Int): Double =
    tmp(x + 1)(y) +
      tmp(x - 1)(y) +
      tmp(x)(y + 1) +
      tmp(x)(y - 1)

  private[algorithm] def getEdgeNeighborSum(tmp: Array[Array[Double]], x: Int, y: Int): Double =
    tmp(getPeriodicXValue(x + 1, width))(y) +
      tmp(getPeriodicXValue(x - 1, width))(y) +
      tmp(x)(getPeriodicXValue(y + 1, height)) +
      tmp(x)(getPeriodicXValue(y - 1, height))

  /** Create some initial pattern of chemical that represents the initial condition. */
  private[algorithm] def resetState(): Unit = {
    f = F0
    k = K0

    u = Array.ofDim[Double](width, height)
    v = Array.ofDim[Double](width, height)
    tmpU = Array.ofDim[Double](width, height)
    tmpV = Array.ofDim[Double](width, height)

    initializer.initialize(this, width, height)
    commitChanges()
  }

  def initializePoint(x: Int, y: Int, initialU: Double, initialV: Double): Unit = {
    if (x >= 0 && x < width && y >= 0 && y < height) {
      tmpU(x)(y) = initialU
      tmpV(x)(y) = initialV
      u(x)(y) = initialU
      v(x)(y) = initialV
    }
  }
}
