/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer1;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.fractalexplorer1.algorithm.AlgorithmEnum;
import com.barrybecker4.simulation.fractalexplorer1.algorithm.JuliaAlgorithm;
import com.barrybecker4.ui.components.ComplexNumberInput;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Component;

/**
 * @author Barry Becker
 */
public class FractalOptionsDialog extends SimulatorOptionsDialog {

    private static AlgorithmEnum[] ALGORITHM_VALUES = AlgorithmEnum.values();

    private Choice algorithmChoice;

    private ComplexNumberInput juliaSeedField;

    public FractalOptionsDialog(Component parent, Simulator simulator) {
        super(parent, simulator);
    }


    protected JPanel createRenderingParamPanel() {
        return new JPanel();
    }

    @Override
    protected JPanel createCustomParamPanel() {
        setResizable(true);
        JPanel mainPanel = new JPanel(new BorderLayout() );
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Select a fractal algorithm to use:");
        algorithmChoice = createAlgorithmDropdown();

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(algorithmChoice);
        JPanel cnPanel = new JPanel();

        juliaSeedField = new ComplexNumberInput("Julia Seed: ", JuliaAlgorithm.DEFAULT_JULIA_SEED);
        cnPanel.add(juliaSeedField);

        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(cnPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    /**
     * The dropdown menu at the top for selecting an algorithm for solving the puzzle.
     * @return a dropdown/down component.
     */
    private Choice createAlgorithmDropdown() {
        algorithmChoice = new Choice();
        for (AlgorithmEnum algorithm: ALGORITHM_VALUES) {
            algorithmChoice.add(algorithm.getLabel());
        }
        algorithmChoice.select(FractalExplorer.DEFAULT_ALGORITHM_ENUM.ordinal());
        return algorithmChoice;
    }


    @Override
    public FractalExplorer getSimulator() {
        return (FractalExplorer) super.getSimulator();
    }


    protected void ok() {

        // set the common rendering and global options
        FractalExplorer sim = getSimulator();

        int selected = algorithmChoice.getSelectedIndex();
        sim.setAlgorithm(ALGORITHM_VALUES[selected]);
        System.out.println("from field seed = "+ juliaSeedField.getValue());
        sim.setJuliaSeed(juliaSeedField.getValue());

        this.setVisible( false );
        sim.repaint();
    }

}

