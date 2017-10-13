/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.parameter1;

import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class ParameterOptionsDialog extends SimulatorOptionsDialog {

    /** type of distribution function to test.   */
    private JComboBox<ParameterDistributionType> parameterChoiceField;
    private JCheckBox showRedistribution;

    /**
     * constructor
     */
    ParameterOptionsDialog(Component parent, Simulator simulator ) {
        super( parent, simulator );
        ParameterSimulator psim = (ParameterSimulator) getSimulator();
        showRedistribution.setSelected(psim.isShowRedistribution());
    }

    @Override
    public String getTitle() {
        return "Parameter Simulation Configuration";
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        parameterChoiceField = new JComboBox<>();
        parameterChoiceField.setModel(
                new DefaultComboBoxModel<>(ParameterDistributionType.values()));

        showRedistribution = new JCheckBox("Show Redistribution");
        innerPanel.add(parameterChoiceField);
        innerPanel.add(showRedistribution);
        JPanel fill = new JPanel();
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(fill, BorderLayout.CENTER);

        return paramPanel;
    }

    @Override
    protected void ok() {
        super.ok();

        ParameterSimulator simulator = (ParameterSimulator) getSimulator();
        // set the common rendering and global physics options
        simulator.setParameter(
                ParameterDistributionType.values()[parameterChoiceField.getSelectedIndex()].getParameter());
        simulator.setShowRedistribution(showRedistribution.isSelected());
    }

}
