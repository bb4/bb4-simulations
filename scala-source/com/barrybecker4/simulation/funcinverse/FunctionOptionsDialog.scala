// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._
import com.barrybecker4.math.interpolation.{InterpolationMethod, _}
import FunctionOptionsDialog.{INTERP_METHODS, indexOfInterpolationMethod}

import scala.compiletime.uninitialized


object FunctionOptionsDialog {
  private val INTERP_METHODS = Array(
    ("Linear", LINEAR), ("Cosine", COSINE), ("Cubic", CUBIC), ("Hermite", HERMITE), ("Step", STEP)
  )

  /** Index into [[INTERP_METHODS]] for the given method, or 0 if unknown. */
  private[funcinverse] def indexOfInterpolationMethod(method: InterpolationMethod): Int = {
    val i = INTERP_METHODS.indexWhere(_._2 == method)
    if (i >= 0) i else 0
  }
}

/**
  * @author Barry Becker
  */
class FunctionOptionsDialog(parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  /** type of distribution function to test.   */
  private var functionChoiceField: JComboBox[String] = uninitialized

  /** manner in which to interpolate the function values */
  private var interpolationChoiceField: JComboBox[String] = uninitialized

  override def getTitle = "Function Inverse Configuration"

  override protected def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    val funcPanel = new JPanel(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    val funcInverseSim = getSimulator.asInstanceOf[FunctionInverseSimulator]
    functionChoiceField = new JComboBox[String]()
    functionChoiceField.setModel(
      new DefaultComboBoxModel[String](FunctionType.values.map(_.name))
    )

    interpolationChoiceField = new JComboBox[String]()
    interpolationChoiceField.setModel(
      new DefaultComboBoxModel[String](INTERP_METHODS.map(_._1))
    )

    functionChoiceField.setSelectedIndex(funcInverseSim.getFunctionType.ordinal)
    interpolationChoiceField.setSelectedIndex(
      indexOfInterpolationMethod(funcInverseSim.getInterpolationMethod)
    )

    innerPanel.add(createChoicePanel("Function: ", functionChoiceField))
    innerPanel.add(createChoicePanel("Interpolation method: ", interpolationChoiceField))

    val fill = new JPanel
    funcPanel.add(innerPanel, BorderLayout.NORTH)
    funcPanel.add(fill, BorderLayout.CENTER)
    funcPanel
  }

  private def createChoicePanel(label: String, chooser: JComboBox[String]): JPanel = {
    val choicePanel = new JPanel(new FlowLayout)
    choicePanel.add(new JLabel(label))
    choicePanel.add(chooser)
    choicePanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[FunctionInverseSimulator]
    simulator.setFunction(FunctionType.fromOrdinal(functionChoiceField.getSelectedIndex))
    simulator.setInterpolationMethod(INTERP_METHODS(interpolationChoiceField.getSelectedIndex)._2)
  }
}
