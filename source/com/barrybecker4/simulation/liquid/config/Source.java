/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.config;

import com.barrybecker4.common.geometry.Location;

import javax.vecmath.Vector2d;

/**
 * Represents a source of liquid in the simulation.
 * Like a spigot.
 *
 * @author Barry Becker
 */
public class Source extends Region {

    /** The direction that all the cells in the source region are flowing. */
    private Vector2d velocity;

    private double startTime;
    private double duration;
    private double repeatInterval;

    /**
     * Constructor
     * @param start one corner of the rectangle were  the flow to start.
     * @param stop opposite corner of the rectangle were  the flow to start.
     * @param velocity speed at which the liquid will flow.
     */
    private Source(Location start, Location stop, Vector2d velocity) {

        this(start, stop, velocity, 0, -1, -1);
    }

    /**
     * Constructor
     * @param start one corner of the rectangle were  the flow to start.
     * @param stop opposite corner of the rectangle were  the flow to start.
     * @param velocity speed at which the liquid will flow.
     * @param startTime time when the flow will start.
     * @param duration magnitude of time that the liquid will flow. If negative, it will flow forever.
     * @param repeatInterval time from when the liquid starts flowing to when it should start flowing again.
     *    if negative, then it will only flow once.
     */
    public Source(Location start, Location stop, Vector2d velocity, double startTime,
                          double duration, double repeatInterval) {
        super(start, stop);
        assert startTime >= 0;
        if (repeatInterval >0) {
            assert duration < repeatInterval : " The duration cannot be longer than the repeatInterval";
        }
        this.velocity = velocity;
        this.startTime = startTime;
        this.duration = duration;
        this.repeatInterval = repeatInterval;
    }

    public Source(Location start,  Vector2d velocity) {
        this(start, null, velocity);
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    /**
     * @param time the current time in the simulation
     * @return true if the source is currently flowing.
     */
    public boolean isOn(double time) {
        boolean on = false;
        if (time >= startTime) {
            double offsetTime = time - startTime;
            on = true;
            if (repeatInterval > 0 && duration > 0) {

                double timeWithinInterval = offsetTime - (int)(offsetTime / repeatInterval) * repeatInterval;
                on = timeWithinInterval < duration;
            }
            else if (duration > 0) {
                on = time < startTime + duration;
            }
       }
        return on;
    }
}
