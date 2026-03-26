// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.utils

/** Verbose logging for WFC (enable with `-Dwfc.debug=true`). */
object WfcDebug {
  val enabled: Boolean = java.lang.Boolean.getBoolean("wfc.debug")
}
