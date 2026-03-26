// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.Function
import com.barrybecker4.math.interpolation.InterpolationMethod
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import javax.swing.*
import java.awt.*

import scala.compiletime.uninitialized


/**
  * @author Barry Becker
  */
class GraphOptionsDialog(parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var functionCombo: JComboBox[String] = uninitialized
  private var interpolationTypeCombo: JComboBox[InterpolationMethod] = uninitialized

  override def getTitle = "Graph Simulation Configuration"

  override protected def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))
    val model = new DefaultComboBoxModel[String](FunctionType.values.map(_.name))
    functionCombo = new JComboBox[String](model)
    innerPanel.add(functionCombo)
    val cboxModel = new DefaultComboBoxModel[InterpolationMethod]()
    InterpolationMethod.VALUES.foreach(m => cboxModel.addElement(m))

    interpolationTypeCombo = new JComboBox[InterpolationMethod](cboxModel)
    innerPanel.add(interpolationTypeCombo)
    interpolationTypeCombo.setSelectedIndex(1)
    val fill = new JPanel
    paramPanel.add(innerPanel, BorderLayout.NORTH)
    paramPanel.add(fill, BorderLayout.CENTER)
    paramPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[GraphSimulator]
    val base: Function = FunctionType.fromOrdinal(functionCombo.getSelectedIndex).function
    val method = Option(interpolationTypeCombo.getSelectedItem)
      .map(_.asInstanceOf[InterpolationMethod])
      .getOrElse(InterpolationMethod.VALUES(0))
    simulator.setFunction(GraphInterpolation(base, method))
  }
}
