/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake;

import com.barrybecker4.common.math.WaveType;
import com.barrybecker4.simulation.common.ui.NewtonianSimOptionsDialog;
import com.barrybecker4.simulation.snake.data.ISnakeData;
import com.barrybecker4.simulation.snake.data.SnakeType;
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
    private JComboBox snakeCombo_;

    /** type of snake to show.   */
    private JComboBox waveTypeCombo_;

    // snake numeric param options controls
    private NumberInput waveSpeedField_;
    private NumberInput waveAmplitudeField_;
    private NumberInput wavePeriodField_;
    private NumberInput massScaleField_;
    private NumberInput springKField_;
    private NumberInput springDampingField_;

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

        ComboBoxModel snakeModel = new DefaultComboBoxModel(SnakeType.values());
        snakeCombo_ = new JComboBox(snakeModel);
        snakeCombo_.setToolTipText("Select a type of snake to show.");

        ComboBoxModel waveModel = new DefaultComboBoxModel(WaveType.values());
        waveTypeCombo_ = new JComboBox(waveModel);
        waveTypeCombo_.setToolTipText("Select a type of wave form to use for muscle contractions.");

        LocomotionParameters params = ((SnakeSimulator) getSimulator()).getLocomotionParams();

        waveSpeedField_ =
                new NumberInput("Wave Speed (.001 slow - .9 fast):  ", params.getWaveSpeed(),
                     "This controls the speed at which the force function that travels down the body of the snake",
                     0.001, 0.9, false);
        waveAmplitudeField_ =
                new NumberInput("Wave Amplitude (.001 small - 2.0 large):  ", params.getWaveAmplitude(),
                    "This controls the amplitude of the force function that travels down the body of the snake",
                    0.001, 0.9, false);
        wavePeriodField_ =
                new NumberInput("Wave Period (0.5 small - 5.0 large):  ", params.getWavePeriod(),
                    "This controls the period (in number of PI radians) of the force function "
                    + "that travels down the body of the snake", 0.5, 5.0, false);
        massScaleField_ =
                new NumberInput("Mass Scale (.1 small - 6.0 large):  ", params.getMassScale(),
                    "This controls the overall mass of the snake. A high number indicates that the snake weighs a lot.",
                    0.1, 6.0, false);

        springKField_ =
                new NumberInput("Spring Stiffness  (0.1 small - 4.0 large):  ", params.getSpringK(),
                    "This controls the stiffness of the springs used to make up the snake's body.",
                    0.1, 4.0, false);

        springDampingField_ =
                new NumberInput("Spring Damping (.1 small - 4.0 large):  ", params.getSpringDamping(),
                    "This controls how quickly the spring returns to rest once released.",
                    0.1, 4.0, false);

        snakeParamPanel.add(snakeCombo_);
        snakeParamPanel.add(waveTypeCombo_);
        snakeParamPanel.add(waveSpeedField_);
        snakeParamPanel.add(waveAmplitudeField_);
        snakeParamPanel.add(wavePeriodField_);
        snakeParamPanel.add(massScaleField_);
        snakeParamPanel.add(springKField_);
        snakeParamPanel.add(springDampingField_);

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
        params.setWaveSpeed(waveSpeedField_.getValue());
        params.setWaveAmplitude(waveAmplitudeField_.getValue());
        params.setWavePeriod(wavePeriodField_.getValue());
        params.setMassScale(massScaleField_.getValue());
        params.setSpringK(springKField_.getValue());
        params.setSpringDamping(springDampingField_.getValue());
        params.setWaveType((WaveType)(waveTypeCombo_.getSelectedItem()));

        ISnakeData snakeData = ((SnakeType)snakeCombo_.getSelectedItem()).getData();
        simulator.setSnakeData(snakeData);
    }
}