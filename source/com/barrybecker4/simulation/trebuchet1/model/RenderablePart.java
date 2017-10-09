/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

import java.awt.*;

import static com.barrybecker4.simulation.trebuchet1.model.TrebuchetConstants.HEIGHT;

/**
 * A physical piece of an object.
 *
 * @author Barry Becker
 */
public abstract class RenderablePart {

    public static final int BASE_Y = 450;
    public static final double SCALE_FACTOR = 70;

    protected static final int BASE_X = 80;
    protected static final int STRUT_BASE_X = 300;

    protected static final BasicStroke VELOCITY_VECTOR_STROKE = new BasicStroke(1.0f);
    protected static final BasicStroke FORCE_VECTOR_STROKE = new BasicStroke(0.3f);
    protected static final Color VELOCITY_VECTOR_COLOR = new Color(70, 10, 255, 200);
    protected static final Color FORCE_VECTOR_COLOR = new Color(200, 0, 80, 200);

    protected static double height = HEIGHT;
    protected static double angle;
    protected static double angularVelocity_ = 0;

    protected static boolean showVelocityVectors = true;
    protected static boolean showForceVectors = true;

    /** Constructor */
    public RenderablePart() {}


    public static double getHieght() {
        return height;
    }

    public static void setHeight(double height) {
        RenderablePart.height = height;
    }


    public static boolean getShowVelocityVectors() {
        return showVelocityVectors;
    }

    public static void setShowVelocityVectors(boolean showVelocityVectors) {
        RenderablePart.showVelocityVectors = showVelocityVectors;
    }

    public static boolean getShowForceVectors() {
        return showForceVectors;
    }

    public static void setShowForceVectors(boolean showForceVectors) {
        RenderablePart.showForceVectors = showForceVectors;
    }

    public static double getAngle() {
        return angle;
    }

    public static void setAngle(double angle) {
        RenderablePart.angle = angle;
    }

    public static double getAngularVelocity() {
        return angularVelocity_;
    }

    public static void setAngularVelocity(double angularVelocity) {
        angularVelocity_ = angularVelocity;
    }

    protected abstract void render(Graphics2D g2, double scale);
}
