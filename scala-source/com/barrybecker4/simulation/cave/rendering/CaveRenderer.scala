// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.rendering

import com.barrybecker4.simulation.cave.model.CaveProcessor
import com.barrybecker4.simulation.common.rendering.bumps.BumpMapper
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap
import javax.vecmath.Vector3d
import java.awt._
import java.awt.image.BufferedImage


/**
  * Draws the 2D cave model by applying the processor to it.
  * @author Barry Becker
  */
object CaveRenderer {
  private val FLOOR_COLOR = new Color(130, 255, 175)
}

class CaveRenderer(val width: Double, val height: Double, var cave: CaveProcessor, var cmap: ColorMap) {

  private val offlineGraphics = new OfflineGraphics(new Dimension(width.toInt, height.toInt), CaveRenderer.FLOOR_COLOR)
  final private val bmapper = new BumpMapper

  def getWidth: Int = width.toInt
  def getHeight: Int = height.toInt
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  /**
    * Draw the floor of the cave.
    * Synchronized so we do not end up calling it multiple times from the same thread until processing is done.
    */
  def render(bumpHeight: Double, specularPct: Double, lightAzymuthAngle: Double,
             lightDescensionAngle: Double): Unit = synchronized {
    val cellWidth = Math.max(1, (width / cave.getWidth).toInt)
    val cellHeight = Math.max(1, (height / cave.getHeight).toInt)
    val lightVector = if (bumpHeight > 0) computeSphericalCoordinateUnitVector(lightAzymuthAngle, lightDescensionAngle)
    else null

    for (i <- 0 until cave.getWidth) {
      for (j <- 0 until cave.getHeight) {
        val xpos = i * cellWidth
        val ypos = j * cellHeight
        val value = cave.getValue(i, j)
        var color = cmap.getColorForValue(value)
        if (bumpHeight > 0) color = bmapper.adjustForLighting(color, i, j, cave, bumpHeight, specularPct, lightVector)
        offlineGraphics.setColor(color)
        offlineGraphics.fillRect(xpos, ypos, cellWidth.toInt, cellHeight.toInt)
      }
    }
  }

  /**
    * See http://mathworld.wolfram.com/SphericalCoordinates.html
    * @param theta azymuthal angle in radians
    * @param phi   angle of descension (pi/2 - elevation) in radians
    * @return unit vector defined by spherical coordinates
    */
  private def computeSphericalCoordinateUnitVector(theta: Double, phi: Double) =
    new Vector3d(Math.cos(theta) * Math.sin(phi), Math.sin(theta) * Math.sin(phi), Math.cos(phi))
}
