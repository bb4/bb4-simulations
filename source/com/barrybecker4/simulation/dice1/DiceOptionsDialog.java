/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice1;


import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: 2007
 */
public class DiceOptionsDialog extends SimulatorOptionsDialog {

    /** number of dice to use.   */
    private NumberInput numDiceField;

    /** number of sides on dice.  */
    private NumberInput numSidesField;


    /**
     * Constructor
     */
    DiceOptionsDialog( Component parent, Simulator simulator ) {
        super( parent, simulator );
    }

    @Override
    public String getTitle()
    {
        return "Dice Simulation Configuration";
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        numDiceField =
                new NumberInput("Number of Dice (1 - 200): ", DiceOptions.DEFAULT_NUMBER_OF_DICE,
                                "This sets the number of dice to throw on each step of the simulation.", 1, 200, true);
        numSidesField =
                new NumberInput( "Number of Sides on Dice (2 - 100): ", DiceOptions.DEFAULT_NUMBER_OF_SIDES,
                                  "This sets the number of sides on each dice that is thrown.", 1, 100, true);

        innerPanel.add(numDiceField);
        innerPanel.add(numSidesField);
        JPanel fill = new JPanel();
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(fill, BorderLayout.CENTER);

        return paramPanel;
    }

    @Override
    protected void ok()
    {
        super.ok();

        DiceSimulator simulator = (DiceSimulator) getSimulator();

        simulator.setNumDice(numDiceField.getIntValue());
        simulator.setNumSides(numSidesField.getIntValue());
    }

}
