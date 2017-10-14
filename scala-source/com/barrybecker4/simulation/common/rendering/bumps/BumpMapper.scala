// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering.bumps

import javax.vecmath.Vector3d
import java.awt._
import BumpMapper._


/**
  * This can be used to apply bump map rendering to a height field.
  * The constants at the top could be parameters to the constructor.
  * @author Barry Becker
  */
object BumpMapper {
  /** The angle at which light will hit the height field surface. Must be normalized to length 1. */
  val DEFAULT_LIGHT_SOURCE_DIR = new Vector3d(1.0, 1.0, 1.0)

  /** the bigger this is the smaller the specular highlight will be. */
  private val DEFAULT_LIGHT_SOURCE_COLOR = Color.WHITE
  private val SPECULAR_HIGHLIGHT_EXP = 4.0
  private val DEFAULT_SPECULAR_PERCENT = 0.1

  DEFAULT_LIGHT_SOURCE_DIR.normalize()
}

class BumpMapper {
  /**
    * Bump mapping is used to adjust the surface model before applying phong shading.
    * @param c       color of surface
    * @param field   a height field used to perturb the normal.
    * @param htScale amount to scale the height field values by
    * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
    */
  def adjustForLighting(c: Color, x: Int, y: Int, field: HeightField, htScale: Double): Color =
    adjustForLighting(c, x, y, field, htScale, DEFAULT_SPECULAR_PERCENT, DEFAULT_LIGHT_SOURCE_DIR)

  /**
    * Bump mapping is used to adjust the surface model before applying Phong shading.
    *
    * @param c           color of surface
    * @param field       a height field used to perturb the normal.
    * @param htScale     amount to scale the height field values by
    * @param specPercent amount of specular highlighting to add to Phong model
    * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
    */
  def adjustForLighting(c: Color, x: Int, y: Int,
                        field: HeightField, htScale: Double, specPercent: Double, lightSourceDir: Vector3d): Color = {
    var xdelta: Double = 0
    var ydelta: Double = 0
    //double centerValue = field.getValue(x, y);
    val posXOffsetValue = field.getValue(Math.min(field.getWidth - 1, x + 1), y)
    val negXOffsetValue = field.getValue(Math.max(0, x - 1), y)
    xdelta = posXOffsetValue - negXOffsetValue
    val posYOffsetValue = field.getValue(x, Math.min(field.getHeight - 1, y + 1))
    val negYOffsetValue = field.getValue(x, Math.max(0, y - 1))
    ydelta = posYOffsetValue - negYOffsetValue
    val xVec = new Vector3d(1.0, 0.0, htScale * xdelta)
    val yVec = new Vector3d(0.0, 1.0, htScale * ydelta)
    val surfaceNormal = new Vector3d
    surfaceNormal.cross(xVec, yVec)
    surfaceNormal.normalize()
    computeColor(c, surfaceNormal, specPercent, lightSourceDir)
  }

  /**
    * Diffuse the surface normal with the light source direction, to determine the shading effect.
    * @param color          base color
    * @param surfaceNormal  surface normal for lighting calculations.
    * @param specPct        amount of specular highlighting to add to phong model
    * @param lightSourceDir normalized unit vector to light source (it must be normalized to length 1)
    * @return color adjusted for lighting.
    */
  private def computeColor(color: Color, surfaceNormal: Vector3d, specPct: Double, lightSourceDir: Vector3d) = {
    val diffuse = Math.abs(surfaceNormal.dot(lightSourceDir))
    val specular = getSpecularExponent(surfaceNormal, specPct, lightSourceDir)
    val cc = color.brighter
    new Color(Math.min(255, cc.getRed * diffuse +
      DEFAULT_LIGHT_SOURCE_COLOR.getRed * specular).toInt, Math.min(255, cc.getGreen * diffuse +
      DEFAULT_LIGHT_SOURCE_COLOR.getGreen * specular).toInt, Math.min(255, cc.getBlue * diffuse +
      DEFAULT_LIGHT_SOURCE_COLOR.getBlue * specular).toInt)
  }

  /**
    * @param specPct amount of specular highlighting to add to Phong model
    * @return specular contribution to add in
    */
  private def getSpecularExponent(surfaceNormal: Vector3d, specPct: Double, lightSourceDir: Vector3d) = {
    if (specPct > 0) {
      val halfAngle = computeHalfAngle(lightSourceDir)
      specPct * Math.pow(Math.abs(surfaceNormal.dot(halfAngle)), SPECULAR_HIGHLIGHT_EXP)
    } else 0
  }

  private def computeHalfAngle(lightSourceDir: Vector3d) = {
    val halfAngle = new Vector3d(0, 0, 1)
    halfAngle.add(lightSourceDir)
    halfAngle.normalize()
    halfAngle
  }
}
