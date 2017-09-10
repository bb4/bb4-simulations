// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem1;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Component;
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

        LSystemExplorer sim = (LSystemExplorer) getSimulator();
        return panel;
    }


    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        LSystemExplorer sim = (LSystemExplorer) getSimulator();
    }
}
