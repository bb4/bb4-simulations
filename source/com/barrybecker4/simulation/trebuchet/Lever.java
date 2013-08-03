/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trebuchet;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Lever extends RenderablePart {

    // the angle of the leverl wrt horizontal (0 being horizontal)
    private double counterWeightLeverLength_;
    private double slingLeverLength_ ;

    // amount of mass in kg per meter magnitude of the lever
    public static final double LEVER_MASS_PER_METER = 2.0;

    private static final BasicStroke LEVER_STROKE = new BasicStroke(10.0f);
    private static final Color LEVER_COLOR = new Color(80, 60, 180);


    public Lever(double counterWightLeverLength, double slingLeverLength) {

        counterWeightLeverLength_ = counterWightLeverLength;
        slingLeverLength_ = slingLeverLength;
    }


    public double getSlingLeverLength() {
        return slingLeverLength_;
    }

    public void setSlingLeverLength(double slingLeverLength) {
        this.slingLeverLength_ = slingLeverLength;
    }

    public double getCounterWeightLeverLength() {
        return counterWeightLeverLength_;
    }

    public void setCounterWeightLeverLength(double counterWeightLeverLength) {
        this.counterWeightLeverLength_ = counterWeightLeverLength;
    }

    /**
     *
     * @return the mass in kilograms
     */
    public double getMass() {
        return LEVER_MASS_PER_METER * getTotalLength();
    }

    public double getTotalLength() {
        return counterWeightLeverLength_ + slingLeverLength_;
    }

    // @@ make constant to improve perf?
    public Vector2d getFulcrumPosition() {
        return new Vector2d(STRUT_BASE_X, (int) (-SCALE_FACTOR * height_));
    }

    /**
     * I = 1/3 * ML^2
     * see physics book.
     * @return the moment of inertia for the lever
     */
    public double getInertia() {
        double sllSquared = slingLeverLength_ * slingLeverLength_;
        double cwlSquared = counterWeightLeverLength_ * counterWeightLeverLength_;
        return getMass() / 3.0 * (sllSquared + cwlSquared);
    }



    public void render(Graphics2D g2, double scale) {

        g2.setStroke(LEVER_STROKE);
        g2.setColor(LEVER_COLOR);

        Vector2d fulcrumPos = getFulcrumPosition();

        double cos = SCALE_FACTOR * Math.cos(angle_);
        double sin = SCALE_FACTOR * Math.sin(angle_);

        g2.drawLine((int) (scale * (fulcrumPos.x + sin * counterWeightLeverLength_)),
                    (int) (scale * (fulcrumPos.y - cos * counterWeightLeverLength_) + BASE_Y),
                    (int) (scale * (fulcrumPos.x - sin * slingLeverLength_)),
                    (int) (scale * (fulcrumPos.y + cos * slingLeverLength_)) + BASE_Y);
    }

}
