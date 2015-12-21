// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.cave.OptionsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Interactively explores LSystem generated trees based on a user supplied expression.
 * See http://en.wikipedia.org/wiki/L-system
 *
 * @author Barry Becker.
 */
public class CaveExplorer extends Simulator {

    private CaveModel caveModel;
    private DynamicOptions options;

    protected static final double INITIAL_TIME_STEP = 10.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    public CaveExplorer() {
        super("Cave Explorer");
        commonInit();
    }

    private void commonInit() {
        caveModel = new CaveModel();
        initCommonUI();
        reset();
    }

    @Override
    protected void reset() {
        caveModel.reset();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        if (options != null) {
            options.reset();
        }
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new OptionsDialog( frame_, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {

            caveModel.setSize(this.getWidth(), this.getHeight());
            caveModel.timeStep(timeStep_);
            //algorithm_.timeStep(timeStep_);
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        if (g == null) return;
        super.paint(g);

        Profiler.getInstance().startRenderingTime();

        g.drawImage(caveModel.getImage(), 0, 0, null);
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
        options = new com.barrybecker4.simulation.cave.DynamicOptions(caveModel, this);
        setPaused(false);
        return options;
    }
}
