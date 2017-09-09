/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase1;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.henonphase1.algorithm.HenonAlgorithm;

import javax.swing.*;
import java.awt.*;

/**
 * Interactively explores the Henon Phase attractors.
 * See   http://mathworld.wolfram.com/HenonMap.html
 * See   http://www.complexification.net/gallery/machines/henonPhaseDeep/
 *
 * @author Barry Becker.
 */
public class HenonPhaseExplorer extends Simulator {

    private HenonAlgorithm algorithm;
    private DynamicOptions options;

    private boolean useFixedSize = false;

    protected static final double INITIAL_TIME_STEP = 10.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    public HenonPhaseExplorer() {
        super("Henon Phase Explorer");
        commonInit();
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        useFixedSize = fixed;
    }

    public boolean getUseFixedSize() {
        return useFixedSize;
    }

    private void commonInit() {
        algorithm = new HenonAlgorithm();
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
        return options;
    }

    public ColorMap getColorMap() {
        return algorithm.getColorMap();
    }
}
