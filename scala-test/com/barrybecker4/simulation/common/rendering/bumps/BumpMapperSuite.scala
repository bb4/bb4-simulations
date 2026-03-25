// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering.bumps

import java.awt.Color
import javax.vecmath.Vector3d
import org.scalatest.funsuite.AnyFunSuite

class BumpMapperSuite extends AnyFunSuite {

  /** Constant height: zero gradient, normal should match unperturbed lighting. */
  private object FlatField extends HeightField {
    override def getWidth: Int = 5
    override def getHeight: Int = 5
    override def getValue(x: Int, y: Int): Double = 1.0
  }

  test("flatHeightFieldProducesBrighterShadeThanBlack") {
    val mapper = new BumpMapper
    val base = Color.DARK_GRAY
    val adjusted = mapper.adjustForLighting(base, 2, 2, FlatField, htScale = 0.5)
    val sum = adjusted.getRed + adjusted.getGreen + adjusted.getBlue
    assert(sum > base.getRed + base.getGreen + base.getBlue)
  }

  test("adjustForLightingWithCustomLightDirectionIsDeterministic") {
    val mapper = new BumpMapper
    val light = new Vector3d(0.0, 0.0, 1.0)
    light.normalize()
    val c1 = mapper.adjustForLighting(Color.RED, 1, 1, FlatField, 1.0, 0.0, light)
    val c2 = mapper.adjustForLighting(Color.RED, 1, 1, FlatField, 1.0, 0.0, light)
    assert(c1.getRGB == c2.getRGB)
  }
}
