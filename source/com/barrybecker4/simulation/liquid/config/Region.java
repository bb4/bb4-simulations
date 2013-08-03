/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.config;

import com.barrybecker4.common.geometry.Location;

/**
 * Represents a region of something (like a source sink, or liquid block) in the simulation.
 *
 * @author Barry Becker
 */
public class Region {

    /** starting location for the rectangular region */
    Location start;

    /** optional stopping location for the rectangular region */
    Location stop;

    /**
     * Constructor
     */
    public Region(Location start, Location stop) {
        this.start = start;
        this.stop = stop;
    }

    public Location getStart() {
        return start;
    }

    public Location getStop() {
        if (stop == null) {
            return start;
        }
        return stop;
    }

}
