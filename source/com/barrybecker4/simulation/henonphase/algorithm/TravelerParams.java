/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase.algorithm;

/**
 * Henon traveler params are immutable.
 *
 * @author Barry Becker
 */
public class TravelerParams {

    public static final double DEFAULT_PHASE_ANGLE = 4.995;
    public static final double DEFAULT_MULTIPLIER = 1.0;
    public static final double DEFAULT_OFFSET = 0.0;

    private double angle = DEFAULT_PHASE_ANGLE;
    private double multiplier = DEFAULT_MULTIPLIER;
    private double offset = DEFAULT_OFFSET;

    /**
     * Default Constructor.
     */
    public TravelerParams() {}

    /**
     * Constructor.
     */
    public TravelerParams(double angle, double multiplier, double offset) {

        this.angle = angle;
        this.multiplier = multiplier;
        this.offset = offset;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isDefaultMultiplier() {
        return this.multiplier != DEFAULT_MULTIPLIER;
    }

    public boolean isDefaultOffset() {
        return this.offset != DEFAULT_OFFSET;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TravelerParams that = (TravelerParams) o;

        if (Double.compare(that.angle, angle) != 0) return false;
        if (Double.compare(that.multiplier, multiplier) != 0) return false;
        if (Double.compare(that.offset, offset) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = angle == +0.0d ? 0L : Double.doubleToLongBits(angle);
        result = (int) (temp ^ (temp >>> 32));
        temp = multiplier == +0.0d ? 0L : Double.doubleToLongBits(multiplier);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = offset == +0.0d ? 0L : Double.doubleToLongBits(offset);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getMultiplier() {

        return multiplier;
    }

    public double getOffset() {
        return offset;
    }
}
