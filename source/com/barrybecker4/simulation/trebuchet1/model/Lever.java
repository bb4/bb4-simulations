/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Lever extends RenderablePart {

    // the angle of the level wrt horizontal (0 being horizontal)
    private double counterWeightLeverLength;
    private double slingLeverLength;

    // amount of mass in kg per meter magnitude of the lever
    private static final double LEVER_MASS_PER_METER = 2.0;

    private static final BasicStroke LEVER_STROKE = new BasicStroke(10.0f);
    private static final Color LEVER_COLOR = new Color(80, 60, 180);


    Lever(double counterWightLeverLength, double slingLeverLength) {

        counterWeightLeverLength = counterWightLeverLength;
        this.slingLeverLength = slingLeverLength;
    }


    double getSlingLeverLength() {
        return slingLeverLength;
    }

    void setSlingLeverLength(double slingLeverLength) {
        this.slingLeverLength = slingLeverLength;
    }

    double getCounterWeightLeverLength() {
        return counterWeightLeverLength;
    }

    void setCounterWeightLeverLength(double counterWeightLeverLength) {
        this.counterWeightLeverLength = counterWeightLeverLength;
    }

    /**
     *
     * @return the mass in kilograms
     */
    public double getMass() {
        return LEVER_MASS_PER_METER * getTotalLength();
    }

    private double getTotalLength() {
        return counterWeightLeverLength + slingLeverLength;
    }

    // @@ make constant to improve perf?
    Vector2d getFulcrumPosition() {
        return new Vector2d(STRUT_BASE_X, (int) (-SCALE_FACTOR * height));
    }

    /**
     * I = 1/3 * ML^2
     * see physics book.
     * @return the moment of inertia for the lever
     */
    double getInertia() {
        double sllSquared = slingLeverLength * slingLeverLength;
        double cwlSquared = counterWeightLeverLength * counterWeightLeverLength;
        return getMass() / 3.0 * (sllSquared + cwlSquared);
    }


    public void render(Graphics2D g2, double scale) {

        g2.setStroke(LEVER_STROKE);
        g2.setColor(LEVER_COLOR);

        Vector2d fulcrumPos = getFulcrumPosition();

        double cos = SCALE_FACTOR * Math.cos(angle);
        double sin = SCALE_FACTOR * Math.sin(angle);

        g2.drawLine((int) (scale * (fulcrumPos.x + sin * counterWeightLeverLength)),
                    (int) (scale * (fulcrumPos.y - cos * counterWeightLeverLength) + BASE_Y),
                    (int) (scale * (fulcrumPos.x - sin * slingLeverLength)),
                    (int) (scale * (fulcrumPos.y + cos * slingLeverLength)) + BASE_Y);
    }

}
