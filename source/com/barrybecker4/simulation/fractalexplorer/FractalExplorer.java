/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.common.math.ComplexNumber;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.rendering.ModelImage;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.fractalexplorer.algorithm.AlgorithmEnum;
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm;
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalModel;
import com.barrybecker4.simulation.fractalexplorer.algorithm.JuliaAlgorithm;
import com.barrybecker4.ui.util.ColorMap;

import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * Interactively explores the Mandelbrot set.
 * @author Barry Becker.
 */
public class FractalExplorer extends Simulator {

    static final AlgorithmEnum DEFAULT_ALGORITHM_ENUM = AlgorithmEnum.MANDELBROT;

    private FractalAlgorithm algorithm_;
    private AlgorithmEnum algorithmEnum_;
    private ComplexNumber juliaSeed_ = JuliaAlgorithm.DEFAULT_JULIA_SEED;
    private FractalModel model_;
    private ModelImage modelImage_;
    private DynamicOptions options_;
    private ZoomHandler zoomHandler_;
    private FractalColorMap colorMap_;

    private boolean useFixedSize_ = false;

    /** Constructor */
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
        algorithmEnum_ = DEFAULT_ALGORITHM_ENUM;
        colorMap_ = new FractalColorMap();
        reset();
    }

    public void setAlgorithm(AlgorithmEnum alg) {
        algorithmEnum_ = alg;
        reset();
    }

    public void setJuliaSeed(ComplexNumber seed) {
       juliaSeed_ = seed;
    }

    /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
    public FractalAlgorithm getAlgorithm() {
        return algorithm_;
    }

    @Override
    protected void reset() {

        model_ = new FractalModel();
        algorithm_ = algorithmEnum_.createInstance(model_);

        // this is a hack. The Options dialog should only know about this seed
        if (algorithm_ instanceof JuliaAlgorithm) {
            ((JuliaAlgorithm) algorithm_).setJuliaSeed(juliaSeed_);
        }
        modelImage_ = new ModelImage(model_, colorMap_);

        setNumStepsPerFrame(DynamicOptions.DEFAULT_STEPS_PER_FRAME);

        if (zoomHandler_ != null) {
            this.removeMouseListener(zoomHandler_);
            this.removeMouseMotionListener(zoomHandler_);
        }
        zoomHandler_ = new ZoomHandler(algorithm_);
        this.addMouseListener(zoomHandler_);
        this.addMouseMotionListener(zoomHandler_);

        if (options_ != null) options_.reset();
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new FractalOptionsDialog( frame_, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return DynamicOptions.INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            if (!useFixedSize_) {
                model_.setSize(getWidth(), getHeight());
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
        if (g != null) {
            g.drawImage(modelImage_.getImage(), 0, 0, null);
        }
        zoomHandler_.render(g, model_.getAspectRatio());
        options_.setCoordinates(algorithm_.getRange());
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
        options_ = new DynamicOptions(this);
        return options_;
    }

    public ColorMap getColorMap() {
        return modelImage_.getColorMap();
    }
}
