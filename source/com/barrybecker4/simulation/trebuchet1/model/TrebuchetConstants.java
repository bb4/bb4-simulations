/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

/**
 * @author Barry Becker Date: Sep 17, 2005
 */
public final class TrebuchetConstants {

    private TrebuchetConstants() {};

    // scales the size of the trebuchet's geometry
    public static final double SCALE = 1.0;


    // all distances are in meters, mass in Kilograms
    public static final double HEIGHT = 2.0;
    public static final double RAMP_HEIGHT = 0.0;
    // coefficient of dynamic friction on the ramp
    public static final double RAMP_FRICTION = 0.4;


    // the allowable ranges for these things will be .2*<default> to 4*<default>
    public static final double MIN_FACTOR = 0.2;
    public static final double MAX_FACTOR = 4.0;

    // defaults for parameters                                                                                                                                                              _
    public static final double DEFAULT_COUNTER_WEIGHT_MASS = 10.0; // Kg
    public static final double DEFAULT_CW_LEVER_LENGTH = 1.0; // M
    public static final double DEFAULT_SLING_LEVER_LENGTH = 2.4;  // M
    public static final double DEFAULT_SLING_LENGTH = 0.8;  // M
    public static final double DEFAULT_PROJECTILE_MASS = 1;
    public static final double DEFAULT_SLING_RELEASE_ANGLE = Math.PI / 8;

    // debug level of 0 means no debug info, 3 is all debug info
    public static final int DEBUG_LEVEL = 0;

}
