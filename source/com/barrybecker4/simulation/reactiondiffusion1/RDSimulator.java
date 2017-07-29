/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.reactiondiffusion1.algorithm.GrayScottController;
import com.barrybecker4.simulation.reactiondiffusion1.rendering.RDRenderingOptions;

import javax.swing.*;
import java.awt.*;

/**
 * Reaction diffusion simulator.
 * Based on work by Joakim Linde and modified by Barry Becker.
 */
public class RDSimulator extends Simulator {

    private GrayScottController grayScott;
    private RDViewer viewer;
    private RDDynamicOptions rdOptions;

    protected static final double INITIAL_TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    public RDSimulator() {
        super("Reaction Diffusion");
        commonInit();
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        viewer.setUseFixedSize(fixed);
    }

    public boolean getUseFixedSize() {
        return viewer.getUseFixedSize();
    }

    public void setUseOffscreenRendering(boolean use) {
        viewer.setUseOffscreenRendering(use);
    }

    public boolean getUseOffScreenRendering() {
        return viewer.getUseOffScreenRendering();
    }


    @Override
    public void setPaused( boolean bPaused ) {
        super.setPaused(bPaused);
        if (isPaused())   {
            RDProfiler.getInstance().print();
            RDProfiler.getInstance().resetAll();
        }
    }

    private void commonInit() {
        initCommonUI();
        grayScott = new GrayScottController(1, 1);

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        viewer = new RDViewer(grayScott, this);
    }

    @Override
    protected void reset() {
        grayScott.reset();
        rdOptions.reset();
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new RDOptionsDialog(frame, this );
    }


    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep()
    {
        if ( !isPaused() ) {
            RDProfiler.getInstance().startCalculationTime();
            grayScott.timeStep( timeStep_ );
            RDProfiler.getInstance().stopCalculationTime();
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g )
    {
        super.paint(g);

        RDProfiler.getInstance().startRenderingTime();
        viewer.paint(g);
        RDProfiler.getInstance().stopRenderingTime();
    }

    @Override
    public void setScale( double scale ) {}

    @Override
    public double getScale() {
        return 0.01;
    }

    @Override
    public JPanel createDynamicControls() {
        rdOptions = new RDDynamicOptions(grayScott, this);
        return rdOptions;
    }

    public ColorMap getColorMap() {
        return viewer.getColorMap();
    }

    public RDRenderingOptions getRenderingOptions() {
        return viewer.getRenderingOptions();
    }
}
