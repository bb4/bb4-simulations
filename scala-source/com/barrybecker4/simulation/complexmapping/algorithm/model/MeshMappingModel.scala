/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import java.awt.image.BufferedImage
import com.barrybecker4.simulation.complexmapping.algorithm.functions.{ComplexFunction, IdentityFunction}
import com.barrybecker4.simulation.complexmapping.algorithm.{GridRenderer, MeshColorMap}
import com.barrybecker4.ui.util.ColorMap


/**
  * Maintains a rectilinear mesh of points that will get distorted by some function and displayed on an
  * image with a specified viewport. The dimensions of the viewport will be in the
  * same units as the original mesh.
  * @author Barry Becker
  */
case class MeshMappingModel(grid: Grid,
  function: ComplexFunction = IdentityFunction(),
  interpolationVal: Double, colorMap: ColorMap = new MeshColorMap()) {

  private val transformedGrid: Grid = grid.transform(function, interpolationVal)
  private var lastTransformedGrid: Grid = _
  private var lastViewport: Box = _
  private var lastImage: BufferedImage = _

  def getColorMap: ColorMap = colorMap

  /* get the image given specified viewport */
  def getImage(viewport: Box, pixelWidth: Int, pixelHeight: Int): BufferedImage = {
    if (viewport != lastViewport || transformedGrid != lastTransformedGrid) {
      lastViewport = viewport
      lastTransformedGrid = transformedGrid
      val gridRenderer = GridRenderer(transformedGrid, colorMap)
      lastImage = gridRenderer.render(viewport, pixelWidth, pixelHeight)
    }
    lastImage
  }

  def getImage(pixelWidth: Int, pixelHeight: Int): BufferedImage = {
    if (transformedGrid != lastTransformedGrid) {
      lastTransformedGrid = transformedGrid
      val gridRenderer = GridRenderer(transformedGrid, colorMap)
      lastImage = gridRenderer.render(pixelWidth, pixelHeight)
    }
    lastImage
  }

}
