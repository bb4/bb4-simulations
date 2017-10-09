/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

import com.barrybecker4.simulation.common.PhysicsConstants;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class CounterWeight extends RenderablePart {

    private Lever lever_;
    private double mass_;

    private static final int WEIGHT_HANG_LENGTH = 20;

    private static final BasicStroke STROKE = new BasicStroke(4.0f);
    private static final Color COLOR = new Color(120, 40, 40);
    private static final Color FILL_COLOR = new Color(190, 180, 140);

    public CounterWeight(Lever lever, double mass) {
        lever_ = lever;
        mass_ = mass;
    }

    public double getMass() {
        return mass_;
    }

    public void setMass(double mass) {
        this.mass_ = mass;
    }


    public void render(Graphics2D g2, double scale) {

        g2.setStroke(STROKE);
        g2.setColor(COLOR);

        double cwLeverLength = lever_.getCounterWeightLeverLength();
        double cos = SCALE_FACTOR * cwLeverLength* Math.cos(angle);
        double sin = SCALE_FACTOR * cwLeverLength * Math.sin(angle);
        Vector2d attachPt = new Vector2d(STRUT_BASE_X + sin, (int) ( -SCALE_FACTOR * height) - cos);


        g2.drawLine((int) (scale * attachPt.x),
                    (int) (BASE_Y + scale * attachPt.y),
                    (int) (scale * attachPt.x),
                    (int) (BASE_Y + scale * (attachPt.y + WEIGHT_HANG_LENGTH)));

        int radius = (int) (SCALE_FACTOR * 0.05 *Math.cbrt( mass_));
        g2.setColor(COLOR);
        int diameter = (int) (scale * 2.0 * radius);
        int xOval = (int) (scale * (attachPt.x - radius));
        int yOval = (int) (BASE_Y + scale *(attachPt.y + WEIGHT_HANG_LENGTH));
        g2.drawOval(xOval, yOval, diameter, diameter);
        g2.setColor(FILL_COLOR);
        g2.fillOval(xOval, yOval, diameter, diameter);

        double bottomY = attachPt.y + WEIGHT_HANG_LENGTH + diameter;
        if (getShowVelocityVectors()) {
            g2.setStroke(VELOCITY_VECTOR_STROKE);
            g2.setColor(VELOCITY_VECTOR_COLOR);
            double velocityMagnitude = lever_.getCounterWeightLeverLength() * getAngularVelocity() * Math.sin(getAngle());
            g2.drawLine((int) (scale * attachPt.x), (int) (scale *bottomY + BASE_Y), (int) (scale * attachPt.x), (int) (scale * (bottomY + velocityMagnitude) + BASE_Y));
        }
        if (getShowForceVectors())  {
            g2.setStroke(FORCE_VECTOR_STROKE);
            g2.setColor(FORCE_VECTOR_COLOR);
            g2.drawLine((int) (scale * attachPt.x), (int) (scale * bottomY) + BASE_Y,
                        (int) (scale * attachPt.x), (int) (scale * (bottomY + PhysicsConstants.GRAVITY * getMass())) + BASE_Y);
        }

    }
}
