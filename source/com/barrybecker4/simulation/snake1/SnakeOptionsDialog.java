/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1;

import com.barrybecker4.common.math.WaveType;
import com.barrybecker4.simulation.common1.ui.NewtonianSimOptionsDialog;
import com.barrybecker4.simulation.snake1.data.SnakeData;
import com.barrybecker4.simulation.snake1.data.SnakeType;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different snake options.
 *
 * @author Barry Becker
 */
class SnakeOptionsDialog extends NewtonianSimOptionsDialog
                         implements ActionListener {

    /** type of snake to show.   */
    private JComboBox<SnakeType> snakeCombo;

    /** type of snake to show.   */
    private JComboBox<WaveType> waveTypeCombo;

    // snake numeric param options controls
    private NumberInput waveSpeedField;
    private NumberInput waveAmplitudeField;
    private NumberInput wavePeriodField;
    private NumberInput massScaleField;
    private NumberInput springKField;
    private NumberInput springDampingField;

    /** constructor */
    SnakeOptionsDialog(Component parent, SnakeSimulator simulator ) {
        super( parent, simulator );
    }


    @Override
    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel snakeParamPanel = new JPanel();
        snakeParamPanel.setLayout( new BoxLayout(snakeParamPanel, BoxLayout.Y_AXIS ) );
        snakeParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Snake Parameters" ) );

        ComboBoxModel<SnakeType> snakeModel = new DefaultComboBoxModel<>(SnakeType.values());
        snakeCombo = new JComboBox<SnakeType>(snakeModel);
        snakeCombo.setToolTipText("Select a type of snake to show.");

        ComboBoxModel<WaveType> waveModel = new DefaultComboBoxModel<>(WaveType.values());
        waveTypeCombo = new JComboBox<>(waveModel);
        waveTypeCombo.setToolTipText("Select a type of wave form to use for muscle contractions.");

        LocomotionParameters params = ((SnakeSimulator) getSimulator()).getLocomotionParams();

        waveSpeedField =
                new NumberInput("Wave Speed (.001 slow - .9 fast):  ", params.getWaveSpeed(),
                     "This controls the speed at which the force function that travels down the body of the snake",
                     0.001, 0.9, false);
        waveAmplitudeField =
                new NumberInput("Wave Amplitude (.001 small - 2.0 large):  ", params.getWaveAmplitude(),
                    "This controls the amplitude of the force function that travels down the body of the snake",
                    0.001, 0.9, false);
        wavePeriodField =
                new NumberInput("Wave Period (0.5 small - 5.0 large):  ", params.getWavePeriod(),
                    "This controls the period (in number of PI radians) of the force function "
                    + "that travels down the body of the snake", 0.5, 5.0, false);
        massScaleField =
                new NumberInput("Mass Scale (.1 small - 6.0 large):  ", params.getMassScale(),
                    "This controls the overall mass of the snake. A high number indicates that the snake weighs a lot.",
                    0.1, 6.0, false);

        springKField =
                new NumberInput("Spring Stiffness  (0.1 small - 4.0 large):  ", params.getSpringK(),
                    "This controls the stiffness of the springs used to make up the snake's body.",
                    0.1, 4.0, false);

        springDampingField =
                new NumberInput("Spring Damping (.1 small - 4.0 large):  ", params.getSpringDamping(),
                    "This controls how quickly the spring returns to rest once released.",
                    0.1, 4.0, false);

        snakeParamPanel.add(snakeCombo);
        snakeParamPanel.add(waveTypeCombo);
        snakeParamPanel.add(waveSpeedField);
        snakeParamPanel.add(waveAmplitudeField);
        snakeParamPanel.add(wavePeriodField);
        snakeParamPanel.add(massScaleField);
        snakeParamPanel.add(springKField);
        snakeParamPanel.add(springDampingField);

        customParamPanel.add(snakeParamPanel, BorderLayout.NORTH);
        customParamPanel.add(Box.createGlue(), BorderLayout.CENTER);

        return customParamPanel;
    }

    @Override
    protected void ok() {

        super.ok();

        // set the snake params
        SnakeSimulator simulator = (SnakeSimulator) getSimulator();

        LocomotionParameters params = simulator.getLocomotionParams();
        params.setWaveSpeed(waveSpeedField.getValue());
        params.setWaveAmplitude(waveAmplitudeField.getValue());
        params.setWavePeriod(wavePeriodField.getValue());
        params.setMassScale(massScaleField.getValue());
        params.setSpringK(springKField.getValue());
        params.setSpringDamping(springDampingField.getValue());
        params.setWaveType((WaveType)(waveTypeCombo.getSelectedItem()));

        SnakeData snakeData = ((SnakeType) snakeCombo.getSelectedItem()).getData();
        simulator.setSnakeData(snakeData);
    }
}