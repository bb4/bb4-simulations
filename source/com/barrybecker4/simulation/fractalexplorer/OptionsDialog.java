/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class OptionsDialog extends SimulatorOptionsDialog {

    public OptionsDialog(Component parent, Simulator simulator ) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        FractalExplorer sim = (FractalExplorer) getSimulator();

        return panel;
    }


    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();
        FractalExplorer sim = (FractalExplorer) getSimulator();
    }
}
