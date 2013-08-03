/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common.ui;

import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public abstract class SimulatorOptionsDialog extends OptionsDialog {

    /** the options get set directly on the snake simulator object that is passed in */
    private Simulator simulator_;

    // rendering option controls
    private JCheckBox antialiasingCheckbox_;
    private JCheckBox recordAnimationCheckbox_;

    // animation param options controls
    private NumberInput timeStepField_;
    private NumberInput numStepsPerFrameField_;
    private NumberInput scaleField_;

    private static final int MAX_NUM_STEPS_PER_FRAME = 10000;

    // bottom buttons
    private GradientButton startButton_ = new GradientButton();

    // constructor
    public SimulatorOptionsDialog( Component parent, Simulator simulator ) {
        super( parent );
        simulator_ = simulator;
        showContent();
    }


    public Simulator getSimulator() {
        return simulator_;
    }

    @Override
    protected JComponent createDialogContent()  {
        setResizable( true );
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

        JPanel renderingParamPanel = createRenderingParamPanel();
        JPanel globalPhysicalParamPanel = createGlobalPhysicalParamPanel();
        JPanel customParamPanel = createCustomParamPanel();

        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.add( "Rendering", renderingParamPanel );
        tabbedPanel.setToolTipTextAt( 0, "Change the rendering options for the simulation" );
        if (globalPhysicalParamPanel != null) {
            tabbedPanel.add( "Animation", globalPhysicalParamPanel );
            tabbedPanel.setToolTipTextAt( 0,
                    "Change the animation and physical constants controlling the simulation" );
        }

        tabbedPanel.add( "Custom", customParamPanel );
        tabbedPanel.setToolTipTextAt( tabbedPanel.getTabCount()-1,
                "Change the custom options for the simulation" );
        tabbedPanel.setSelectedComponent(customParamPanel);

        mainPanel.add( tabbedPanel, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        return mainPanel;
    }


    protected JCheckBox createCheckBox(String label, String ttip, boolean initialValue) {
        JCheckBox cb = new JCheckBox(label, initialValue);
        cb.setToolTipText(ttip);
        cb.addActionListener(this);
        return cb;
    }

    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( startButton_, "Done", "Use these selections when running the simulation." );
        initBottomButton(cancelButton, "Cancel", "Resume the current simulation without changing the options" );

        buttonsPanel.add( startButton_ );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    @Override
    public String getTitle() {
        return "Simulation Configuration";
    }

    private JPanel createRenderingParamPanel() {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout( new BorderLayout() );

        JPanel togglesPanel = new JPanel();
        togglesPanel.setLayout( new BoxLayout( togglesPanel, BoxLayout.Y_AXIS ) );
        togglesPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Toggle Options" ) );

        JPanel textInputsPanel = new JPanel();
        textInputsPanel.setLayout( new BoxLayout( textInputsPanel, BoxLayout.Y_AXIS ) );
        textInputsPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Animation Options" ) );

        antialiasingCheckbox_ = new JCheckBox("Use Antialiasing", simulator_.getAntialiasing());
        antialiasingCheckbox_.setToolTipText( "this toggle the use of antialising when rendering lines." );
        antialiasingCheckbox_.addActionListener( this );
        togglesPanel.add( antialiasingCheckbox_ );

        addAdditionalToggles(togglesPanel) ;

        recordAnimationCheckbox_ = new JCheckBox( "Record Animation Frames", simulator_.getRecordAnimation());
        recordAnimationCheckbox_.setToolTipText( "Record each animation frame to a unique file" );
        recordAnimationCheckbox_.addActionListener( this );
        togglesPanel.add( recordAnimationCheckbox_ );

        timeStepField_ =
                new NumberInput("Time Step (.001 slow - .9 fast but unstable):  ",  simulator_.getTimeStep(),
                                "This controls the size of the numerical intergration steps",
                                0.001, 0.9, false);
        numStepsPerFrameField_ =
                new NumberInput("Num Steps Per Frame (1 slow but smooth - "+MAX_NUM_STEPS_PER_FRAME+" (fast but choppy):  ",
                                simulator_.getNumStepsPerFrame(),
                               "This controls the number of the numerical intergration steps per animation frame",
                               1, MAX_NUM_STEPS_PER_FRAME, true);

        textInputsPanel.add( timeStepField_ );
        textInputsPanel.add( numStepsPerFrameField_ );

        scaleField_ =
                new NumberInput( "Geometry Scale (1.0 = standard size):  ", simulator_.getScale(),
                                 "This controls the size of the objects in the simulation",
                                 0.01, 1000, false);
        scaleField_.setEnabled( false );
        textInputsPanel.add( scaleField_ );

        paramPanel.add( togglesPanel, BorderLayout.CENTER );
        paramPanel.add( textInputsPanel, BorderLayout.SOUTH );

        return paramPanel;
    }

    /**
     * override if you want to add more toggles.
     */
    protected void addAdditionalToggles(JPanel togglesPanel) {
    }

    /**
     * override if you want this panel of options for your simulation.
     */
    protected JPanel createGlobalPhysicalParamPanel() {
        return null;
    }

    /**
     * For custom parameters that don't fall in other categories.
     */
    protected abstract JPanel createCustomParamPanel();

    protected void ok() {

        // set the common rendering and global options
        simulator_.setAntialiasing( antialiasingCheckbox_.isSelected() );
        simulator_.setRecordAnimation( recordAnimationCheckbox_.isSelected() );

        simulator_.setTimeStep( timeStepField_.getValue() );
        simulator_.setNumStepsPerFrame( numStepsPerFrameField_.getIntValue() );
        simulator_.setScale( scaleField_.getValue() );

        this.setVisible( false );
        simulator_.repaint();
    }

    @Override
    public void actionPerformed( ActionEvent e )  {

        Object source = e.getSource();

        if ( source == startButton_ ) {
            ok();
        }
        else if ( source == cancelButton) {
            cancel();
        }
    }
}