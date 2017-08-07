/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import GrayScottModel._
import java.awt._


/**
  * Data structure for the Gray-Scott algorithm.
  *
  * @author Barry Becker.
  */
object GrayScottModel {
  val K0: Double = 0.079
  val F0: Double = 0.02
  private val INITIAL_U: Double = 0.5
  private val INITIAL_V: Double = 0.25

  /**  Periodic boundary conditions.
    * @return new x value taking into account wrapping boundaries.
    */
  private[algorithm] def getPeriodicXValue(x: Int, max: Int): Int = {
    val xp = x % max
    if (xp < 0) xp + max else xp
  }
}

/**
  * @param width  width of computational space.
  * @param height height of computational space.
  */
final class GrayScottModel(var width: Int, var height: Int)  {
  private var initialK = K0
  private var initialF = F0

  /** concentrations of the 2 chemicals, u and v. */
  private[algorithm] var u: Array[Array[Double]] = _
  private[algorithm] var v: Array[Array[Double]] = _
  private[algorithm] var tmpU: Array[Array[Double]] = _
  private[algorithm] var tmpV: Array[Array[Double]] = _
  private var k: Double = _
  private var f: Double = _
  resetState()

  def getU(x: Int, y: Int): Double = u(x)(y)
  def getV(x: Int, y: Int): Double = v(x)(y)

  def getWidth: Int = width
  def getHeight: Int = height

  def setSize(requestedNewSize: Dimension) {
    this.width = requestedNewSize.width
    this.height = requestedNewSize.height
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
  private[algorithm] def resetState() = {
    f = initialF
    k = initialK

    u = Array.ofDim[Double](width, height)
    v = Array.ofDim[Double](width, height)
    tmpU = Array.ofDim[Double](width, height)
    tmpV = Array.ofDim[Double](width, height)

    stampInitialSquare(0, 0, width, height, 1.0, 0)
    // random square 1
    val w3 = width / 3
    val h3 = height / 3
    stampInitialSquare(w3, h3, w3, h3, INITIAL_U, INITIAL_V)
    // random square 2
    val w7 = width / 7
    val h5 = height / 5
    stampInitialSquare(5 * w7, 3 * h5, w7, h5, INITIAL_U, INITIAL_V)
  }

  /**
    * Place a square of chemicals with the initial concentrations.
    */
  private def stampInitialSquare(startX: Int, startY: Int, width: Int, height: Int, initialU: Double, initialV: Double) = {
    for (x <- 0 until width) {
      for (y <- 0 until height) {
        tmpU(startX + x)(startY + y) = initialU
        tmpV(startX + x)(startY + y) = initialV
      }
    }
  }
}