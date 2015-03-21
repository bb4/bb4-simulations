/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.options.ChangePolicy;
import com.barrybecker4.simulation.trading.options.TradingOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;

/**
 * Allow the user to add a change policy from the UI.
 *
 * @author Barry Becker
 */
public class ChangePolicyPanel extends JPanel {

    /** The % market gain/loss necessary to trigger next transaction. */
    private NumberInput thresholdChangePercentField;

    /** Percent of current investment/reserve to sell/buy when threshold reached.  */
    private NumberInput transactPercentField;


    /**
     * constructor
     */
    public ChangePolicyPanel(String threshChangePercentLabel, String transactPercentLabel, ChangePolicy defaultPolicy) {


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        thresholdChangePercentField =
                new NumberInput(threshChangePercentLabel, defaultPolicy.getChangePercent() * 100,
                        "When gain/loss percent happens, execute the specified transaction.",
                        0, 100, false);
        transactPercentField =
                new NumberInput(transactPercentLabel, defaultPolicy.getTransactPercent() * 100,
                        "Percent of current investment/reserve to sell/buy when threshold reached. ",
                        -100, 100, false);

        add(thresholdChangePercentField);
        add(transactPercentField);

        setBorder(BorderFactory.createEtchedBorder());
    }


    ChangePolicy getChangePolicy() {

        return new ChangePolicy(thresholdChangePercentField.getValue() / 100.0, transactPercentField.getValue() / 100.0);
    }

}