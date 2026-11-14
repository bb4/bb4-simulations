// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.{BoxLayout, JCheckBox, JPanel}
import org.scalatest.funsuite.AnyFunSuite


class DynamicOptionsPanelSupportSuite extends AnyFunSuite {

  test("configureVerticalOptionsPanel sets layout, border, and preferred size") {
    val panel = new JPanel
    DynamicOptionsPanelSupport.configureVerticalOptionsPanel(panel)

    assert(panel.getLayout.isInstanceOf[BoxLayout])
    assert(panel.getBorder != null)
    assert(panel.getPreferredSize === DynamicOptionsPanelSupport.DefaultPreferredSize)
  }

  test("configureVerticalOptionsPanel accepts custom preferred size") {
    val panel = new JPanel
    val custom = new Dimension(240, 800)
    DynamicOptionsPanelSupport.configureVerticalOptionsPanel(panel, custom)

    assert(panel.getPreferredSize === custom)
  }

  test("createScrollFiller uses default height and requested width") {
    val fill = DynamicOptionsPanelSupport.createScrollFiller(width = 10)

    assert(fill.getPreferredSize === new Dimension(10, DynamicOptionsPanelSupport.DefaultFillerHeight))
  }

  test("createCheckBox wires label, tooltip, selection, and listener") {
    var fired = false
    val listener: ActionListener = _ => fired = true
    val cb: JCheckBox =
      DynamicOptionsPanelSupport.createCheckBox("Parallel", "tooltip text", selected = true, listener)

    assert(cb.getText === "Parallel")
    assert(cb.getToolTipText === "tooltip text")
    assert(cb.isSelected)
    cb.doClick()
    assert(fired)
  }
}
