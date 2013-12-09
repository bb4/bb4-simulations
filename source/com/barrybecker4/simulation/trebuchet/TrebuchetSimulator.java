/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trebuchet;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.optimization.strategy.OptimizationStrategyType;
import com.barrybecker4.simulation.common.ui.NewtonianSimulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Physically base dynamic simulation of a trebuchet firing.
 * Try simulating using Breve.
 * @author Barry Becker
 */
public class TrebuchetSimulator extends NewtonianSimulator
                                implements ChangeListener {

    private Trebuchet trebuchet_ = null;

    JSlider zoomSlider_;

    private static final int DEFAULT_NUM_STEPS_PER_FRAME = 1;

    // the amount to advance the animation in time for each frame in seconds
    private static final double TIME_STEP = 0.002;

    private static final Color BACKGROUND_COLOR = new Color(253, 250, 253);

    private static final int NUM_PARAMS = 3;


    public TrebuchetSimulator() {
        super("Trebuchet");
        reset();
        this.setPreferredSize(new Dimension( 800, 900));
    }

    public TrebuchetSimulator( Trebuchet trebuchet ) {
        super("Trebuchet");
        commonInit( trebuchet );
    }

    private void commonInit( Trebuchet trebuchet ) {
        trebuchet_ = trebuchet;
        setNumStepsPerFrame(DEFAULT_NUM_STEPS_PER_FRAME);
        this.setBackground(BACKGROUND_COLOR);
        initCommonUI();
        this.render();
    }

    @Override
    protected void reset() {
        final Trebuchet trebuchet = new Trebuchet();
        commonInit( trebuchet );
    }

    @Override
    public Color getBackground() {
        return BACKGROUND_COLOR;
    }

    @Override
    public JPanel createTopControls() {
         JPanel controls = super.createTopControls();

        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new FlowLayout());
        JLabel zoomLabel = new JLabel( " Zoom" );
        zoomSlider_ = new JSlider( JSlider.HORIZONTAL, 15, 255, 200 );
        zoomSlider_.addChangeListener( this );
        zoomPanel.add(zoomLabel);
        zoomPanel .add(zoomSlider_);
        this.add(zoomPanel);

        controls.add(zoomLabel);
        controls.add(zoomSlider_);
        return controls;
    }

    @Override
    public void doOptimization() {
        Optimizer optimizer;
        if (GUIUtil.hasBasicService())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.getHomeDir() +"performance/trebuchet/trebuchet_optimization.txt" );
        Parameter[] params = new Parameter[NUM_PARAMS];
        //params[0] = new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" );
        //params[1] = new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" );
        //params[2] = new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" );
        ParameterArray paramArray = new NumericParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(  OptimizationStrategyType.GENETIC_SEARCH, paramArray, 0.3);
    }

    public int getNumParameters() {
        return NUM_PARAMS;
    }

    @Override
    protected double getInitialTimeStep() {
        return TIME_STEP;
    }


    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new TrebuchetOptionsDialog( frame_, this );
    }


    @Override
    public double timeStep()  {
        if ( !isPaused() ) {
            timeStep_ = trebuchet_.stepForward( timeStep_ );
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        if (g==null) return;
        Graphics2D g2 = (Graphics2D) g;


        g2.setColor( BACKGROUND_COLOR );
        g2.fillRect( 0, 0, (int) getSize().getWidth(), (int)  getSize().getHeight() );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                useAntialiasing_ ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );


        // draw the trebuchet in its current position
        trebuchet_.render( g2 );
    }



    @Override
    public void setScale( double scale ) {
        trebuchet_.setScale(scale);
    }
    @Override
    public double getScale() {
        return trebuchet_.getScale();
    }

    @Override
    public void setShowVelocityVectors( boolean show ) {
        RenderablePart.setShowVelocityVectors(show);
    }
    @Override
    public boolean getShowVelocityVectors() {
        return RenderablePart.getShowVelocityVectors();
    }

    @Override
    public void setShowForceVectors( boolean show ) {
        RenderablePart.setShowForceVectors(show);
    }
    @Override
    public boolean getShowForceVectors() {
        return RenderablePart.getShowForceVectors();
    }

    @Override
    public void setDrawMesh( boolean use ) {
        //trebuchet_.setDrawMesh(use);
    }
    @Override
    public boolean getDrawMesh() {
        //return trebuchet_.getDrawMesh();
        return false;
    }


    @Override
    public void setStaticFriction( double staticFriction ) {
        // do nothing
    }
    @Override
    public double getStaticFriction() {
        // do nothing
        return 0.1;
    }

    @Override
    public void setDynamicFriction( double dynamicFriction ) {
       // do nothing
    }
    @Override
    public double getDynamicFriction() {
        // do nothing
        return 0.01;
    }


    // api for setting trebuchet params  /////////////////////////////////
    public Trebuchet getTrebuchet() {
        return trebuchet_;
    }

    public void stateChanged(ChangeEvent event) {
        Object src = event.getSource();
        if (src == zoomSlider_) {
            double v = (double) zoomSlider_.getValue() / 200.0;
            trebuchet_.setScale(v);
            this.repaint();
        }
    }


    /////////////// the next methods implement the Optimizee interface  /////////////////////////

    /**
     * evaluates the trebuchet's fitness.
     * The measure is purely based on its velocity.
     * If the trebuchet becomes unstable, then 0.0 is returned.
     */
    @Override
    public double evaluateFitness( ParameterArray params ) {

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            // let it run for a while
            ThreadUtil.sleep(1000 + (int) (3000 / (1.0 + 0.2 * ct)));

            ct++;
            //stable = trebuchet_.isStable();
        }
        if ( !stable )   {
            System.out.println( "Trebuchet Sim unstable" );
            return 10000.0;
        }
        else {
            return 1.0/oldVelocity;
        }
    }


}