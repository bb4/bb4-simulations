/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid.rendering;

import com.barrybecker4.simulation.common.rendering.ModelImage;
import com.barrybecker4.simulation.fluid.model.Grid;
import com.barrybecker4.ui.util.ColorMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *  Renders a specified liquid environment.
 *
 *  @author Barry Becker
 */
public final class EnvironmentRenderer {

    // rendering attributes
    private static final Color GRID_COLOR = new Color( 30, 30, 30, 10 );
    private static final Color VECTOR_COLOR = new Color( 200, 60, 30, 50 );


    private static final double  VECTOR_SCALE = 40.0;
    private static final int OFFSET = 10;

    private static final PressureColorMap PRESSURE_COLOR_MAP = new PressureColorMap();

    private Grid grid;
    private ModelImage modelImage;
    private RenderingOptions options;


    /** Constructor */
    public EnvironmentRenderer(Grid grid, RenderingOptions options) {

        this.grid = grid;
        this.options = options;
        modelImage = new ModelImage(grid, PRESSURE_COLOR_MAP, (int)options.getScale());
    }

    public ColorMap getColorMap() {
        return PRESSURE_COLOR_MAP;
    }

    public RenderingOptions getOptions() {
        return options;
    }

    /**
     * Render the Environment on the screen.
     */
    public void render(Graphics2D g) {

        // draw the cells colored by ---pressure--- val
        if (options.getShowPressures()) {
            concurrentRenderPressures(g);
        }

        // outer boundary
        double scale = options.getScale();
        g.drawRect( OFFSET, OFFSET, (int) (grid.getWidth() * scale), (int) (grid.getHeight() * scale) );

        // draw the ---velocity--- field (and status)
        if (options.getShowVelocities()) {
            drawVectors(g);
        }

        if (options.getShowGrid())  {
            drawGrid(g);
        }
    }

    /**
     * If the render options say to use parallelism, then we will render the pressures concurrently.
     */
    private void concurrentRenderPressures(Graphics2D g2) {

        int height = grid.getHeight();
        modelImage.setUseLinearInterpolation(options.getUseLinearInterpolation());

        int numProcs = options.getParallelizer().getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);
        int range = (height / numProcs);
        for (int i = 0; i < (numProcs - 1); i++) {
            int offset = i * range;
            workers.add(new RenderWorker(modelImage, offset, offset + range));
        }
        // leftover in the last strip, or all of it if only one processor.
        int currentRow = range * (numProcs - 1);
        workers.add(new RenderWorker(modelImage, currentRow, height));

        // blocks until all Callables are done running.
        options.getParallelizer().invokeAllRunnables(workers);

        g2.drawImage(modelImage.getImage(), OFFSET, OFFSET, null);
    }

    private void drawGrid(Graphics2D g)    {
        g.setColor( GRID_COLOR );
        double scale = options.getScale();

        int rightEdgePos = (int) (scale * grid.getWidth());
        int bottomEdgePos = (int) (scale * grid.getHeight());

        for (int j = 0; j < grid.getHeight(); j++ )   //  -----
        {
            int ypos = (int) (j * scale);
            g.drawLine( OFFSET, ypos + OFFSET, rightEdgePos + OFFSET, ypos + OFFSET );
        }
        for (int i = 0; i < grid.getWidth(); i++ )    //  ||||
        {
            int xpos = (int) (i * scale);
            g.drawLine( xpos + OFFSET, OFFSET, xpos + OFFSET, bottomEdgePos + OFFSET );
        }
    }


    private void drawVectors(Graphics2D g) {
        g.setColor( VECTOR_COLOR );
        double scale = options.getScale();

        for ( int j = 0; j < grid.getHeight(); j++ ) {
            for ( int i = 0; i < grid.getWidth(); i++ ) {
                double u = grid.getU(i, j);
                double v = grid.getV(i, j);
                int x = (int) (scale * i) + OFFSET;
                int y = (int) (scale * j) + OFFSET;

                g.drawLine( x, y,
                        (int) (scale * i + VECTOR_SCALE  * u) + OFFSET, (int) (scale * j + VECTOR_SCALE * v) + OFFSET );

            }
        }
    }
}
