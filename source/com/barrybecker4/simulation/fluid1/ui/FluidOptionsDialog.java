/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid1.ui;

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different simulation options.
 *
 * @author Bary Becker
 */
class FluidOptionsDialog extends SimulatorOptionsDialog
                         implements ActionListener {

    FluidOptionsDialog( Component parent, FluidSimulator simulator ) {
        super( parent, simulator );
    }

    @Override
    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel fluidParamPanel = new JPanel();
        fluidParamPanel.setLayout(new BoxLayout(fluidParamPanel, BoxLayout.Y_AXIS));
        fluidParamPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Liquid Parameters"));

        //FluidSimulator simulator = (FluidSimulator) getSimulator();
        customParamPanel.add(fluidParamPanel, BorderLayout.NORTH);

        return customParamPanel;
    }
}