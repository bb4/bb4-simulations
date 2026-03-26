// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import java.awt.Component
import javax.swing.{JDialog, SwingUtilities}


private[ui] object StrategyPanelUi {

  /** Resize the enclosing options dialog after strategy-specific controls change. */
  def repackAncestorDialog(component: Component): Unit = {
    val dlg = SwingUtilities.getAncestorOfClass(classOf[JDialog], component)
    if (dlg != null) dlg.asInstanceOf[JDialog].pack()
  }
}
