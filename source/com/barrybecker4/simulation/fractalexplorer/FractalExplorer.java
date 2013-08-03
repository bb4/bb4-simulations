/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.rendering.ModelImage;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm;
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalModel;
import com.barrybecker4.simulation.fractalexplorer.algorithm.MandelbrotAlgorithm;

import javax.swing.*;
import java.awt.*;

/**
 * Interactively explores the Mandelbrot set.
 * @author Barry Becker.
 */
public class FractalExplorer extends Simulator {

    private FractalAlgorithm algorithm_;
    private FractalModel model_;
    private ModelImage modelImage_;
    private DynamicOptions options_;
    private ZoomHandler zoomHandler_;

    private boolean useFixedSize_ = false;

    protected static final double INITIAL_TIME_STEP = 10.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 1;


    public FractalExplorer() {
        super("Fractal Explorer");
        commonInit();
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        useFixedSize_ = fixed;
    }

    public boolean getUseFixedSize() {
        return useFixedSize_;
    }


    private void commonInit() {
        initCommonUI();
        reset();
    }

    @Override
    protected void reset() {

        model_ = new FractalModel();
        modelImage_ = new ModelImage(model_, new FractalColorMap());
        algorithm_ = new MandelbrotAlgorithm(model_);

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        zoomHandler_ = new ZoomHandler(algorithm_);
        this.addMouseListener(zoomHandler_);
        this.addMouseMotionListener(zoomHandler_);

        if (options_ != null) options_.reset();
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
            if (!useFixedSize_) {
                model_.setSize(this.getWidth(), this.getHeight());
            }

            algorithm_.timeStep( timeStep_ );
            modelImage_.updateImage(model_.getLastRow(), model_.getCurrentRow());

        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        super.paint(g);

        Profiler.getInstance().startRenderingTime();
        if (g!=null) {
            g.drawImage(modelImage_.getImage(), 0, 0, null);
        }
        zoomHandler_.render(g, model_.getAspectRatio());
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
        options_ = new DynamicOptions(algorithm_, this);
        return options_;
    }

    public ColorMap getColorMap() {
        return modelImage_.getColorMap();
    }
}
