// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.math.interpolation.InterpolationMethod

import java.awt.{BorderLayout, Component, FlowLayout}
import javax.swing.*

import scala.compiletime.uninitialized


object FunctionPlotOptionsDialog {

  /** Index of [[method]] in [[InterpolationMethod.VALUES]], or 0 if unknown. */
  def indexOfInterpolationMethod(method: InterpolationMethod): Int = {
    val i = InterpolationMethod.VALUES.indexOf(method)
    if (i >= 0) i else 0
  }
}

/**
  * Shared options UI: function preset combo + interpolation method combo.
  * Subclasses supply preset names, current selection, and apply behavior.
  */
abstract class FunctionPlotOptionsDialog(parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var functionCombo: JComboBox[String] = uninitialized
  private var interpolationCombo: JComboBox[InterpolationMethod] = uninitialized

  /** Display names for the function preset combo (ordinal order). */
  protected def functionNames: Array[String]

  /** Currently selected function preset index. */
  protected def selectedFunctionIndex: Int

  /** Currently selected interpolation method. */
  protected def selectedInterpolationMethod: InterpolationMethod

  /** Apply dialog selections to the simulator. */
  protected def applySelections(functionIndex: Int, method: InterpolationMethod): Unit

  override protected def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    functionCombo = new JComboBox[String](new DefaultComboBoxModel[String](functionNames))
    functionCombo.setSelectedIndex(selectedFunctionIndex.max(0).min(functionNames.length - 1))

    val interpModel = new DefaultComboBoxModel[InterpolationMethod]()
    InterpolationMethod.VALUES.foreach(interpModel.addElement)
    interpolationCombo = new JComboBox[InterpolationMethod](interpModel)
    interpolationCombo.setSelectedIndex(
      FunctionPlotOptionsDialog.indexOfInterpolationMethod(selectedInterpolationMethod)
    )

    innerPanel.add(createChoicePanel("Function: ", functionCombo))
    innerPanel.add(createChoicePanel("Interpolation method: ", interpolationCombo))

    paramPanel.add(innerPanel, BorderLayout.NORTH)
    paramPanel.add(new JPanel, BorderLayout.CENTER)
    paramPanel
  }

  protected def createChoicePanel(label: String, chooser: JComboBox[?]): JPanel = {
    val choicePanel = new JPanel(new FlowLayout)
    choicePanel.add(new JLabel(label))
    choicePanel.add(chooser)
    choicePanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val method = Option(interpolationCombo.getSelectedItem)
      .collect { case m: InterpolationMethod => m }
      .getOrElse(InterpolationMethod.VALUES(0))
    applySelections(functionCombo.getSelectedIndex, method)
  }
}
