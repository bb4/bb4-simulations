/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey1.options;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.predprey1.PredPreySimulator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class PredPreyOptionsDialog extends SimulatorOptionsDialog {

    public PredPreyOptionsDialog(Component parent, Simulator simulator) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        PredPreySimulator sim = (PredPreySimulator) getSimulator();

        return panel;
    }

}