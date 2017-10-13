/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.graphing1;


import com.barrybecker4.common.math.function.ArrayFunction;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.common.math.interplolation.InterpolationMethod;
import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class GraphOptionsDialog extends SimulatorOptionsDialog {

    /** type of interpolation to use.   */
    private JComboBox<FunctionType> functionCombo;

    /** type of interpolation to use.   */
    private JComboBox<InterpolationMethod> interpolationTypeCombo;

    /**
     * Constructor
     */
    public GraphOptionsDialog( Component parent, Simulator simulator ) {
        super( parent, simulator );
    }

    @Override
    public String getTitle() {
        return "Graph Simulation Configuration";
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        ComboBoxModel<FunctionType> model = new DefaultComboBoxModel<>(FunctionType.values());
        functionCombo = new JComboBox<>(model);
        innerPanel.add(functionCombo);

        interpolationTypeCombo = new JComboBox<>(InterpolationMethod.values());
        innerPanel.add(interpolationTypeCombo);
        interpolationTypeCombo.setSelectedIndex(1);

        JPanel fill = new JPanel();
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(fill, BorderLayout.CENTER);

        return paramPanel;
    }

    @Override
    protected void ok() {
        super.ok();

        GraphSimulator simulator = (GraphSimulator) getSimulator();

        Function func = ((FunctionType) functionCombo.getSelectedItem()).function;
        if (func instanceof ArrayFunction)  {
            ((ArrayFunction)func).setInterpolationMethod(
                    (InterpolationMethod) interpolationTypeCombo.getSelectedItem());
        }

        simulator.setFunction(func);
    }

}