/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid1.model;

import com.barrybecker4.simulation.common.Profiler;

/**
 *  This is the global space containing all the cells, walls, and fluid
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics
 *
 *  @author Jos Stam, ported to java by Barry Becker and enhanced.
 */
public class FluidEnvironment {

    /** the cells that the liquid will flow through. Contains two CellGrids for 2 steps - current and last.   */
    private Grid grid;

    public static final double DEFAULT_DIFFUSION_RATE = 0.0f;
    public static final double DEFAULT_VISCOSITY = 0.0f;
    public static final int DEFAULT_NUM_SOLVER_ITERATIONS = 20;

    private double diffusionRate_ = DEFAULT_DIFFUSION_RATE;
    private double viscosity_ = DEFAULT_VISCOSITY;

    private int numSolverIterations = DEFAULT_NUM_SOLVER_ITERATIONS;


    /**
     * Constructor
     */
    public FluidEnvironment(int dimX, int dimY) {

        grid = new Grid(dimX, dimY);
    }

    /** reset to original state */
    public void reset() {
        grid = new Grid(grid.getWidth(), grid.getHeight());
    }

    public Grid getGrid() {
        return grid;
    }

    public int getWidth() {
        return grid.getWidth() + 2;
    }
    public int getHeight() {
        return grid.getHeight() + 2;
    }

    public void setDiffusionRate(double rate) {
        diffusionRate_ = rate;
    }

    public void setViscosity(double v) {
        viscosity_ = v;
    }

    public void setNumSolverIterations(int numIterations) {
        numSolverIterations = numIterations;
    }

    /**
     * Advance a timeStep
     * @return the new timeStep (does not change in this case)
     */
    public double stepForward( double timeStep) {
        Profiler.getInstance().startCalculationTime();

        velocityStep(viscosity_, timeStep);
        densityStep(CellProperty.DENSITY, grid.getGrid1().u, grid.getGrid1().v, timeStep);

        Profiler.getInstance().stopCalculationTime();
        return timeStep;
    }

    private void densityStep(CellProperty prop, double[][] u, double[][] v, double dt) {
        //addSource( x, dt );
        grid.swap(prop);
        diffuse(Boundary.NEITHER, prop, diffusionRate_, dt );
        grid.swap(prop);
        advect(Boundary.NEITHER, prop, u, v, dt );
    }

    private void velocityStep(double visc, double dt) {

        //addSource( u, dt );
        //addSource( v, dt );
        CellGrid g0 = grid.getGrid0();
        CellGrid g1 = grid.getGrid1();

        grid.swap(CellProperty.U);
        diffuse( Boundary.VERTICAL, CellProperty.U, visc, dt );
        grid.swap(CellProperty.V);
        diffuse( Boundary.HORIZONTAL, CellProperty.V, visc, dt );

        project( g1.u, g1.v, g0.u, g0.v );

        grid.swap(CellProperty.U);
        grid.swap(CellProperty.V);

        advect( Boundary.VERTICAL, CellProperty.U, g0.u, g0.v, dt );
        advect( Boundary.HORIZONTAL, CellProperty.V, g0.u, g0.v, dt );

        project( g1.u, g1.v, g0.u, g0.v );
    }

    /** project the fluid */
    private void project(double[][] u, double[][] v,
                         double[][] p, double[][] div )   {
        int width = grid.getWidth();
        int height = grid.getHeight();

        for (int i =1 ; i <= width; i++ ) {
            for (int j =1 ; j <= height; j++ ) {
                div[i][j] = -(u[i+1][j] - u[i-1][j] + v[i][j+1] - v[i][j-1]) / (width + height);
                p[i][j] = 0;
            }
        }
        grid.setBoundary(Boundary.NEITHER, div);
        grid.setBoundary(Boundary.NEITHER, p);

        linearSolve(Boundary.NEITHER, p, div, 1, 4);

        for (int i=1 ; i <= width ; i++ ) {
            for (int j=1 ; j <= height ; j++ ) {
                u[i][j] -= 0.5f * width * (p[i+1][j] - p[i-1][j]);
                v[i][j] -= 0.5f * height  *(p[i][j+1] - p[i][j-1]);
            }
        }
        grid.setBoundary(Boundary.VERTICAL, u);
        grid.setBoundary(Boundary.HORIZONTAL, v);
    }

    /**
     * Diffuse the pressure.
     * @param bound
     * @param prop the cell property to diffuse
     * @param diff either diffusion rate or viscosity.
     */
    private void diffuse(Boundary bound, CellProperty prop, double diff, double dt) {
        double a = dt * diff * grid.getWidth() * grid.getHeight();
        linearSolve(bound, grid.getGrid1().getProperty(prop), grid.getGrid0().getProperty(prop), a, 1 + 4 * a);
    }

    /** Advect the fluid in the field. */
    private void advect( Boundary bound, CellProperty prop, double[][] u, double[][] v, double dt )  {

        int width = grid.getWidth();
        int height = grid.getHeight();

        double[][] d0 = grid.getGrid0().getProperty(prop);
        double[][] d1 = grid.getGrid1().getProperty(prop);

        double dt0 = dt * width;
        for ( int i=1 ; i <= width; i++ ) {
            for ( int j=1 ; j <= height; j++ ) {
                double x = i - dt0 * u[i][j];
                double y = j - dt0 * v[i][j];
                if (x < 0.5f) {
                    x=0.5f;
                }
                if (x > width + 0.5f)  {
                    x = width + 0.5f;
                }
                int i0 = (int)x;
                int i1 = i0+1;
                if (y < 0.5f) {
                    y = 0.5f;
                }
                if (y > height + 0.5f) {
                    y = height + 0.5f;
                }
                int j0 = (int)y;
                int j1 = j0+1;
                double s1 = x - i0;
                double s0 = 1 - s1;
                double t1 = y - j0;
                double t0 = 1 - t1;
                d1[i][j] = s0 * (t0 * d0[i0][j0] + t1 * d0[i0][j1]) +
                            s1 * (t0 * d0[i1][j0] + t1 * d0[i1][j1]);
            }
        }
        grid.setBoundary(bound, d1);
    }

    /**
     * Solve the system
     */
    private void linearSolve(Boundary bound, double[][] x, double[][] x0, double a, double c) {

       for ( int k = 0 ; k < numSolverIterations; k++ ) {
            for ( int i = 1 ; i <= grid.getWidth(); i++ ) {
                for ( int j = 1 ; j <= grid.getHeight(); j++ ) {
                    x[i][j] = (x0[i][j] + a * (x[i-1][j] + x0[i+1][j] + x[i][j-1] + x[i][j+1])) / c;
                }
            }
            grid.setBoundary(bound, x);
        }
    }

    /**
     * Add a fluid source to the environment
     */
    private void addSource(double[][] x0, double[][] x1, double dt) {

        for (int i = 0 ; i < getWidth() ; i++ ) {
            for (int j = 0 ; j < getHeight(); j++ ) {
                x1[i][j] += dt * x0[i][j];
            }
        }
    }
}
