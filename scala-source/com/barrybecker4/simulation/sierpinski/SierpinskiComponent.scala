// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import com.barrybecker4.ui.components.NumberInput
import com.barrybecker4.ui.sliders.LabeledSlider
import com.barrybecker4.ui.sliders.SliderChangeListener
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import SierpinskiComponent._


/**
  * Contains the area to draw the Sierpinski triangle and UI configuration controls at the top above it.
  * @author Barry Becker
  */
object SierpinskiComponent {
  private val INITIAL_RECURSIVE_DEPTH = 1
  private val MAX_RECURSIVE_DEPTH = 10
}

class SierpinskiComponent() extends JPanel with ActionListener with SliderChangeListener {
  createUI()
  private var sierpinskiPanel: SierpinskiPanel = _
  private var lineWidthSlider: LabeledSlider = _
  private var depthField: NumberInput = _
  private var drawButton: JButton = _

  private def createUI(): Unit = {
    this.setLayout(new BorderLayout)
    sierpinskiPanel = new SierpinskiPanel
    this.add(createControlsPanel, BorderLayout.NORTH)
    this.add(sierpinskiPanel, BorderLayout.CENTER)
  }

  private def createControlsPanel = {
    val controlsPanel = new JPanel(new FlowLayout)
    depthField = new NumberInput("Recursive depth:  ",
      INITIAL_RECURSIVE_DEPTH, "This the amount of detail that will be shown.", 0, MAX_RECURSIVE_DEPTH, true)
    lineWidthSlider = new LabeledSlider("Line Width", SierpinskiRenderer.DEFAULT_LINE_WIDTH, 0.1, 100.0)
    lineWidthSlider.addChangeListener(this)
    drawButton = new JButton("Draw it!")
    drawButton.addActionListener(this)
    controlsPanel.add(depthField)
    controlsPanel.add(lineWidthSlider)
    controlsPanel.add(drawButton)
    controlsPanel
  }

  /**
    * Called when the "draw" button is pressed.
    */
  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource eq drawButton) {
      val depth = Math.min(depthField.getIntValue, MAX_RECURSIVE_DEPTH)
      sierpinskiPanel.setRecursiveDepth(depth)
      sierpinskiPanel.repaint()
    }
  }

  override def sliderChanged(slider: LabeledSlider): Unit = {
    sierpinskiPanel.setLineWidth(slider.getValue.toFloat)
    sierpinskiPanel.repaint()
  }
}