/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the magnitude of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public final class TestSnakeData implements SnakeData {

    private static final int NUM_SEGMENTS = 22;

    private static final double SEGMENT_LENGTH = 26;

    /** The widths starting at the nose and edging at the tip of the tail  */
    private static final double[] WIDTHS = {
            10.0, 17.0, 12.0, 14.0,
            16.0, 18.0, 19.1, 20.2, 20.8,
            21.0, 21.0, 21.0, 21.0, 20.0,
            19.0, 18.0, 17.0, 16.0, 14.0,
            12.0, 10.0,  8.0, 6.0
    };

    public int getNumSegments() {
       return NUM_SEGMENTS;
    }

    public double getSegmentLength() {
       return SEGMENT_LENGTH;
    }

    public double[] getWidths() {
        return WIDTHS;
    }
}
