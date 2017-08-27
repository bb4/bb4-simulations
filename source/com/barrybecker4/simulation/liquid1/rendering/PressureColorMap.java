/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.rendering;

import com.barrybecker4.ui.util.ColorMap;

import java.awt.*;

/**
 * A colormap for coloring the groups according to how healthy they are.
 * Blue will be healthy, while red will be near dead.
 *
 * @author Barry Becker
 */
class PressureColorMap extends ColorMap {

    /** base transparency value.*/
    private static final int CM_TRANS = 20;

    /** control point values */
    private static final double pressureVals_[] = {
                    -200000.0, -2000.0, -50.0, -10.0,
                    0.0,
                    10.0, 50.0, 2000.0, 200000.0};

    private static final Color pressureColors_[] = {
        new Color( 0, 0, 250, 255 ),
        new Color( 0, 10, 255, CM_TRANS + 90 ),
        new Color( 0, 100, 255, CM_TRANS + 30 ),
        new Color( 10, 255, 70, CM_TRANS ),
        new Color( 250, 250, 250, CM_TRANS ),
        new Color( 250, 250, 27, CM_TRANS ),
        new Color( 255, 120, 0, CM_TRANS + 30),
        new Color( 255, 0, 0, CM_TRANS + 90 ),
        new Color( 250, 0, 0, 255 ),
    };

    /**
     * our own custom colormap for visualizing values in Go.
     */
    PressureColorMap()
    {
        super( pressureVals_, pressureColors_ );
    }

}