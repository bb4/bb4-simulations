/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDColorMap
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDOffscreenRenderer
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDOnscreenRenderer
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions
import java.awt._


/**
  * Reaction diffusion viewer.
  */
object RDViewer {
  private val FIXED_SIZE_DIM = 250
}

class RDViewer private[reactiondiffusion](var grayScott: GrayScottController, var parent: Container) {
  private var oldWidth: Int = this.parent.getWidth
  private var oldHeight: Int = this.parent.getHeight
  private var cmap = new RDColorMap
  private var renderOptions = new RDRenderingOptions
  private var onScreenRenderer: Option[RDOnscreenRenderer] = _
  private var offScreenRenderer: Option[RDOffscreenRenderer] = _
  private var useFixedSize: Boolean = false
  private var useOfflineRendering = false

  private[reactiondiffusion] def getRenderingOptions = renderOptions

  /**
    * @param fixed if true then the render area does not resize automatically.
    */
  def setUseFixedSize(fixed: Boolean): Unit = { useFixedSize = fixed }
  def getUseFixedSize: Boolean = useFixedSize

  def setUseOffscreenRendering(use: Boolean): Unit = {useOfflineRendering = use}
  def getUseOffScreenRendering: Boolean = useOfflineRendering

  def getColorMap: ColorMap = cmap

  def paint(g: Graphics): Unit = {
    checkDimensions()
    val g2 = g.asInstanceOf[Graphics2D]
    getRenderer.get.render(g2)
  }

  /** Sets to new size if needed. */
  private def checkDimensions(): Unit = {
    var w = RDViewer.FIXED_SIZE_DIM
    var h = RDViewer.FIXED_SIZE_DIM
    if (!useFixedSize) {
      w = parent.getWidth
      h = parent.getHeight
    }
    initRenderers(w, h)
  }

  private def initRenderers(w: Int, h: Int): Unit = {
    if (w != oldWidth || h != oldHeight) {
      grayScott.setSize(w, h)
      onScreenRenderer = None
      offScreenRenderer = None
      oldWidth = w
      oldHeight = h
    }
  }

  private def getOffScreenRenderer = {
    if (offScreenRenderer.isEmpty)
      offScreenRenderer = Some(new RDOffscreenRenderer(grayScott.getModel, cmap, renderOptions, parent))
    offScreenRenderer
  }

  private def getOnScreenRenderer = {
    if (onScreenRenderer.isEmpty)
      onScreenRenderer = Some(new RDOnscreenRenderer(grayScott.getModel, cmap, renderOptions))
    onScreenRenderer
  }

  private def getRenderer = if (useOfflineRendering) getOffScreenRenderer else getOnScreenRenderer
}
