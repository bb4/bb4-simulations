// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.ui

import javax.swing.{ImageIcon, JLabel, JList, ListCellRenderer, SwingConstants}
import java.awt.{Component, Dimension, Font}

class ComboBoxRenderer(images: IndexedSeq[ImageIcon], labels: IndexedSeq[String])
  extends JLabel with ListCellRenderer[Integer] {

  setOpaque(true)
  setHorizontalAlignment(SwingConstants.CENTER)
  setVerticalAlignment(SwingConstants.CENTER)
  private var uhOhFont: Font = _


  /*
   * This method finds the image and text corresponding
   * to the selected value and returns the label, set up
   * to display the text and image.
   */
  override def getListCellRendererComponent(
    list: JList[_ <: Integer], value: Integer, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component = {

    //if (index == -1) return this
    // Get the selected index. (The index param isn't always valid, so just use the value.)
    val selectedIndex = value
    if (isSelected) {
      setBackground(list.getSelectionBackground)
      setForeground(list.getSelectionForeground)
    }
    else {
      setBackground(list.getBackground)
      setForeground(list.getForeground)
    }
    setPreferredSize(new Dimension(150, 25))
    // Set the icon and text.  If icon was null, say so.
    val icon = images(selectedIndex)
    val label = labels(selectedIndex)

    setIcon(icon)
    if (icon != null) {
      setHorizontalAlignment(SwingConstants.LEFT)
      setText(label)
      setFont(list.getFont)
    }
    else setUhOhText(label + " (no image available)", list.getFont)

    this
  }

  //Set the font and text when no image was found.
  protected def setUhOhText(uhOhText: String, normalFont: Font): Unit = {
    if (uhOhFont == null) { // lazily create this font
      uhOhFont = normalFont.deriveFont(Font.ITALIC)
    }
    setFont(uhOhFont)
    setText(uhOhText)
  }
}