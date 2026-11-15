// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

/** Map panel pixel size to environment dimensions inside a border inset. */
object EnvSize {

  /**
    * @return None if the panel is not yet sized; otherwise env width/height
    *         filling the area inside `offset` on each side (at least 1×1).
    */
  def envDims(panelWidth: Int, panelHeight: Int, offset: Int): Option[(Int, Int)] = {
    if (panelWidth <= 0 || panelHeight <= 0) None
    else Some(
      (math.max(1, panelWidth - 2 * offset),
       math.max(1, panelHeight - 2 * offset))
    )
  }
}
