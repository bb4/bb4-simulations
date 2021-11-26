// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.{ArrayFunction, ErrorFunction}
import com.barrybecker4.math.interpolation.InterpolationMethod
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._


/**
  * @author Barry Becker
  */
class GraphOptionsDialog(parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  /** type of interpolation to use.   */
  private var functionCombo: JComboBox[String] = _
  private var interpolationTypeCombo: JComboBox[InterpolationMethod] = _

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

    interpolationTypeCombo = new JComboBox(cboxModel)
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
    var func: com.barrybecker4.math.function.Function = FunctionType.fromOrdinal(functionCombo.getSelectedIndex).function
    func match {
      case function: ArrayFunction =>
        val method: InterpolationMethod = interpolationTypeCombo.getSelectedItem.asInstanceOf[InterpolationMethod]
        //val f1 = new ArrayFunction(function.functionMap)
        //val f2 = new ArrayFunction(function.functionMap, function.inverseFunctionMap)
        func = new ArrayFunction(function.functionMap, function.inverseFunctionMap, method)
      case errFunc: ErrorFunction => // no interpolation method
      case _ => throw new IllegalArgumentException("Unexpected function type: " + func.getClass.getName)
    }
    simulator.setFunction(func)
  }
}