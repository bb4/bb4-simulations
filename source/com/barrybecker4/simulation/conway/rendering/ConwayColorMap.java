/** Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.conway.rendering;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.ui.util.ColorMap;

import java.awt.*;

/**
 * Default colormap for cave visualization
 * May be edited in the UI.
 *
 * @author Barry Becker
 */
public class ConwayColorMap extends ColorMap {

    private static Color[] COLORS =  {
        new Color(120, 15, 100),
        new Color(250, 215, 0),
        new Color(140, 230, 0),
        new Color(40, 150, 225),
        new Color(20, 0, 210),
        new Color(210, 101, 208),
        //Color.BLACK
    };

    public ConwayColorMap() {
        super(getControlPoints(new Range(0, 100.0)), COLORS);
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
