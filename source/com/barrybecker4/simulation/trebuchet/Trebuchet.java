/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trebuchet;

import com.barrybecker4.common.app.ILog;
import com.barrybecker4.ui.util.GUIUtil;
import com.barrybecker4.ui.util.Log;

import javax.vecmath.Vector2d;
import java.awt.*;

import static com.barrybecker4.simulation.common.PhysicsConstants.GRAVITY;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.DEFAULT_COUNTER_WEIGHT_MASS;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.DEFAULT_CW_LEVER_LENGTH;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.DEFAULT_PROJECTILE_MASS;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.DEFAULT_SLING_LENGTH;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.DEFAULT_SLING_LEVER_LENGTH;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.DEFAULT_SLING_RELEASE_ANGLE;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.HEIGHT;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.RAMP_FRICTION;
import static com.barrybecker4.simulation.trebuchet.TrebuchetConstants.SCALE;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *  Data structure and methods for representing a single dynamic trebuchet (advanced form of a catapult)
 *  The geometry of the trebuchet is defined by constants in TebuchetConstants.

 *  Performance Improvements:
 *    - profile (where is the time spent? rendering or computation)
 *
 *  @author Barry Becker
 */
public class Trebuchet {

    protected static final Font BASE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 );
    protected static final int LOG_LEVEL = 1;

    protected static final double MIN_EDGE_ANGLE = 0.3;
    protected static final Vector2d GRAVITY_VEC = new Vector2d(0, GRAVITY);
    protected static final double MAX_LEVER_ANGLE = PI - 0.1;


    // the parts
    private Base base_;
    private Lever lever_;
    private CounterWeight counterWeight_;
    private Sling sling_;
    private Projectile projectile_;

    private Vector2d forceFromHook_ = new Vector2d(0, 0);


    protected static final int NUM_PARTS = 5;
    private RenderablePart[] part_;

    // the time since the start of the simulation
    private static ILog logger_ = null;

    // tweekable rendering parameters
    private boolean showVelocityVectors_ = false;
    private boolean showForceVectors_ = false;


    // scales the geometry of the trebuchet
    private double scale_ = SCALE;


    //Constructor
    // use a harcoded static data interface to initialize
    // so it can be easily run in an applet without using resources.
    public Trebuchet()
    {
        commonInit();
    }

    public void reset() {
        RenderablePart.setAngularVelocity(0);
        commonInit();
    }

    private void commonInit()
    {
        logger_ = new Log();

        part_ = new RenderablePart[NUM_PARTS];

        double angle = PI/2.0 - asin(HEIGHT / DEFAULT_SLING_LEVER_LENGTH);
        RenderablePart.setAngle(angle);
        base_ = new Base();
        part_[0] = base_;
        lever_ = new Lever(DEFAULT_CW_LEVER_LENGTH, DEFAULT_SLING_LEVER_LENGTH);
        part_[1] = lever_;
        //System.out.println("cw mass="+DEFAULT_COUNTER_WEIGHT_MASS);
        counterWeight_ = new CounterWeight(lever_, DEFAULT_COUNTER_WEIGHT_MASS);
        part_[2] = counterWeight_;
        projectile_ = new Projectile(DEFAULT_PROJECTILE_MASS);
        sling_ = new Sling(DEFAULT_SLING_LENGTH, DEFAULT_SLING_RELEASE_ANGLE, lever_, projectile_);
        part_[3] = sling_;
        part_[4] = projectile_;
    }



    /**
     * steps the simulation forward in time
     * if the timestep is too big inaccuracy and instability may result.
     * @return the new timestep
     */
    public double stepForward( double timeStep )
    {
        //logger_.println(1, LOG_LEVEL, "stepForward: about to update (timeStep="+timeStep+')');

        double angle = RenderablePart.getAngle();
        double angularVelocity = RenderablePart.getAngularVelocity();
        double slingAngle = sling_.getAngleWithLever();
        double torque = calculateTorque(angle, slingAngle);
        double inertia = calculateInertia();

        double angularAcceleration = 0;
        if (angle < MAX_LEVER_ANGLE) {
            angularAcceleration = inertia / torque; // in radians per second squared
            angularVelocity += timeStep * angularAcceleration;
            angle += timeStep * angularVelocity;
        }
        else {
            angularVelocity = 0;
            angle = MAX_LEVER_ANGLE;
        }

        RenderablePart.setAngle(angle);
        RenderablePart.setAngularVelocity(angularVelocity);
        //System.out.println("angle="+angle+"  angularVelocity_="
        //  +angularVelocity +" angularAcceleration="+angularAcceleration);

        // calculate the forces acting on the projectile.
        // the magnitude of the tangential force at the hook
        if (!projectile_.isReleased()) {
            double tangentialForceAtHook = torque / lever_.getSlingLeverLength();
            //System.out.println("tangentialForceAtHook="+tangentialForceAtHook);
            double slingAngleWithHorz = sling_.getAngleWithHorz();

            forceFromHook_.set(-cos(slingAngleWithHorz), sin(slingAngleWithHorz));  //sin(PI - angle), -cos(PI + angle));
            forceFromHook_.scale( tangentialForceAtHook * sin(slingAngle));
            Vector2d gravityForce = new Vector2d(GRAVITY_VEC);
            gravityForce.scale(projectile_.getMass());
            forceFromHook_.add(gravityForce);
            // also add a restoring force which is proportional to the distnace from the attachpoint on the sling
            // if we have not yet been released.

            Vector2d restoreForce = sling_.getProjectileAttachPoint();
            restoreForce.sub(projectile_.getPosition());
            restoreForce.scale(100.0);
            forceFromHook_.add(restoreForce);
            projectile_.setForce(forceFromHook_, timeStep);
        }  else {
            Vector2d gravityForce = new Vector2d(GRAVITY_VEC);
            gravityForce.scale(projectile_.getMass());
            projectile_.setForce(gravityForce, timeStep);
        }


        // at the time when it is released, the only force acting on it will be gravity.
        if (!projectile_.isReleased() && slingAngle >= (PI + sling_.getReleaseAngle())) {
            System.out.println("##########################################################################");
            System.out.println("released!  slingAngle = "+slingAngle +" sling release angle = "+sling_.getReleaseAngle());
            System.out.println("##########################################################################");
            projectile_.setReleased(true);
        }

        return timeStep;
    }

    private double calculateTorque(double angle, double slingAngle) {
        double primaryTorque = 0;
        if ( angle < MAX_LEVER_ANGLE) {
            primaryTorque = lever_.getCounterWeightLeverLength() * sin(angle) * GRAVITY * counterWeight_.getMass();
        }
        double dragTorque = 0;
        if (projectile_.isOnRamp()) {
            // case when the projectile is still on the ramp
            dragTorque = -lever_.getSlingLeverLength() * projectile_.getMass() * GRAVITY * RAMP_FRICTION * sin(slingAngle);
        } else {
            // case when the projectile is no longer on the ramp
            double r = projectile_.getDistanceFrom(lever_.getFulcrumPosition());
            dragTorque = r * projectile_.getMass() * GRAVITY * cos(PI - angle - slingAngle) * sin(angle) ;
        }
        //System.out.println("torque= primaryTorque("+primaryTorque+")" +
        //                   " + dragTorque("+dragTorque+")= "+(primaryTorque+dragTorque));
        return primaryTorque + dragTorque;
    }

    /**
     * got this from a physics text
     * I = LEVER_MASS / 3  (b^3 + c^3) + projectileMass/3 * r^2
     * @return
     */
    private double calculateInertia() {
        return lever_.getInertia() + projectile_.getInertia(lever_.getFulcrumPosition());
    }



    // api for tweeking Trebuchet params ////////////////////////////////////////

    public void setScale( double scale )
    {
        scale_ = scale;
    }

    public double getScale()
    {
        return scale_;
    }

    public void setShowVelocityVectors( boolean show )
    {
        showVelocityVectors_ = show;
    }

    public boolean getShowVelocityVectors()
    {
        return showVelocityVectors_;
    }

    public void setShowForceVectors( boolean show )
    {
        showForceVectors_ = show;
    }

    public boolean getShowForceVectors()
    {
        return showForceVectors_;
    }



    public double getCounterWeightLeverLength() {
        return lever_.getCounterWeightLeverLength();
    }

    public void setCounterWeightLeverLength(double counterWeightLeverLength) {
        lever_.setCounterWeightLeverLength(counterWeightLeverLength);
    }

    public double getSlingLeverLength() {
        return lever_.getSlingLeverLength();
    }

    public void setSlingLeverLength(double slingLeverLength) {
        lever_.setSlingLeverLength(slingLeverLength);
    }

    public double getCounterWeightMass() {
        return counterWeight_.getMass();
    }

    public void setCounterWeightMass(double counterWeightMass) {
        this.counterWeight_.setMass(counterWeightMass);
    }

    public double getSlingLength() {
        return sling_.getLength();
    }

    public void setSlingLength(double slingLength) {
        this.sling_.setLength(slingLength);
    }

    public double getProjectileMass() {
        return projectile_.getMass();
    }

    public void setProjectileMass(double projectileMass) {
        this.projectile_.setMass(projectileMass);
    }


    public double getSlingReleaseAngle() {
        return sling_.getReleaseAngle();
    }

    public void setSlingReleaseAngle(double slingReleaseAngle) {
        this.sling_.setReleaseAngle(slingReleaseAngle);
    }


    /**
     * Render the Environment on the screen
     */
    public void render( Graphics2D g )
    {
        int i;

        g.setColor( Color.black ); // default

        // render each part
        for ( i = 0; i < NUM_PARTS; i++ ) {
            if (part_[i] != null)
                part_[i].render( g, getScale() );
        }
    }

}
