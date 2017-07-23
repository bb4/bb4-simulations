/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer1;

import com.barrybecker4.common.math.ComplexNumber;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.simulation.common.rendering.ModelImage;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.fractalexplorer1.algorithm.AlgorithmEnum;
import com.barrybecker4.simulation.fractalexplorer1.algorithm.FractalAlgorithm1;
import com.barrybecker4.simulation.fractalexplorer1.algorithm.FractalModel;
import com.barrybecker4.simulation.fractalexplorer1.algorithm.JuliaAlgorithm;
import com.barrybecker4.ui.util.ColorMap;

import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * Interactively explores the Mandelbrot set.
 * @author Barry Becker.
 */
public class FractalExplorer extends Simulator {

    static final AlgorithmEnum DEFAULT_ALGORITHM_ENUM = AlgorithmEnum.MANDELBROT;

    private FractalAlgorithm1 algorithm;
    private AlgorithmEnum algorithmEnum;
    private ComplexNumber juliaSeed = JuliaAlgorithm.DEFAULT_JULIA_SEED;
    private FractalModel model;
    private ModelImage modelImage;
    private DynamicOptions options;
    private ZoomHandler zoomHandler;
    private FractalColorMap colorMap;

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
        algorithmEnum = DEFAULT_ALGORITHM_ENUM;
        colorMap = new FractalColorMap();
        reset();
    }

    public void setAlgorithm(AlgorithmEnum alg) {
        algorithmEnum = alg;
        reset();
    }

    public void setJuliaSeed(ComplexNumber seed) {
       juliaSeed = seed;
    }

    /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
    public FractalAlgorithm1 getAlgorithm() {
        return algorithm;
    }

    @Override
    protected void reset() {

        model = new FractalModel();
        algorithm = algorithmEnum.createInstance(model);

        // this is a hack. The Options dialog should only know about this seed
        if (algorithm instanceof JuliaAlgorithm) {
            ((JuliaAlgorithm) algorithm).setJuliaSeed(juliaSeed);
        }
        modelImage = new ModelImage(model, colorMap);

        setNumStepsPerFrame(DynamicOptions.DEFAULT_STEPS_PER_FRAME);

        if (zoomHandler != null) {
            this.removeMouseListener(zoomHandler);
            this.removeMouseMotionListener(zoomHandler);
        }
        zoomHandler = new ZoomHandler(algorithm);
        this.addMouseListener(zoomHandler);
        this.addMouseMotionListener(zoomHandler);

        if (options != null) options.reset();
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new FractalOptionsDialog(frame, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return DynamicOptions.INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            if (!useFixedSize_) {
                model.setSize(getWidth(), getHeight());
            }

            algorithm.timeStep( timeStep_ );
            modelImage.updateImage(model.getLastRow(), model.getCurrentRow());
            options.setCoordinates(algorithm.getRange());
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        super.paint(g);

        Profiler.getInstance().startRenderingTime();
        if (g != null) {
            g.drawImage(modelImage.getImage(), 0, 0, null);
        }
        zoomHandler.render(g, model.getAspectRatio());
        options.setCoordinates(algorithm.getRange());
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
        options = new DynamicOptions(this);
        return options;
    }

    public ColorMap getColorMap() {
        return modelImage.getColorMap();
    }
}
