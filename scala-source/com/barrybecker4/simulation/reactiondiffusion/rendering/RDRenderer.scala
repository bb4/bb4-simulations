/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common.rendering.ColorRect
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import java.awt.Graphics2D
import scala.collection.parallel.CollectionConverters._


/**
  * Renders the state of the GrayScottController model to the screen.
  * @author Barry Becker
  */
abstract class RDRenderer private[rendering](var model: GrayScottModel,
                                             val cmap: ColorMap, var options: RDRenderingOptions)  {

  private val renderingModel = new RenderingColorModel(model, cmap, options)

  def getColorMap: ColorMap = renderingModel.getColorMap

  /** Draw the model representing the current state of the GrayScottController rd implementation. */
  def render(g2: Graphics2D): Unit = {
    val width = model.getWidth
    val numProcs = Runtime.getRuntime.availableProcessors
    val range = width / numProcs

    val strips = (for (i <- 0 until numProcs) yield {
      if (i < numProcs - 1) {
        val offset = i * range
        (offset, offset + range)
      } else {
        // leftover in the last strip, or all of it if only one processor.
        val currentX = range * (numProcs - 1)
        (currentX, width)
      }
    }).filter { case (minX, maxX) => maxX > minX }

    // Compute ColorRects in parallel (expensive); blit strips sequentially on this thread
    // because Graphics2D / Swing painting are not thread-safe.
    val colorRects =
      if (options.isParallelized)
        strips.par.map { case (minX, maxX) => (minX, getColorRect(minX, maxX)) }.seq
      else
        strips.map { case (minX, maxX) => (minX, getColorRect(minX, maxX)) }

    colorRects.foreach { case (minX, rect) => renderStrip(minX, rect, g2) }
    postRender(g2)
  }

  /** Determine the colors for a rectangular strip of pixels.
    * @return array of colors that will be used to define an image for quick rendering.
    */
  def getColorRect(minX: Int, maxX: Int): ColorRect = {
    val ymax = model.getHeight
    val colorRect = new ColorRect(maxX - minX, ymax)

    for (x <- minX until maxX)
      for (y <- 0 until ymax)
        colorRect.setColor(x - minX, y, renderingModel.getColorForPosition(x, y))
    colorRect
  }

  /** Renders a rectangular strip of pixels. Called only from the render thread after parallel color computation. */
  def renderStrip(minX: Int, rect: ColorRect, g2: Graphics2D): Unit

  /**  Follow up step after rendering. Does nothing by default. */
  protected def postRender(g2: Graphics2D): Unit
}
