/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import scala.compiletime.uninitialized

/**
  * Core Gray-Scott reaction diffusion step with fully periodic boundaries.
  * Based on work by Joakim Linde and modified by Barry Becker.
  */
object GrayScottAlgorithm {
  /** Diffusion rates of the 2 chemicals. */
  private val DU: Double = 2.0e-1
  private val DV: Double = 1.0e-1
}

final class GrayScottAlgorithm private[algorithm](model: GrayScottModel)  {
  private var h: Double = uninitialized
  private var duDivh2: Double = uninitialized
  private var dvDivh2: Double = uninitialized

  /** Current grid spacing used for diffusion (`du/h²`, `dv/h²`). */
  private[algorithm] def getH: Double = h

  /**
    * Advance concentrations for columns `[minX, maxX)` using periodic Laplacians.
    * Reads from tmp buffers, writes to display buffers.
    */
  def computeNextTimeStep(minX: Int, maxX: Int, dt: Double): Unit = {
    val uRead = model.tmpU
    val vRead = model.tmpV
    val uWrite = model.uWrite
    val vWrite = model.vWrite
    val height = model.getHeight
    var x = minX
    while (x < maxX) {
      var y = 0
      while (y < height) {
        val uv = uRead(x, y)
        val vv = vRead(x, y)
        val uv2 = uv * vv * vv
        uWrite.update(x, y, calcNew(uv, uRead.laplacian(x, y), duDivh2, useF = true, uv2, dt))
        vWrite.update(x, y, calcNew(vv, vRead.laplacian(x, y), dvDivh2, useF = false, uv2, dt))
        y += 1
      }
      x += 1
    }
  }

  def setH(h: Double): Unit = {
    this.h = h
    val h2 = h * h
    duDivh2 = GrayScottAlgorithm.DU / h2
    dvDivh2 = GrayScottAlgorithm.DV / h2
  }

  private def calcNew(txy: Double, lap: Double, dDivh2: Double, useF: Boolean, uv2: Double, dt: Double): Double = {
    val c = if (useF) model.getF * (1.0 - txy) - uv2
            else uv2 - model.getK * txy
    txy + dt * (dDivh2 * lap + c)
  }
}
