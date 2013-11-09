/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common.ui;

import com.barrybecker4.ui.animation.AnimationFrame;
import com.barrybecker4.ui.renderers.HistogramRenderer;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Simulates the the generation of a histogram based on
 * some stochastic process.
 *
 * @author Barry Becker
 */
public abstract class DistributionSimulator extends Simulator {

    private static final double TIME_STEP = 1.0;
    private static final int DEFAULT_STEPS_PER_FRAME = 100;

    protected HistogramRenderer histogram_;
    protected int[] data_;

    /** Constructor */
    public DistributionSimulator(String title) {
        super(title);
        commonInit();
    }

    @Override
    protected void reset() {
        initHistogram();
    }

    protected abstract void initHistogram();

    private void commonInit() {
        initCommonUI();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        this.setPreferredSize(new Dimension( 600, 500 ));
    }

    @Override
    protected abstract SimulatorOptionsDialog createOptionsDialog();

    @Override
    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            histogram_.increment(getXPositionToIncrement());
        }
        return timeStep_;
    }

    /**
     * @return An x value to add to the histogram.
     * The histogram itself will convert it to the correct x axis bin location.
     */
    protected abstract double getXPositionToIncrement();


    @Override
    public void paint( Graphics g ) {
        histogram_.setSize(getWidth(), getHeight());
        histogram_.paint(g);
    }

    protected static void runSimulation(DistributionSimulator simulator) {
        simulator.setPaused(false);
        new AnimationFrame( simulator );
    }
}


