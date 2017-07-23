// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway;

import com.barrybecker4.simulation.conway.model.ConwayModel;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Interactively explores Conway's game of life.
 * See https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
 *
 * @author Barry Becker.
 */
public class ConwayExplorer extends Simulator {

    private ConwayModel conwayModel;
    private DynamicOptions options;
    private InteractionHandler handler_;


    public ConwayExplorer() {
        super("Conway's Game of Life Explorer");
        commonInit();
    }

    InteractionHandler getInteractionHandler() {
        return handler_;
    }

    private void commonInit() {
        conwayModel = new ConwayModel();

        initCommonUI();

        handler_ = new InteractionHandler(conwayModel, conwayModel.getScale());
        this.addMouseListener(handler_);
        this.addMouseMotionListener(handler_);
    }

    @Override
    protected void reset() {
        setNumStepsPerFrame(1);
        // remove handlers to void memory leak
        this.removeMouseListener(handler_);
        this.removeMouseMotionListener(handler_);

        if (options != null) {
            options.reset();
        }
        commonInit();
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new OptionsDialog(frame, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return 1;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            conwayModel.setSize(this.getWidth(), this.getHeight());
            conwayModel.timeStep(timeStep_);
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        if (g == null) return;
        super.paint(g);

        Profiler.getInstance().startRenderingTime();

        g.drawImage(conwayModel.getImage(), 0, 0, null);
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
        options = new DynamicOptions(conwayModel, this);
        setPaused(false);
        return options;
    }
}
