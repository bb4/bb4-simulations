/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid1.ui;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.optimization.strategy.OptimizationStrategyType;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.fluid1.model.FluidEnvironment;
import com.barrybecker4.simulation.fluid1.rendering.EnvironmentRenderer;
import com.barrybecker4.simulation.fluid1.rendering.RenderingOptions;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Simulate deep water.
 * Based on work by Jos Stam
 * http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 *
 * TODO
 *  Have the grid resize as the panel resizes
 *  Fluid specific parameters
 *   - number of cells (x,y) - auto-calculate the scale size based on the window size.
 */
public class FluidSimulator extends Simulator {

    public static final String CONFIG_FILE = "com/barrybecker4/fluid/initialStateTest.data";

    private FluidEnvironment environment;
    private EnvironmentRenderer envRenderer;
    private InteractionHandler handler;

    public static final int DEFAULT_STEPS_PER_FRAME = 1;

    /** if true it will save all the animation steps to file */
    public static final double INITIAL_TIME_STEP = 0.03;  // initial time step

    private static final Color BG_COLOR = Color.white;
    private RenderingOptions renderOptions;


    public FluidSimulator() {
        this(new FluidEnvironment(250, 200));
    }

    private FluidSimulator( FluidEnvironment environment ) {
        super("Fuild");
        this.environment = environment;
        renderOptions = new RenderingOptions();
        commonInit();
    }

    private void commonInit() {
        initCommonUI();
        envRenderer = new EnvironmentRenderer(environment.getGrid(), renderOptions);
        int scale = (int) envRenderer.getOptions().getScale();
        setPreferredSize(new Dimension( environment.getWidth() * scale, environment.getHeight() * scale));
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        handler = new InteractionHandler(environment.getGrid(), scale);
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new FluidOptionsDialog(frame, this );
    }

    @Override
    public JPanel createDynamicControls() {
        return new FluidDynamicOptions(this);
    }

    public EnvironmentRenderer getRenderer() {
            return envRenderer;
    }

    public FluidEnvironment getEnvironment() {
        return environment;
    }

    public InteractionHandler getInteractionHandler() {
        return handler;
    }

    @Override
    public void setPaused( boolean bPaused ) {
        super.setPaused(bPaused);
        if (isPaused())   {
            Profiler.getInstance().print();
            Profiler.getInstance().resetAll();
        }
    }

    @Override
    protected void reset() {
        // remove the listeners in order to prevent a memory leak.
        this.removeMouseListener(handler);
        this.removeMouseMotionListener(handler);
        environment.reset();
        commonInit();
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    /**
     * @return  a new recommended time step change.
     */
    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            timeStep_ = environment.stepForward( timeStep_);
        }
        return timeStep_;
    }


    @Override
    public void setScale( double scale ) {
        envRenderer.getOptions().setScale(scale);

    }
    @Override
    public double getScale() {
        return envRenderer.getOptions().getScale();
    }

    public void setShowVelocityVectors( boolean show ) {
        envRenderer.getOptions().setShowVelocities(show);
    }
    public boolean getShowVelocityVectors() {
        return envRenderer.getOptions().getShowVelocities();
    }

    @Override
    public void doOptimization() {

        Optimizer optimizer;
        if (GUIUtil.hasBasicService())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.getHomeDir() +"performance/fluid/fluid_optimization.txt" );
        Parameter[] params = new Parameter[3];
        ParameterArray paramArray = new NumericParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(OptimizationStrategyType.GENETIC_SEARCH, paramArray, 0.3);
    }

    @Override
    public Color getBackground() {
        return BG_COLOR;
    }

    @Override
    public void paint( Graphics g ) {
        if (g == null) return;
        Graphics2D g2 = (Graphics2D) g;
        Profiler.getInstance().startRenderingTime();
        envRenderer.render(g2);
        Profiler.getInstance().stopRenderingTime();
    }

    @Override
    protected String getFileNameBase() {
        return FileUtil.getHomeDir() + "temp/animations/simulation/" + "fluid/fluidFrame";
    }
}