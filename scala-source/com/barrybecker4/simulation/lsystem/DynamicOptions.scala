// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem

import com.barrybecker4.simulation.lsystem.model.LSystemModel
import com.barrybecker4.ui.components.TextInput
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  *
  * @author Barry Becker
  */
object DynamicOptions {
  private val NUM_ITERATIONS_SLIDER = "Num Iterations"
  private val ANGLE_SLIDER = "Angle"
  private val SCALE_SLIDER = "Scale"
  private val SCALE_FACTOR_SLIDER = "Scale Factor"
  private val SLIDER_PROPS = Array(
    new SliderProperties(NUM_ITERATIONS_SLIDER, 0, 10, LSystemModel.DEFAULT_ITERATIONS),
    new SliderProperties(ANGLE_SLIDER, 0, 180, LSystemModel.DEFAULT_ANGLE, 100),
    new SliderProperties(SCALE_SLIDER, 0.01, 2.2, LSystemModel.DEFAULT_SCALE, 1000.0),
    new SliderProperties(SCALE_FACTOR_SLIDER, 0.2, 2.0, LSystemModel.DEFAULT_SCALE_FACTOR, 1000.0)
  )
}

class DynamicOptions private[lsystem](var algorithm: LSystemModel, var simulator: LSystemExplorer)
    extends JPanel with ActionListener with KeyListener with SliderGroupChangeListener {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  sliderGroup.addSliderChangeListener(this)
  val checkBoxes: JPanel = createCheckBoxes
  add(createExpressionInput)
  add(Box.createVerticalStrut(10))
  add(sliderGroup)
  add(checkBoxes)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)
  add(createFormulaText)
  private var useFixedSize: JCheckBox = _
  private var expression: TextInput = _
  private var formulaText: JTextArea = _

  private def createExpressionInput = {
    val p = new JPanel(new FlowLayout)
    expression = new TextInput("Expression", LSystemModel.DEFAULT_EXPRESSION, 18)
    expression.addKeyListener(this)
    p.add(expression)
    p
  }

  private def createCheckBoxes = {
    useFixedSize = new JCheckBox("Fixed Size", simulator.getUseFixedSize)
    useFixedSize.addActionListener(this)
    val checkBoxes = new JPanel(new GridLayout(0, 1))
    checkBoxes.add(useFixedSize)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createFormulaText = {
    val textPanel = new JPanel
    textPanel.setLayout(new BorderLayout)
    formulaText = new JTextArea
    formulaText.setEditable(false)
    formulaText.setBackground(getBackground)
    updateFormulaText()
    textPanel.add(formulaText, BorderLayout.CENTER)
    textPanel
  }

  private def updateFormulaText() {
    val text = new StringBuilder(algorithm.getExpression)
    formulaText.setText(text.toString)
  }

  def reset() {sliderGroup.reset()}

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource eq useFixedSize) simulator.setUseFixedSize(useFixedSize.isSelected)
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    if (sliderName == DynamicOptions.NUM_ITERATIONS_SLIDER) algorithm.setNumIterations(value.toInt)
    else if (sliderName == DynamicOptions.ANGLE_SLIDER) algorithm.setAngle(value)
    else if (sliderName == DynamicOptions.SCALE_SLIDER) algorithm.setScale(value)
    else if (sliderName == DynamicOptions.SCALE_FACTOR_SLIDER) algorithm.setScaleFactor(value)
  }

  /**
    * Implement keyListener interface.
    * @param key key that was pressed
    */
  override def keyTyped(key: KeyEvent) {}
  override def keyPressed(key: KeyEvent) {}
  override def keyReleased(key: KeyEvent) {
    val keyChar = key.getKeyChar
    if (keyChar == '\n') {
      algorithm.setExpression(expression.getValue)
      updateFormulaText()
    }
  }
}
