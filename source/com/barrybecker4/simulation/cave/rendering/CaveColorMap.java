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

    private static Color[] COLORS =  {
        Color.WHITE,
        new Color(250, 215, 0),
        new Color(140, 230, 0),
        //new Color(90, 210, 5),
        //new Color(0, 200, 230),
        new Color(40, 150, 225),
        //new Color(110, 60, 200),
        //new Color(200, 120, 90),
        new Color(20, 0, 210),
        new Color(210, 101, 208),
        //Color.BLACK
    };

    public CaveColorMap() {
        super(getControlPoints(new Range(0, 1.0)), COLORS);
    }

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
