/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice

import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import DiceOptions._


/**
  * @author Barry Becker
  */
class DiceOptionsDialog private[dice](parent: Component,  simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var numDiceField: NumberInput = _
  private var numSidesField: NumberInput = _

  override def getTitle = "Dice Simulation Configuration"

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    numDiceField = new NumberInput("Number of Dice (1 - 200): ", DEFAULT_NUMBER_OF_DICE,
      "This sets the number of dice to throw on each step of the simulation.", 1, 200, true)
    numSidesField = new NumberInput("Number of Sides on Dice (2 - 100): ", DEFAULT_NUMBER_OF_SIDES,
      "This sets the number of sides on each dice that is thrown.", 1, 100, true)

    innerPanel.add(numDiceField)
    innerPanel.add(numSidesField)
    val fill = new JPanel
    paramPanel.add(innerPanel, BorderLayout.NORTH)
    paramPanel.add(fill, BorderLayout.CENTER)
    paramPanel
  }

  override protected def ok() {
    super.ok()
    val simulator = getSimulator.asInstanceOf[DiceSimulator]
    simulator.setNumDice(numDiceField.getIntValue)
    simulator.setNumSides(numSidesField.getIntValue)
  }
}
