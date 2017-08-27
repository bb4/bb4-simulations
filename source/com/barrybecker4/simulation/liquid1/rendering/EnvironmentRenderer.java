/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.liquid1.Logger;
import com.barrybecker4.simulation.liquid1.compute.VelocityInterpolator;
import com.barrybecker4.simulation.liquid1.model.Cell;
import com.barrybecker4.simulation.liquid1.model.Grid;
import com.barrybecker4.simulation.liquid1.model.LiquidEnvironment;
import com.barrybecker4.simulation.liquid1.model.Particle;
import com.barrybecker4.ui.util.GUIUtil;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 *  Renders a specified liquid environment.
 *
 *  @author Barry Becker
 */
public final class EnvironmentRenderer {

    // rendering style attributes
    private static final Color GRID_COLOR = new Color( 20, 20, 20, 15 );

    private static final Color PARTICLE_VELOCITY_COLOR = new Color( 225, 0, 35, 20 );
    private static final Stroke PARTICLE_VELOCITY_STROKE  = new BasicStroke(0.2f);

    private static final Color FACE_VELOCITY_COLOR = new Color( 205, 90, 25, 110 );
    private static final Stroke FACE_VELOCITY_STROKE  = new BasicStroke(2.0f);
    private static final double VELOCITY_SCALE = 8.0;

    private static final Color WALL_COLOR = new Color( 100, 210, 170, 150 );
    private static final Color TEXT_COLOR = new Color( 10, 10, 170, 200 );

    /** scales the size of everything */
    private static final double DEFAULT_SCALE = 30;

    /* grid offset  */
    private static final int OFFSET = 10;

    private static final ColorMap pressureColorMap_ = new PressureColorMap();

    private static final Font BASE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 );

    private double scale = DEFAULT_SCALE;

    private float wallLineWidth;
    private int particleSize;

    private RenderingOptions options = new RenderingOptions();

    private LiquidEnvironment env;

    public EnvironmentRenderer(LiquidEnvironment env) {
        this.env = env;
    }

    public void setScale(double scale) {
        this.scale = scale;
        wallLineWidth = (float) (scale / 5.0) + 1;
        particleSize = (int) (scale / 6.0) + 1;
    }

    /**
     * figure out the biggest scale based in on which dimension is bumped up to first
     */
    private void determinScaling(int width, int height) {

        Grid grid = env.getGrid();
        double proposedXScale = width / grid.getXDimension();
        double proposedYScale = height / grid.getYDimension();
        setScale(Math.min(proposedXScale, proposedYScale));
    }

    public double getScale() {
        return scale;
    }

    public RenderingOptions getRenderingOptions() {
        return options;
    }

    /**
     * Render the Environment on the screen.
     */
    public void render(Graphics2D g, int width, int height) {

        double time = System.currentTimeMillis();

        determinScaling(width, height);

        // make sure all the cell statuses are in a consistent state
        env.getGrid().updateCellStatus();

        drawGrid(g);

       // draw the cells colored by ---pressure--- val
       if (options.getShowPressures()) {
           renderPressure(g);
       }

       // draw the ---walls---
       drawWalls(g);

       drawParticles(g);

       if ( options.getShowCellStatus() ) {
           drawCellSymbols(g);
       }

       // draw the ---velocity--- field (and status)
       if (options.getShowVelocities()) {
           drawCellFaceVelocities(g);
       }

        double duration = (System.currentTimeMillis() - time) / 100.0;
        Logger.log( 1, "time to render:  (" + duration + ") " );
    }

    private double getMaxY() {
       return  scale * env.getGrid().getYDimension() + OFFSET;
    }

    /**
     * draw the cells/grid_
     */
    private void drawGrid(Graphics2D g) {

        g.setColor( GRID_COLOR );
        Grid grid = env.getGrid();
        int xDim = grid.getXDimension();
        int yDim = grid.getYDimension();
        int rightEdgePos = (int) (scale * xDim);
        int bottomEdgePos = (int) (scale * yDim);
        int maxY = (int)getMaxY();

        for (int  j = 0; j < yDim; j++ )   //  -----
        {
            int ypos = (int) (j * scale);
            g.drawLine( OFFSET, maxY - ypos, rightEdgePos + OFFSET, maxY - ypos );
        }
        for (int i = 0; i < xDim; i++ )    //  ||||
        {
            int xpos = (int) (i * scale);
            g.drawLine( xpos + OFFSET, maxY, xpos + OFFSET, maxY - bottomEdgePos );
        }
    }

    /**
     * Draw the particles in the liquid in the environment.
     */
    private void drawParticles(Graphics2D g) {

        // draw the ---particles--- of liquid
        double[] a_ = new double[2];
        double maxY = getMaxY();

        for (Particle p : env.getParticles()) {
            p.get(a_);
            g.setColor(getColorForParticle(p));
            double offset = -particleSize /2.0;
            int y = (int) (maxY - (scale * a_[1] - offset));
            g.fillOval((int) (scale * a_[0] + offset + OFFSET), y,
                    particleSize, particleSize);
        }

        if (options.getShowVelocities()) {
            drawParticleVelocities(g);
        }
    }

    private Color getColorForParticle(Particle part) {

        int green = ((int)part.y % 2) == 0  ? 150 : 50;
        int comp = (int) (256.0 * part.getAge() / 20.0);
        comp = (comp > 255) ? 255 : comp;
        return new Color(comp, green, 255 - comp, 80);
    }

    private void drawParticleVelocities(Graphics2D g) {

        g.setStroke( PARTICLE_VELOCITY_STROKE);
        g.setColor( PARTICLE_VELOCITY_COLOR );
        double[] a_ = new double[2];
        Grid grid = env.getGrid();
        VelocityInterpolator interpolator = new VelocityInterpolator(grid);
        double maxY = getMaxY();

        for (Particle p : env.getParticles()) {
            if (options.getShowVelocities()) {
                Vector2d vel = interpolator.findVelocity(p);
                p.get(a_);
                double x = (scale * a_[0]) + OFFSET;

                double xLen = x + VELOCITY_SCALE * vel.x;
                double y = maxY - scale * a_[1];
                double yLen = y - VELOCITY_SCALE * vel.y;
                g.drawLine( (int)x, (int)y, (int)xLen, (int)yLen);
            }
        }
    }

    /**
     * PathColor the squares according to the pressure in that discrete region.
     */
    private void renderPressure(Graphics2D g) {
        Grid grid = env.getGrid();
        double maxY = getMaxY();

        for (int j = 0; j < grid.getYDimension(); j++ ) {
            for (int i = 0; i < grid.getXDimension(); i++ ) {
                g.setColor( pressureColorMap_.getColorForValue( grid.getCell(i, j).getPressure() ) );
                g.fillRect( (int) (scale * (i)) + OFFSET, (int) (maxY - scale * (j)),
                            (int) scale, (int) scale);
            }
        }
    }

    /**
     * Draw walls and boundary.
     */
    private void drawWalls(Graphics2D g) {

        Stroke wallStroke = new BasicStroke(wallLineWidth);
        g.setStroke( wallStroke );
        g.setColor(WALL_COLOR);
        /*
        //Stroke stroke = new BasicStroke(wall.getThickness(), BasicStroke.CAP_BUTT,
        //                                BasicStroke.JOIN_ROUND, 10);
        for (i=0; i<walls_.size(); i++)  {
            Wall wall = (Wall)walls_.elementAt(i);
            g.drawLine( (int)(wall.getStartPoint().getX()*rat+OFFSET),
                        (int)(maxY - (wall.getStartPoint().getY()*rat+OFFSET)),
                        (int)(wall.getStopPoint().getX()*rat+OFFSET),
                        (int)(maxY - (wall.getStopPoint().getY()*rat+OFFSET)) );
        }*/

        // outer boundary
        g.drawRect(OFFSET, OFFSET, (int) (env.getGrid().getXDimension() * scale),
                                   (int) (env.getGrid().getYDimension() * scale));
    }

    /**
     * draw text representing internal state for debug purposes.
     */
    private void drawCellSymbols(Graphics2D g) {

        Grid grid = env.getGrid();
        g.setColor( TEXT_COLOR );
        g.setFont( BASE_FONT );
        StringBuilder strBuf = new StringBuilder( "12" );
        double maxY = getMaxY();

        for ( int j = 0; j < grid.getYDimension(); j++ ) {
            for (int  i = 0; i < grid.getXDimension(); i++ ) {
                int x = (int) (scale * i) + OFFSET;
                int y = (int) (maxY - scale * (j + 1));
                strBuf.setCharAt( 0, grid.getCell(i, j).getStatus().getSymbol() );
                strBuf.setLength( 1 );
                //int nump = grid.getCell(i, j).getNumParticles();
                //if ( nump > 0 )
                //    strBuf.append( String.valueOf( nump ) );
                g.drawString( strBuf.toString(), x + 6, y + 18 );
            }
        }
    }

    /**
     * There is a velocity vector in the center of each cell face.
     */
    private void drawCellFaceVelocities(Graphics2D g) {
        g.setStroke( FACE_VELOCITY_STROKE);
        g.setColor( FACE_VELOCITY_COLOR );
        Grid grid = env.getGrid();
        double maxY = getMaxY();

        for ( int j = 0; j < grid.getYDimension(); j++ ) {
            for ( int i = 0; i < grid.getXDimension(); i++ ) {
                Cell cell = grid.getCell(i, j);
                double u = cell.getU();
                double v = cell.getV();
                int x = (int) (scale * i) + OFFSET;
                int xMid =  (int) (scale * (i + 0.5)) + OFFSET;
                int xLen = (int) (scale * i + VELOCITY_SCALE * u) + OFFSET;
                int y = (int)(maxY - scale * j);
                int yMid =  (int) (maxY - (scale * (j + 0.5)));
                int yLen = (int) (maxY - (scale * j + VELOCITY_SCALE * v ))  ;
                g.drawLine( xMid, y, xMid, yLen);
                g.drawLine( x, yMid, xLen, yMid );
            }
        }
    }
}
