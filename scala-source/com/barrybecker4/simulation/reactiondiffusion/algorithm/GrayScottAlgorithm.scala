/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm


/**
  * This is the core of the Gray-Scott reaction diffusion simulation implementation.
  * Based on an work by Joakim Linde and modified by Barry Becker.
  */
object GrayScottAlgorithm {
  /** We could add scrollbars to scale these */
  private val DU = 2.0e-5
  private val DV = 1.0e-5
}

final class GrayScottAlgorithm private[algorithm](var model: GrayScottModel)  {
  private var duDivh2: Double = .0
  private var dvDivh2: Double = .0

  def computeNextTimeStep(minX: Int, maxX: Int, dt: Double) {
    var uv2 = .0
    val u = model.tmpU
    val v = model.tmpV
    val height = model.getHeight
    for (x <- minX to maxX)
      for (y <- 1 until height) {
        uv2 = u(x)(y) * v(x)(y) * v(x)(y)
        model.u(x)(y) = calcNewCenter(u, x, y, duDivh2, useF = true, uv2, dt)
        model.v(x)(y) = calcNewCenter(v, x, y, dvDivh2, useF = false, uv2, dt)
      }
  }

  def computeNewEdgeValues(dt: Double) {
    val width = model.getWidth
    val height = model.getHeight

    for (x <- 0 until width) {   // top and bottom edges
      calcEdge(x, 0, dt)
      calcEdge(x, height - 1, dt)
    }

    for (y <- 0 until height) {   // left and right edges
      calcEdge(0, y, dt)
      calcEdge(width - 1, y, dt)
    }
  }

  def setH(h: Double): Unit = {
    val h2 = h * h
    duDivh2 = GrayScottAlgorithm.DU / h2
    dvDivh2 = GrayScottAlgorithm.DV / h2
  }

  /** Calculate new values on an edge. */
  private def calcEdge(x: Int, y: Int, dt: Double) = {
    val uv2 = model.tmpU(x)(y) * model.tmpV(x)(y) * model.tmpV(x)(y)
    model.u(x)(y) = calcNewEdge(model.tmpU, x, y, duDivh2, useF =  true, uv2, dt)
    model.v(x)(y) = calcNewEdge(model.tmpV, x, y, dvDivh2, useF = false, uv2, dt)
  }

  /** @return new value for a center point. */
  private def calcNewCenter(tmp: Array[Array[Double]], x: Int, y: Int, dDivh2: Double, useF: Boolean, uv2: Double, dt: Double) = {
    val sum = model.getNeighborSum(tmp, x, y) - 4 * tmp(x)(y)
    calcNewAux(tmp(x)(y), sum, dDivh2, useF, uv2, dt)
  }

  /**  @return new value for an edge point. */
  private def calcNewEdge(tmp: Array[Array[Double]], x: Int, y: Int, dDivh2: Double, useF: Boolean, uv2: Double, dt: Double) = {
    val sum = model.getEdgeNeighborSum(tmp, x, y) - 4 * tmp(x)(y)
    calcNewAux(tmp(x)(y), sum, dDivh2, useF, uv2, dt)
  }

  private def calcNewAux(txy: Double, sum: Double, dDivh2: Double, useF: Boolean, uv2: Double, dt: Double) = {
    val c = if (useF) -uv2 + model.getF * (1.0 - txy)
    else uv2 - model.getK * txy
    txy + dt * (dDivh2 * sum + c)
  }
}
