/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.model;

/**
 *  The cells x and y dimensions. Should be square.
 *
 *  @author Barry Becker
 */
public class CellDimensions {

    public static final double CELL_SIZE = 10.0;

    public static final double INVERSE_CELL_SIZE = 1.0 / CellDimensions.CELL_SIZE;

    /** size of a cell */
    public final double dx;
    public final double dy;

    /** squares of edge lengths */
    public final double dxSq;
    public final double dySq;

    /**
     * constructor
     */
    public CellDimensions()  {

        // cell dimensions
        dx = dy = CELL_SIZE;
        dxSq = dx * dx;
        dySq = dy * dy;
    }
}