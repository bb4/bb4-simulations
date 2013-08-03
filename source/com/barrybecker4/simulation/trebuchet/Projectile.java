/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trebuchet;

import com.barrybecker4.common.math.LinearUtil;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Projectile extends RenderablePart {


    private double mass_ = 1.0;
    private double radius_;
    private Vector2d position_ = new Vector2d();
    private boolean isOnRamp_ = true;
    private boolean isReleased_ = false;

    private Vector2d acceleration_ = new Vector2d(0, 0);
    private Vector2d velocity_ = new Vector2d(0, 0);
    private Vector2d force_ = new Vector2d(0, 0);

    private static final BasicStroke LEVER_STROKE = new BasicStroke(10.0f);
    private static final Color BORDER_COLOR = new Color(140, 50, 110);
    private static final Color FILL_COLOR = new Color(80, 150, 10);


    public Projectile(double projectileMass) {
        mass_ = projectileMass;
        radius_ = 0.05 * Math.cbrt(mass_);
    }


    public void setX(double x) {
        position_.x = x;
    }

    public double getX() {
        return position_.x;
    }

    public Vector2d getPosition() {
        return position_;
    }

    public void setY(double y) {
        position_.y = y;
    }

    public double getY() {
        return position_.y;
    }

    public void setPosition(Vector2d position) {
        position_  = position;
    }


    public void setMass(double mass) {
        mass_ = mass;
    }

    public double getMass() {
        return mass_;
    }

    public double getRadius() {
        return radius_;
    }


    public boolean isOnRamp() {
        return isOnRamp_;
    }

    private void setOnRamp(boolean onRamp) {
        isOnRamp_ = onRamp;
    }

    public boolean isReleased() {
        return isReleased_;
    }

    public void setReleased(boolean released) {
        isReleased_ = released;
    }

    public double getDistanceFrom(Vector2d referencePoint) {
        return LinearUtil.distance(referencePoint, position_);
    }
    public double getInertia(Vector2d referencePoint) {
        double dist = getDistanceFrom(referencePoint);
        return getMass() / 3.0 * dist * dist;
    }

    public void setForce(Vector2d force, double timeStep) {

        if (isOnRamp() && force.y > 0.0) {
            force.y = 0;
        }
        force_.set(force);

        acceleration_.set(force);
        acceleration_.scale( 1.0 / getMass());
        Vector2d deltaVelocity = new Vector2d(acceleration_);
        deltaVelocity.scale(timeStep);
        velocity_.add(deltaVelocity);

        position_.set(position_.x + SCALE_FACTOR * timeStep * velocity_.x,
                      position_.y + SCALE_FACTOR * timeStep * velocity_.y);
        if (isOnRamp() && position_.y < (-4)) {
            setOnRamp(false);
            System.out.println("*********** no longer on ramp!");
        }
    }

    public Vector2d getVelocity() {
        return velocity_;
    }


    public void render(Graphics2D g2, double scale) {

        int radius = (int) (SCALE_FACTOR * radius_);
        g2.setColor(BORDER_COLOR);
        int diameter = (int) (scale * 2.0 * radius);
        int ovalX = (int) (scale * (position_.x - radius));
        int ovalY = (int) (scale * (position_.y - radius) + BASE_Y);
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
            g2.drawLine((int) (scale * position_.x), (int) (BASE_Y + scale * position_.y),
                        (int) (scale * (position_.x + velocity_.x)), (int) (BASE_Y + scale *(position_.y + velocity_.y)));
        }
        if (getShowForceVectors())  {
            g2.setStroke(FORCE_VECTOR_STROKE);
            g2.setColor(FORCE_VECTOR_COLOR);
            g2.drawLine((int) (scale * position_.x), (int) (BASE_Y + scale * position_.y),
                        (int) (scale * (position_.x + force_.x)), (int) (BASE_Y + scale *(position_.y + force_.y)));
        }
    }

}
