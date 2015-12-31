/** Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.cave.rendering;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.ui.util.ColorMap;

import java.awt.Color;
import java.util.Arrays;

/**
 * Default colormap for cave visualization
 * May be edited in the UI.
 *
 * @author Barry Becker
 */
public class CaveColorMap extends ColorMap {



    private static final Color[] COLORS =  {
        Color.WHITE,
        new Color(190, 240, 255),
        new Color(160, 200, 250),
        new Color(100, 180, 230),
        new Color(90, 170, 200),
        new Color(70, 160, 100),
        new Color(40, 130, 0),
        Color.BLACK
    };

    public CaveColorMap(Range range) {
        super(getControlPoints(range), COLORS);
    }

    private static double[] getControlPoints(Range range) {
        double floor = range.getMin();
        double ceil = range.getMax() + 0.000001 * range.getExtent();
        double[] values = new double[COLORS.length];
        double step = range.getExtent() / (COLORS.length - 1);
        int ct = 0;
        for (Double v = floor; v <= ceil; v += step) {
            values[ct++] = v;
        }
        return values;
    }
}
