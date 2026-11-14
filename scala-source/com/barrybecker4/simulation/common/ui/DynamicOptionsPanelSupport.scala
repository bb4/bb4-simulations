// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.*


/**
  * Shared Swing setup helpers for DynamicOptions panels shown on the right of simulators.
  * @author Barry Becker
  */
object DynamicOptionsPanelSupport {
  val DefaultPreferredSize: Dimension = new Dimension(300, 300)
  val DefaultFillerHeight: Int = 1000

  def configureVerticalOptionsPanel(
    panel: JPanel,
    preferredSize: Dimension = DefaultPreferredSize
  ): Unit = {
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.setBorder(BorderFactory.createEtchedBorder)
    panel.setPreferredSize(preferredSize)
  }

  def createScrollFiller(width: Int = 1, height: Int = DefaultFillerHeight): JPanel = {
    val fill = new JPanel
    fill.setPreferredSize(new Dimension(width, height))
    fill
  }

  def createCheckBox(
    label: String,
    tooltip: String,
    selected: Boolean,
    listener: ActionListener
  ): JCheckBox = {
    val cb = new JCheckBox(label, selected)
    cb.setToolTipText(tooltip)
    cb.addActionListener(listener)
    cb
  }
}
