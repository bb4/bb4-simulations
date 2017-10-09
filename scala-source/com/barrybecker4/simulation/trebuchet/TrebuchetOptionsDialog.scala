// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.simulation.common.ui.NewtonianSimOptionsDialog
import com.barrybecker4.simulation.trebuchet.model.Trebuchet
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import java.awt.event.ActionListener


/**
  * Use this modal dialog to let the user choose from among the
  * different simulator options.
  * @author Bary Becker
  */
class TrebuchetOptionsDialog private[trebuchet](parent: Component, simulator: TrebuchetSimulator) // constructor
  extends NewtonianSimOptionsDialog(parent, simulator) with ActionListener {
  // snake param options controls
  private var counterWeightLeverlLengthField: NumberInput = _
  private var projectileMassField: NumberInput = _
  private var slingLengthField: NumberInput = _
  private var slingLeverLengthField: NumberInput = _
  private var counterWeightMassField: NumberInput = _
  private var slingReleaseAngleField: NumberInput = _

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val trebParamPanel = new JPanel
    trebParamPanel.setLayout(new BoxLayout(trebParamPanel, BoxLayout.Y_AXIS))
    trebParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Trebuchet Parameters"))
    val simulator = getSimulator.asInstanceOf[TrebuchetSimulator]
    val treb = simulator.getTrebuchet
    counterWeightLeverlLengthField = new NumberInput("Counter Weight Lever Length (1.0 short - 3.0 long):  ",
      treb.getCounterWeightLeverLength, "This controls the distance from the fulcrum point to the point " +
        "where the counter weight is attached to the the end of the arm.", 1.0, 3.0, false)
    counterWeightMassField = new NumberInput("Counter Weight Mass  (2.0 light - 60.0 heavy):  ",
      treb.getCounterWeightMass, "This controls mass of main counterweight on the right ",
      2.0, 60.0, false)
    projectileMassField = new NumberInput("Projectile Mass  (0.2 light - 5.0 heavy):  ",
      treb.getProjectileMass, "This controls mass of the projectile thrown.", 0.2, 5.0, false)
    slingLengthField = new NumberInput("Sling Length  (0.2 short - 3.0 long):  ",
      treb.getSlingLength, "This controls the magnitude of the sling.", 0.2, 3.0, false)
    slingLeverLengthField = new NumberInput("Sling Lever Length  (1.0 short - 5.0 long):  ",
      treb.getSlingLeverLength,
      "This controls magnitude of the lever arm from the fulcrum to the sling attachment point.",
      1.0, 5.0, false)
    slingReleaseAngleField = new NumberInput("Sling Release Angle  (0.0 small - PI/2 large):  ",
      treb.getSlingReleaseAngle, "The angle between the sling and the lever arm when the projectile will be released. ",
      0, Math.PI / 2, false)
    trebParamPanel.add(counterWeightLeverlLengthField)
    trebParamPanel.add(counterWeightMassField)
    trebParamPanel.add(projectileMassField)
    trebParamPanel.add(slingLengthField)
    trebParamPanel.add(slingLeverLengthField)
    trebParamPanel.add(slingReleaseAngleField)
    customParamPanel.add(trebParamPanel, BorderLayout.NORTH)
    // start over
    treb.reset()
    customParamPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    // set the snake params
    val simulator = getSimulator.asInstanceOf[TrebuchetSimulator]
    val treb = simulator.getTrebuchet
    treb.reset()
    treb.setCounterWeightLeverLength(counterWeightLeverlLengthField.getValue)
    treb.setCounterWeightMass(counterWeightMassField.getValue)
    treb.setProjectileMass(projectileMassField.getValue)
    treb.setSlingLength(slingLengthField.getValue)
    treb.setSlingLeverLength(slingLeverLengthField.getValue)
    treb.setSlingReleaseAngle(counterWeightMassField.getValue)
    treb.setSlingReleaseAngle(slingReleaseAngleField.getValue)
  }
}