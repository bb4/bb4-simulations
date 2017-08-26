// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.ui.util.ColorMap
import java.awt._


/**
  * Colormap for rendering pressures in a meaningful way.
  * @author Barry Becker
  */
object PressureColorMap {
  private val PRESSURE_VALUES = Array(0.0, 0.00001, 0.0001, 0.001, 0.005, 0.01, 0.02, 0.04, .1, 0.5, 1.0)
  private val PRESSURE_COLORS = Array(
    new Color(110, 110, 140, 20),
    new Color(205, 10, 255, 55),
    new Color(50, 50, 255, 80),
    new Color(0, 0, 255, 100),
    new Color(0, 200, 190, 130),
    new Color(0, 255, 0, 140),
    new Color(150, 255, 0, 190),
    new Color(250, 230, 30, 230),
    new Color(255, 150, 0, 230),
    new Color(205, 1, 255, 240),
    new Color(250, 0, 0, 255)
  )
}

final class PressureColorMap() extends ColorMap(PressureColorMap.PRESSURE_VALUES, PressureColorMap.PRESSURE_COLORS)
