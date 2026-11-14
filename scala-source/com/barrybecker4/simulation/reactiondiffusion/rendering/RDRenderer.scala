/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.reactiondiffusion.algorithm.{GrayScottModel, StripPartition}
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import scala.collection.parallel.CollectionConverters._


/**
  * Renders the Gray-Scott model. Workers only write disjoint pixel ranges;
  * the calling thread performs a single blit via [[blit]].
  */
abstract class RDRenderer private[rendering](var model: GrayScottModel,
                                             val cmap: ColorMap, var options: RDRenderingOptions)  {

  private val renderingModel = new RenderingColorModel(model, cmap, options)
  private var pixels: Array[Int] = Array.empty
  private var image: BufferedImage = uninitializedImage()
  private var workers: Array[RenderWorker] = Array.empty

  def getColorMap: ColorMap = renderingModel.getColorMap

  /** Draw the current state of the model. */
  def render(g2: Graphics2D): Unit = {
    val width = model.getWidth
    val height = model.getHeight
    if (width <= 0 || height <= 0) return

    ensureBuffers(width, height)
    val numProcs = math.max(1, Runtime.getRuntime.availableProcessors)
    ensureWorkers(numProcs)
    val ranges = StripPartition.ranges(width, numProcs)
    var i = 0
    while (i < ranges.length) {
      val (minX, maxX) = ranges(i)
      workers(i).setRange(minX, maxX)
      i += 1
    }
    val active = workers.take(ranges.length)
    if (options.isParallelized) active.par.foreach(_.run())
    else active.foreach(_.run())

    image.setRGB(0, 0, width, height, pixels, 0, width)
    blit(image, g2)
  }

  /** Fill columns `[minX, maxX)` into the shared pixel buffer (thread-safe for disjoint strips). */
  private[rendering] def fillStrip(minX: Int, maxX: Int): Unit = {
    val height = model.getHeight
    val width = model.getWidth
    var x = minX
    while (x < maxX) {
      var y = 0
      while (y < height) {
        pixels(y * width + x) = renderingModel.getColorForPosition(x, y).getRGB
        y += 1
      }
      x += 1
    }
  }

  /** Present the completed image (main thread only). */
  protected def blit(image: BufferedImage, g2: Graphics2D): Unit

  private def ensureBuffers(width: Int, height: Int): Unit = {
    val size = width * height
    if (pixels.length != size) pixels = Array.ofDim[Int](size)
    if (image.getWidth != width || image.getHeight != height)
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  }

  private def ensureWorkers(n: Int): Unit = {
    if (workers.length != n)
      workers = Array.tabulate(n)(_ => new RenderWorker(this))
  }

  private def uninitializedImage(): BufferedImage =
    new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
}
