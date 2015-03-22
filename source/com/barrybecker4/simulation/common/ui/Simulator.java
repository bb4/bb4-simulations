/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common.ui;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.ui.animation.AnimationComponent;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Base class for all simulations.
 * Extends from {@code AnimationComponent} because simulation involve
 * showing an animation of a process.
 *
 * @author Barry Becker
 */
public abstract class Simulator extends AnimationComponent
                                implements Optimizee {

    protected SimulatorOptionsDialog optionsDialog_ = null;
    protected static JFrame frame_ = null;

    /** timestep for every step of the animation */
    protected double timeStep_;

    /** whether or not to use anti-aliasing when rendering */
    private boolean useAntialiasing_ = true;

    /**
     * Constructor
     * @param name the name of the simulator (eg Snake, Liquid, or Trebuchet)
     */
    public Simulator(String name) {
        setName(name);
        timeStep_ = getInitialTimeStep();
    }

    protected void initCommonUI() {
        GUIUtil.setCustomLookAndFeel();
    }

    protected abstract double getInitialTimeStep();

    public void setTimeStep( double timeStep )  {
        timeStep_ = timeStep;
    }

    public double getTimeStep() {
        return timeStep_;
    }

    public void setAntialiasing( boolean use ) {
        useAntialiasing_ = use;
    }
    public boolean getAntialiasing() {
        return useAntialiasing_;
    }

    public void setScale( double scale ) {}

    public double getScale() {
        return 1;
    }

    protected GradientButton createOptionsButton() {

        GradientButton button = new GradientButton( "Options" );

        optionsDialog_ = createOptionsDialog();

        button.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {

                optionsDialog_.setLocationRelativeTo( (Component) e.getSource() );
                // pause the snake while the options are open
                final Simulator simulator = optionsDialog_.getSimulator();
                final boolean oldPauseVal = simulator.isPaused();
                simulator.setPaused( true );
                optionsDialog_.showDialog();
                simulator.setPaused( oldPauseVal );
            }
        } );
        return button;
    }

    protected abstract SimulatorOptionsDialog createOptionsDialog();

    /**
     * return to the initial state.
     */
    protected abstract void reset();

    public JPanel createTopControls() {
        JPanel controls = new JPanel();
        controls.add( createStartButton() );
        controls.add( createResetButton() );
        controls.add( createOptionsButton() );
        return controls;
    }

    @Override
    protected String getFileNameBase() {
        return FileUtil.getHomeDir() + "temp/animations/simulation/" + getClass().getName();
    }

    /**
     *
     * @return  a reset button that allows you to restore the initial condition of the simulation.
     */
    protected JButton createResetButton() {

        final JButton resetButton = new JButton( "Reset");
        resetButton.addActionListener( new ActionListener()  {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                reset();
            }
        });
        return resetButton;
    }

    /**
     * Override this to return ui elements that can be used to modify the simulation as it is running.
     */
    public JPanel createDynamicControls() {
        return null;
    }

    @Override
    protected String getStatusMessage() {
        return "frames/second=" + FormatUtil.formatNumber(getFrameRate());
    }


    // the next methods implement the unused methods of the optimizee interface.
    // Simulators must implement evaluateFitness

    public void doOptimization() {
       System.out.println("not implemented for this simulator");
    }

    @Override
    public boolean evaluateByComparison() {
        return false;
    }

    /**
     * part of the Optimizee interface
     */
    @Override
    public double getOptimalFitness() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double compareFitness(ParameterArray params1, ParameterArray params2) {
        return evaluateFitness(params1) - evaluateFitness(params2);
    }

    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the fitness.
     */
    @Override
    public double evaluateFitness( ParameterArray params ) {
        assert false : "not implemented yet";
        return 0.0;
    }
}
