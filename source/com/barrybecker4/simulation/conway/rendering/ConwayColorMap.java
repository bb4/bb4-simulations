/** Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.conway.rendering;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.ui.util.ColorMap;
import sun.security.provider.SHA;

import java.awt.*;
import java.util.Arrays;

/**
 * Default colormap for cave visualization
 * May be edited in the UI.
 *
 * @author Barry Becker
 */
public class ConwayColorMap extends ColorMap {

    private static final Color SHADOW_COLOR = new Color(120, 130, 180);
    private static Color[] COLORS =  {
        SHADOW_COLOR,
        new Color(250, 180, 0),
        new Color(240, 230, 0),
        new Color(140, 230, 0),
        new Color(40, 150, 225),
        new Color(20, 0, 210),
        new Color(210, 101, 208),
        //Color.BLACK
    };

    public ConwayColorMap() {
        super(getControlPoints(new Range(0, 1000.0)), COLORS);
    }

    private static double[] getControlPoints(Range range) {

        double floor = range.getMin();
        double ceil = range.getMax() + 0.000001 * range.getExtent();
        double[] values = new double[COLORS.length];
        values[0] = floor;
        double step = (range.getExtent() - 1) / (COLORS.length - 2);
        int ct = 1;
        for (Double v = floor + 1; v <= ceil; v += step) {
            values[ct++] = v;
        }
        System.out.println("cpts = " + Arrays.toString(values));
        return values;
    }
}
