/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase.algorithm;

import com.barrybecker4.ui.util.ColorMap;

import java.awt.*;

/**
 * Default colormap for visualization.
 * May be edited in the UI.
 *
 * @author Barry Becker
 */
public class HenonColorMap2 extends ColorMap {

    private static final double MIN_VALUE = 0;
    private static final  double MAX_VALUE = 1.0;
    private static final  double RANGE = MAX_VALUE - MIN_VALUE;

    private static final int ALPHA = 100;

    private static final double[] VALUES = {
          MIN_VALUE,
          MIN_VALUE + 0.05 * RANGE,
          MIN_VALUE + 0.1 * RANGE,
          MIN_VALUE + 0.15 * RANGE,
          MIN_VALUE + 0.2 * RANGE,
          MIN_VALUE + 0.25 * RANGE,
          MIN_VALUE + 0.3 * RANGE,
          MIN_VALUE + 0.35 * RANGE,
          MIN_VALUE + 0.4 * RANGE,
          MIN_VALUE + 0.45 * RANGE,
          MIN_VALUE + 0.5 * RANGE,
          MIN_VALUE + 0.55 * RANGE,
          MIN_VALUE + 0.6 * RANGE,
          MIN_VALUE + 0.65 * RANGE,
          MIN_VALUE + 0.7 * RANGE,
          MIN_VALUE + 0.75 * RANGE,
          MIN_VALUE + 0.8 * RANGE,
          MIN_VALUE + 0.85 * RANGE,
          MIN_VALUE + 0.9 * RANGE,
          MIN_VALUE + 0.95 * RANGE,
          MIN_VALUE + RANGE
    };

    private static final Color[] COLORS =  {
        Color.WHITE,
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA),
        new Color(144, 139, 124, ALPHA),
        new Color(121, 116, 110, ALPHA),
        new Color(117, 93, 53, ALPHA),
        new Color(147, 115, 167, ALPHA),
        new Color(107, 101, 86, ALPHA),        // stopped
        new Color(160, 156, 132, ALPHA),
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA),
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA),
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA),
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA),
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA),
        new Color(107, 101, 86, ALPHA),
        new Color(160, 156, 132, ALPHA)
    };

    public HenonColorMap2(int alpha) {
        super(VALUES, COLORS);
        this.setGlobalAlpha(alpha);
    }
   /*
    //color[] goodcolor = {#000000, #6b6556, #a09c84, #908b7c, #79746e,
    #755d35, #937343, #9c6b4b, #ab8259, #aa8a61, #578375, #f0f6f2, #d0e0e5,
    #d7e5ec, #d3dfea, #c2d7e7, #a5c6e3, #a6cbe6, #adcbe5, #77839d, #d9d9b9,
    #a9a978, #727b5b, #6b7c4b, #546d3e, #47472e, #727b52, #898a6a, #919272,
    #AC623b, #cb6a33, #9d5c30, #843f2b, #652c2a, #7e372b, #403229, #47392b,
    #3d2626, #362c26, #57392c, #998a72, #864d36, #544732 };
 */

}
