package com.barrybecker4.simulation.dungeon.ui

import java.awt.BorderLayout
import java.awt.event.ActionListener
import javax.swing.{JCheckBox, JLabel, JPanel}

object CheckBoxCreator {

  def createCheckBox(label: String, ttip: String, initialValue: Boolean, checkBox: JCheckBox, 
                     listener: ActionListener): JPanel = {
                     
    val checkboxPanel = new JPanel(new BorderLayout())

    checkboxPanel.setToolTipText(ttip)
    val cbLabel = new JLabel(label)
    checkBox.setSelected(initialValue)
    checkBox.addActionListener(listener)

    checkboxPanel.add(cbLabel, BorderLayout.CENTER)
    checkboxPanel.add(checkBox, BorderLayout.EAST)
    checkboxPanel
  }
}
