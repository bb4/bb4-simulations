/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.optimization.strategy.OptimizationStrategyType;
import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.liquid1.config.ConfigurationEnum;
import com.barrybecker4.simulation.liquid1.model.LiquidEnvironment;
import com.barrybecker4.simulation.liquid1.rendering.EnvironmentRenderer;
import com.barrybecker4.simulation.liquid1.rendering.RenderingOptions;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Main class for particle liquid simulation.
 *
 * @author Barry Becker
 */
public class LiquidSimulator extends Simulator implements MouseListener {

    private LiquidEnvironment environment;
    private EnvironmentRenderer envRenderer;

    /** These options can be changed while the simulation is running. */
    private LiquidDynamicOptions dynamicOptions;

    /** The initial time step. It may adapt. */
    private static final double INITIAL_TIME_STEP = 0.005;

    private static final Color BG_COLOR = Color.white;

    private static final int NUM_OPT_PARAMS = 3;

    private boolean advectionOnly = false;

    /**
     * Constructor
     */
    public LiquidSimulator() {
        super("Liquid");
        environment = new LiquidEnvironment( ConfigurationEnum.getDefaultValue().getFileName());
        commonInit();
    }

    void loadEnvironment(String configFile) {
        environment = new LiquidEnvironment(configFile);
        environment.setAdvectionOnly(advectionOnly);
        commonInit();
    }

    @Override
    protected void reset() {
        boolean oldPaused = this.isPaused();
        setPaused(true);
        environment.reset();
        commonInit();
        setPaused(oldPaused);
    }

    private void commonInit() {
        initCommonUI();
        envRenderer = new EnvironmentRenderer(environment);

        int s = (int) envRenderer.getScale();
        setPreferredSize(new Dimension( environment.getWidth() * s, environment.getHeight() * s));
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new LiquidOptionsDialog(frame, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    /**
     * @return a new recommended time step change.
     */
    @Override
    public double timeStep() {

        if ( !isPaused() ) {
            timeStep_ = environment.stepForward( timeStep_);
        }
        return timeStep_;
    }

    public LiquidEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void setScale( double scale ) {
        envRenderer.setScale(scale);
    }
    @Override
    public double getScale() {
        return envRenderer.getScale();
    }

    public RenderingOptions getRenderingOptions() {
        return envRenderer.getRenderingOptions();
    }

    public boolean getSingleStepMode() {
        return !isAnimating();
    }

    public void setSingleStepMode(boolean singleStep) {
        setAnimating(!singleStep);
        if (singleStep)  {
            addMouseListener(this);
        }
        else {
            removeMouseListener(this);
        }
    }

    public boolean getAdvectionOnly() {
        return advectionOnly;
    }
    public void setAdvectionOnly(boolean advectOnly) {
        advectionOnly = advectOnly;
        environment.setAdvectionOnly(advectOnly);
    }

    @Override
    public JPanel createDynamicControls() {
        dynamicOptions = new LiquidDynamicOptions(this);
        return dynamicOptions;
    }

    @Override
    public void doOptimization() {

        Optimizer optimizer;
        if (GUIUtil.hasBasicService())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.getHomeDir()+ "performance/liquid/liquid_optimization.txt" );
        Parameter[] params = new Parameter[3];
        ParameterArray paramArray = new NumericParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(OptimizationStrategyType.GENETIC_SEARCH, paramArray, 0.3);
    }

    @Override
    public Color getBackground()  {
        return BG_COLOR;
    }

    @Override
    public void paint( Graphics g ) {
        if (g==null) return;
        Graphics2D g2 = (Graphics2D) g;
        envRenderer.render(g2, getWidth(),  getHeight());
    }

    @Override
    protected String getFileNameBase() {
        return FileUtil.getHomeDir() + "temp/animations/simulation/" + "liquid/liquidFrame";
    }

    public void mouseClicked(MouseEvent e) {
        //System.out.println("mclick timeStep="+ timeStep_ );
        environment.stepForward( timeStep_);
        this.repaint();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}