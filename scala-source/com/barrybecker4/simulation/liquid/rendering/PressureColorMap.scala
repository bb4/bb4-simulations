// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

import com.barrybecker4.ui.util.ColorMap
import java.awt._


/**
  * A colormap for coloring the groups according to how healthy they are.
  * Blue will be healthy, while red will be near dead.
  *
  * @author Barry Becker
  */
object PressureColorMap {

  /** base transparency value. */
  private val CM_TRANS = 20

  /** control point values */
  private val pressureVals = Array(-200000.0, -2000.0, -50.0, -10.0, 0.0, 10.0, 50.0, 2000.0, 200000.0)

  private val pressureColors = Array(
    new Color(0, 0, 250, 255),
    new Color(0, 10, 255, CM_TRANS + 90),
    new Color(0, 100, 255, CM_TRANS + 30),
    new Color(10, 255, 70, CM_TRANS), 
    new Color(250, 250, 250, CM_TRANS),
    new Color(250, 250, 27, CM_TRANS),
    new Color(255, 120, 0, CM_TRANS + 30),
    new Color(255, 0, 0, CM_TRANS + 90),
    new Color(250, 0, 0, 255)
  )
}

/** Our own custom colormap for visualizing pressure values. */
class PressureColorMap private[rendering]()
  extends ColorMap(PressureColorMap.pressureVals, PressureColorMap.pressureColors)