// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.simulation.common.ui.NewtonianSimOptionsDialog
import com.barrybecker4.simulation.trebuchet.model.Trebuchet
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import scala.compiletime.uninitialized


/**
  * Use this modal dialog to let the user choose from among the
  * different simulator options.
  * @author Bary Becker
  */
class TrebuchetOptionsDialog private[trebuchet](parent: Component, simulator: TrebuchetSimulator) // constructor
  extends NewtonianSimOptionsDialog(parent, simulator) with ActionListener {
  // snake param options controls
  private var counterWeightLeverLengthField: NumberInput = uninitialized
  private var projectileMassField: NumberInput = uninitialized
  private var slingLengthField: NumberInput = uninitialized
  private var slingLeverLengthField: NumberInput = uninitialized
  private var counterWeightMassField: NumberInput = uninitialized
  private var slingReleaseAngleField: NumberInput = uninitialized

  override protected def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val trebParamPanel = new JPanel
    trebParamPanel.setLayout(new BoxLayout(trebParamPanel, BoxLayout.Y_AXIS))
    trebParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Trebuchet Parameters"))
    val simulator = getSimulator.asInstanceOf[TrebuchetSimulator]
    val treb = simulator.getTrebuchet
    val lever = TrebuchetParameters.CounterweightLever
    val mass = TrebuchetParameters.CounterweightMass
    val projectile = TrebuchetParameters.ProjectileMass
    val slingLen = TrebuchetParameters.SlingLength
    val slingLever = TrebuchetParameters.SlingLever
    val release = TrebuchetParameters.ReleaseAngle
    counterWeightLeverLengthField = new NumberInput(
      s"Counter Weight Lever Length (${lever.min} short - ${lever.max} long):  ",
      treb.getCounterWeightLeverLength, "This controls the distance from the fulcrum point to the point " +
        "where the counter weight is attached to the the end of the arm.", lever.min, lever.max, false)
    counterWeightMassField = new NumberInput(
      s"Counter Weight Mass  (${mass.min} light - ${mass.max} heavy):  ",
      treb.getCounterWeightMass, "This controls mass of main counterweight on the right ",
      mass.min, mass.max, false)
    projectileMassField = new NumberInput(
      s"Projectile Mass  (${projectile.min} light - ${projectile.max} heavy):  ",
      treb.getProjectileMass, "This controls mass of the projectile thrown.", projectile.min, projectile.max, false)
    slingLengthField = new NumberInput(
      s"Sling Length  (${slingLen.min} short - ${slingLen.max} long):  ",
      treb.getSlingLength, "This controls the magnitude of the sling.", slingLen.min, slingLen.max, false)
    slingLeverLengthField = new NumberInput(
      s"Sling Lever Length  (${slingLever.min} short - ${slingLever.max} long):  ",
      treb.getSlingLeverLength,
      "This controls magnitude of the lever arm from the fulcrum to the sling attachment point.",
      slingLever.min, slingLever.max, false)
    slingReleaseAngleField = new NumberInput("Sling Release Angle  (0.0 small - PI/2 large):  ",
      treb.getSlingReleaseAngle, "The angle between the sling and the lever arm when the projectile will be released. ",
      release.min, release.max, false)
    trebParamPanel.add(counterWeightLeverLengthField)
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
    treb.setCounterWeightLeverLength(counterWeightLeverLengthField.getValue)
    treb.setCounterWeightMass(counterWeightMassField.getValue)
    treb.setProjectileMass(projectileMassField.getValue)
    treb.setSlingLength(slingLengthField.getValue)
    treb.setSlingLeverLength(slingLeverLengthField.getValue)
    treb.setSlingReleaseAngle(slingReleaseAngleField.getValue)
  }
}