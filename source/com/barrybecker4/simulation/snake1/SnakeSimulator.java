/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.types.DoubleParameter;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.optimization.strategy.OptimizationStrategyType;
import com.barrybecker4.simulation.common1.rendering.BackgroundGridRenderer;
import com.barrybecker4.simulation.common1.ui.NewtonianSimulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.snake1.data.SnakeData;
import com.barrybecker4.simulation.snake1.data.LongSnakeData;
import com.barrybecker4.simulation.snake1.rendering.RenderingParameters;
import com.barrybecker4.simulation.snake1.rendering.SnakeRenderer;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * Simulates the motion of a snake.
 */
public class SnakeSimulator extends NewtonianSimulator {

    /** the amount to advance the animation in time for each frame in seconds. */
    protected static final int NUM_STEPS_PER_FRAME = 200;

    private static final Parameter[] PARAMS = {
            new DoubleParameter( LocomotionParameters.WAVE_SPEED, 0.0001, 0.02, "wave speed" ),
            new DoubleParameter( LocomotionParameters.WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" ),
            new DoubleParameter( LocomotionParameters.WAVE_PERIOD, 0.5, 9.0, "wave period" ),
    };

    private static final ParameterArray INITIAL_PARAMS = new NumericParameterArray(PARAMS);

    private Snake snake = new Snake(new LongSnakeData());
    private SnakeUpdater updater = new SnakeUpdater();

    /** change in center of the snake between time steps */
    private Point2d oldCenter;

    /** the overall distance the the snake has travelled so far. */
    private Vector2d distance = new Vector2d( 0.0, 0.0 );

    /** magnitude of the snakes velocity vector */
    private double velocity = 0;

    /** initial time step */
    protected static final double INITIAL_TIME_STEP = 0.2;

    // size of the background grid
    // note the ground should move, not the snake so that the snake always remains visible.
    private static final int XDIM = 12;
    private static final int YDIM = 10;
    private static final double CELL_SIZE = 80.0;
    private static final Color GRID_COLOR = new Color( 0, 0, 60, 100 );

    private Color gridColor = GRID_COLOR;

    private RenderingParameters renderParams = new RenderingParameters();
    private SnakeRenderer renderer;


    public SnakeSimulator() {
        super("Snake");
        commonInit();
    }

    public SnakeSimulator( SnakeData snakeData ) {
        super("Snake");
        setSnakeData(snakeData);
    }

    @Override
    protected void reset() {
        snake.reset();
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    public void setSnakeData(SnakeData snakeData) {
        snake.setData(snakeData);
        commonInit();
    }

    private void commonInit() {
        oldCenter = snake.getCenter();
        renderer = new SnakeRenderer(renderParams);
        setNumStepsPerFrame(NUM_STEPS_PER_FRAME);

        this.setPreferredSize(new Dimension( (int) (CELL_SIZE * XDIM), (int) (CELL_SIZE * YDIM)) );
        initCommonUI();
    }

    public LocomotionParameters getLocomotionParams() {
        return updater.getLocomotionParams();
    }

    @Override
    public void setScale( double scale ) {
        renderParams.setScale(scale);
    }

    @Override
    public double getScale() {
        return renderParams.getScale();
    }

    @Override
    public void setShowVelocityVectors(boolean show) {
        renderParams.setShowVelocityVectors(show);
    }
    @Override
    public boolean getShowVelocityVectors() {
        return renderParams.getShowVelocityVectors();
    }

    @Override
    public void setShowForceVectors( boolean show ) {
        renderParams.setShowForceVectors(show);
    }
    @Override
    public boolean getShowForceVectors() {
        return renderParams.getShowForceVectors();
    }

    @Override
    public void setDrawMesh( boolean use ) {
        renderParams.setDrawMesh(use);
    }
    @Override
    public boolean getDrawMesh() {
        return renderParams.getDrawMesh();
    }

    @Override
    public void setStaticFriction( double staticFriction ) {
        updater.getLocomotionParams().setStaticFriction( staticFriction );
    }
    @Override
    public double getStaticFriction() {
        return updater.getLocomotionParams().getStaticFriction();
    }

    @Override
    public void setDynamicFriction( double dynamicFriction ) {
        updater.getLocomotionParams().setDynamicFriction(dynamicFriction);
    }
    @Override
    public double getDynamicFriction() {
        return updater.getLocomotionParams().getDynamicFriction();
    }

    public void setDirection(double direction) {
        updater.getLocomotionParams().setDirection(direction);
    }

    @Override
    public JPanel createDynamicControls() {
        return new SnakeDynamicOptions(this);
    }

    @Override
    public void doOptimization()  {
        Optimizer optimizer;
        if (GUIUtil.hasBasicService())   // need to verify
            optimizer = new Optimizer(this);
        else
            optimizer = new Optimizer(this, FileUtil.getHomeDir() +"performance/snake/snake_optimization.txt" );

        setPaused(false);
        optimizer.doOptimization(  OptimizationStrategyType.GENETIC_SEARCH, INITIAL_PARAMS, 0.3);
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            timeStep_ = updater.stepForward(snake, timeStep_ );
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g )  {
        if (g==null) return;
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                getAntialiasing() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );

        Point2d newCenter = snake.getCenter();
        Vector2d distanceDelta = new Vector2d( oldCenter.x - newCenter.x, oldCenter.y - newCenter.y );
        velocity = distanceDelta.length() / (getNumStepsPerFrame() * timeStep_);
        distance.add( distanceDelta );

        BackgroundGridRenderer bgRenderer = new BackgroundGridRenderer(gridColor);
        bgRenderer.drawGridBackground(g2, CELL_SIZE, XDIM, YDIM, distance);

        // draw the snake on the grid
        snake.translate( distanceDelta );

        renderer.render(snake, g2);

        oldCenter = snake.getCenter();
    }

    // api for setting snake params  /////////////////////////////////

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new SnakeOptionsDialog(frame, this );
    }

    @Override
    protected String getStatusMessage() {
        return super.getStatusMessage() + "    velocity=" + FormatUtil.formatNumber(velocity);
    }

    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the snake's fitness.
     * The measure is purely based on its velocity.
     * If the snake becomes unstable, then 0.0 is returned.
     */
    @Override
    public double evaluateFitness( ParameterArray params ) {

        LocomotionParameters locoParams = updater.getLocomotionParams();
        locoParams.setWaveSpeed( params.get( 0 ).getValue() );
        locoParams.setWaveAmplitude( params.get( 1 ).getValue() );
        locoParams.setWavePeriod( params.get( 2 ).getValue() );

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            // let the snake run for a while
            ThreadUtil.sleep(1000 + (int) (3000 / (1.0 + 0.2 * ct)));

            improved = (velocity - oldVelocity) > 0.00001;

            oldVelocity = velocity;
            ct++;
            stable = snake.isStable();
        }
        if ( !stable )   {
            System.out.println( "SnakeSim unstable" );
            return 100000.0;
        }
        else {
            return 1.0 / oldVelocity;
        }
    }
}