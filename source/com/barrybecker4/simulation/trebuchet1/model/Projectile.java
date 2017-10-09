/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

import com.barrybecker4.common.math.LinearUtil;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Projectile extends RenderablePart {

    private double mass = 1.0;
    private double radius;
    private Vector2d position = new Vector2d();
    private boolean isOnRamp = true;
    private boolean isReleased = false;

    private Vector2d acceleration = new Vector2d(0, 0);
    private Vector2d velocity = new Vector2d(0, 0);
    private Vector2d force = new Vector2d(0, 0);

    private static final BasicStroke LEVER_STROKE = new BasicStroke(10.0f);
    private static final Color BORDER_COLOR = new Color(140, 50, 110);
    private static final Color FILL_COLOR = new Color(80, 150, 10);


    public Projectile(double projectileMass) {
        mass = projectileMass;
        radius = 0.05 * Math.cbrt(mass);
    }

    public void setX(double x) {
        position.x = x;
    }

    public double getX() {
        return position.x;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setY(double y) {
        position.y = y;
    }

    public double getY() {
        return position.y;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }


    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }


    public boolean isOnRamp() {
        return isOnRamp;
    }

    private void setOnRamp(boolean onRamp) {
        isOnRamp = onRamp;
    }

    public boolean isReleased() {
        return isReleased;
    }

    public void setReleased(boolean released) {
        isReleased = released;
    }

    public double getDistanceFrom(Vector2d referencePoint) {
        return LinearUtil.distance(referencePoint, position);
    }
    public double getInertia(Vector2d referencePoint) {
        double dist = getDistanceFrom(referencePoint);
        return getMass() / 3.0 * dist * dist;
    }

    public void setForce(Vector2d force, double timeStep) {

        if (isOnRamp() && force.y > 0.0) {
            force.y = 0;
        }
        this.force.set(force);

        acceleration.set(force);
        acceleration.scale( 1.0 / getMass());
        Vector2d deltaVelocity = new Vector2d(acceleration);
        deltaVelocity.scale(timeStep);
        velocity.add(deltaVelocity);

        position.set(position.x + SCALE_FACTOR * timeStep * velocity.x,
                      position.y + SCALE_FACTOR * timeStep * velocity.y);
        if (isOnRamp() && position.y < (-4)) {
            setOnRamp(false);
            System.out.println("*********** no longer on ramp!");
        }
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public void render(Graphics2D g2, double scale) {

        int radius = (int) (SCALE_FACTOR * this.radius);
        g2.setColor(BORDER_COLOR);
        int diameter = (int) (scale * 2.0 * radius);
        int ovalX = (int) (scale * (position.x - radius));
        int ovalY = (int) (scale * (position.y - radius) + BASE_Y);
        g2.drawOval(ovalX, ovalY, diameter, diameter);
        g2.setColor(FILL_COLOR);
        g2.fillOval(ovalX, ovalY, diameter, diameter);
        if (isReleased()) {
            int d = (int) (diameter + scale * 4.0);
            g2.drawOval(ovalX, ovalY, d, d);
        }

        if (getShowVelocityVectors()) {
            g2.setStroke(VELOCITY_VECTOR_STROKE);
            g2.setColor(VELOCITY_VECTOR_COLOR);
            g2.drawLine((int) (scale * position.x), (int) (BASE_Y + scale * position.y),
                        (int) (scale * (position.x + velocity.x)), (int) (BASE_Y + scale *(position.y + velocity.y)));
        }
        if (getShowForceVectors())  {
            g2.setStroke(FORCE_VECTOR_STROKE);
            g2.setColor(FORCE_VECTOR_COLOR);
            g2.drawLine((int) (scale * position.x), (int) (BASE_Y + scale * position.y),
                        (int) (scale * (position.x + force.x)), (int) (BASE_Y + scale *(position.y + force.y)));
        }
    }

}
