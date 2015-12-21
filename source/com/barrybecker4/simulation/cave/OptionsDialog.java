// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class OptionsDialog extends SimulatorOptionsDialog {

    public OptionsDialog(Component parent, Simulator simulator) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        CaveExplorer sim = (CaveExplorer) getSimulator();
        return panel;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        CaveExplorer sim = (CaveExplorer) getSimulator();
    }
}
