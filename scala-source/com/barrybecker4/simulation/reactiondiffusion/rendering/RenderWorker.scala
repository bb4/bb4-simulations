/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering


/**
  * Fills one column strip of the shared pixel buffer. Does not touch Graphics2D.
  */
final class RenderWorker private[rendering](renderer: RDRenderer) extends Runnable {
  private var minX = 0
  private var maxX = 0

  def setRange(minX: Int, maxX: Int): Unit = {
    this.minX = minX
    this.maxX = maxX
  }

  override def run(): Unit = {
    if (maxX > minX) renderer.fillStrip(minX, maxX)
  }
}
