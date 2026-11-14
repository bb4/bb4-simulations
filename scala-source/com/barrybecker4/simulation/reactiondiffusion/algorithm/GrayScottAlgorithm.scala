/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import scala.compiletime.uninitialized

/**
  * Core Gray-Scott reaction diffusion step with fully periodic boundaries.
  * Based on work by Joakim Linde and modified by Barry Becker.
  */
object GrayScottAlgorithm {
  /** Diffusion rates of the 2 chemicals. */
  private[algorithm] val DU: Double = 2.0e-1
  private[algorithm] val DV: Double = 1.0e-1

  /**
    * Explicit Euler bound for a 5-point Laplacian:
    * `dt * max(DU, DV) / h² < 1/4`.
    */
  def maxStableDt(h: Double): Double = {
    require(h > 0.0, s"h must be positive, got $h")
    0.25 * h * h / math.max(DU, DV)
  }
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
    val f = model.getF
    val k = model.getK
    var x = minX
    while (x < maxX) {
      var y = 0
      while (y < height) {
        val uv = uRead(x, y)
        val vv = vRead(x, y)
        val uv2 = uv * vv * vv
        uWrite.update(x, y, updateU(uv, uRead.laplacian(x, y), uv2, f, dt))
        vWrite.update(x, y, updateV(vv, vRead.laplacian(x, y), uv2, k, dt))
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

  private def updateU(u: Double, lap: Double, uv2: Double, f: Double, dt: Double): Double =
    u + dt * (duDivh2 * lap + f * (1.0 - u) - uv2)

  private def updateV(v: Double, lap: Double, uv2: Double, k: Double, dt: Double): Double =
    v + dt * (dvDivh2 * lap + uv2 - k * v)
}
