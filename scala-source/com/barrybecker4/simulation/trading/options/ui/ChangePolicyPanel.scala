// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import com.barrybecker4.simulation.trading.options.ChangePolicy
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._


/**
  * Allow the user to add a change policy from the UI.
  * @author Barry Becker
  */
class ChangePolicyPanel(val threshChangePercentLabel: String, val transactPercentLabel: String,
                        val defaultPolicy: ChangePolicy) extends JPanel {
  setLayout(new FlowLayout(FlowLayout.LEADING))
  val panel = new JPanel
  panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))

  /** The % market gain/loss necessary to trigger next transaction. */
  private var thresholdChangePercentField =
    new NumberInput(threshChangePercentLabel, defaultPolicy.changePercent * 100,
      "When gain/loss percent happens, execute the specified transaction.", 0, 100, false)

  /** Percent of current investment/reserve to sell/buy when threshold reached.  */
  private var transactPercentField =
    new NumberInput(transactPercentLabel, defaultPolicy.transactPercent * 100,
      "Percent of current investment/reserve to sell/buy when threshold reached. ", -100, 100, false)

  panel.add(thresholdChangePercentField)
  panel.add(transactPercentField)
  panel.setBorder(BorderFactory.createEtchedBorder)
  this.add(panel)

  def getChangePolicy =
    new ChangePolicy(thresholdChangePercentField.getValue / 100.0, transactPercentField.getValue / 100.0)
}