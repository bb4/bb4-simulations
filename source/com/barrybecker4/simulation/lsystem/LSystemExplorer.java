// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem;

import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.lsystem.model.LSystemModel;

import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * Interactively explores LSystem generated trees based on a user supplied expression.
 * See http://en.wikipedia.org/wiki/L-system
 *
 * @author Barry Becker.
 */
public class LSystemExplorer extends Simulator {

    private LSystemModel algorithm;
    private DynamicOptions options;

    private boolean useFixedSize = false;

    protected static final double INITIAL_TIME_STEP = 10.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    public LSystemExplorer() {
        super("LSystem Tree Explorer");
        commonInit();
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    void setUseFixedSize(boolean fixed) {
        useFixedSize = fixed;
    }

    boolean getUseFixedSize() {
        return useFixedSize;
    }

    private void commonInit() {
        algorithm = new LSystemModel();
        initCommonUI();
        reset();
    }

    @Override
    protected void reset() {

        algorithm.reset();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        if (options != null) {
            options.reset();
        }
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new OptionsDialog(frame, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {

            if (!useFixedSize) {
                algorithm.setSize(this.getWidth(), this.getHeight());
            }
            algorithm.timeStep(timeStep_);
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        if (g == null) return;
        super.paint(g);

        Profiler.getInstance().startRenderingTime();
        g.drawImage(algorithm.getImage(), 0, 0, null);
        Profiler.getInstance().stopRenderingTime();
    }

    @Override
    public void setScale( double scale ) {}

    @Override
    public double getScale() {
        return 0.01;
    }

    @Override
    public JPanel createDynamicControls() {
        options = new DynamicOptions(algorithm, this);
        setPaused(false);
        return options;
    }
}
