/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import java.awt.Color
import com.barrybecker4.ui.util.ColorMap


/**
  * Colormap for log10(|f|): dark near zeros, bright where |f| is large.
  * Values are expected in roughly [-2, 2] after clamping in the renderer.
  */
object MagnitudeColorMap {
  private val MIN = -2.0
  private val MAX = 2.0
  private val RANGE = MAX - MIN

  private val VALUES = Array(
    MIN,
    MIN + 0.25 * RANGE,
    MIN + 0.5 * RANGE,
    MIN + 0.75 * RANGE,
    MAX
  )

  private val COLORS = Array(
    new Color(5, 5, 20),       // near zero
    new Color(20, 40, 120),
    new Color(40, 140, 180),
    new Color(220, 200, 80),
    new Color(255, 250, 230)   // large |f|
  )
}

class MagnitudeColorMap() extends ColorMap(MagnitudeColorMap.VALUES, MagnitudeColorMap.COLORS)


/**
  * Cyclic colormap for arg(f) in [-π, π].
  * @author Barry Becker
  */
object ArgumentColorMap {
  private val MIN = -Math.PI
  private val MAX = Math.PI
  private val RANGE = MAX - MIN

  private val VALUES = Array(
    MIN,
    MIN + 0.25 * RANGE,
    MIN + 0.5 * RANGE,
    MIN + 0.75 * RANGE,
    MAX
  )

  private val COLORS = Array(
    new Color(220, 40, 40),
    new Color(220, 200, 40),
    new Color(40, 180, 80),
    new Color(40, 100, 220),
    new Color(220, 40, 40) // wrap
  )
}

class ArgumentColorMap() extends ColorMap(ArgumentColorMap.VALUES, ArgumentColorMap.COLORS)
