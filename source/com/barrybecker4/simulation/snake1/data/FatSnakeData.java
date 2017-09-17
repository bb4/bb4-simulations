/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.data;


/**
 * Snake that has just eaten a big cat.
 *
 *  @author Barry Becker
 */
public final class FatSnakeData implements SnakeData {

    /** The widths starting at the nose and edging at the tip of the tail  */
    private static final double[] WIDTHS = {
        9.0, 22.0, 10.0, 16.0, 22.0, 29.0, 35.0, 41.0,
        47.0, 52.0, 56.0, 60.0, 63.0, 65.0, 66.0, 67.0,
        67.0, 66.0, 65.0, 63.0, 60.0, 56.0, 52.0, 48.0,
        43.0, 38.0, 33.0, 28.0, 23.0, 19.0, 16.0, 13.0,
        10.0, 7.0, 4.0
    };

    public int getNumSegments() {
       return 34;
    }

    public double getSegmentLength() {
       return 22;
    }

    public double[] getWidths() {
        return WIDTHS;
    }
}
