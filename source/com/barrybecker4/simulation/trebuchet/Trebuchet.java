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
    private Base base;
    private Lever lever;
    private CounterWeight counterWeight;
    private Sling sling;
    private Projectile projectile;

    private Vector2d forceFromHook_ = new Vector2d(0, 0);

    protected static final int NUM_PARTS = 5;
    private RenderablePart[] part;

    // the time since the start of the simulation
    private static ILog logger = null;

    // tweakable rendering parameters
    private boolean showVelocityVectors = false;
    private boolean showForceVectors = false;

    // scales the geometry of the trebuchet
    private double scale = SCALE;


    /**
     * Constructor
     * use a hard-coded static data interface to initialize
     * so it can be easily run in an applet without using resources.
     */
    public Trebuchet(){
        commonInit();
    }

    public void reset() {
        RenderablePart.setAngularVelocity(0);
        commonInit();
    }

    private void commonInit() {
        logger = new Log();

        part = new RenderablePart[NUM_PARTS];

        double angle = PI/2.0 - asin(HEIGHT / DEFAULT_SLING_LEVER_LENGTH);
        RenderablePart.setAngle(angle);
        base = new Base();
        part[0] = base;
        lever = new Lever(DEFAULT_CW_LEVER_LENGTH, DEFAULT_SLING_LEVER_LENGTH);
        part[1] = lever;
        //System.out.println("cw mass="+DEFAULT_COUNTER_WEIGHT_MASS);
        counterWeight = new CounterWeight(lever, DEFAULT_COUNTER_WEIGHT_MASS);
        part[2] = counterWeight;
        projectile = new Projectile(DEFAULT_PROJECTILE_MASS);
        sling = new Sling(DEFAULT_SLING_LENGTH, DEFAULT_SLING_RELEASE_ANGLE, lever, projectile);
        part[3] = sling;
        part[4] = projectile;
    }

    /**
     * steps the simulation forward in time
     * if the timestep is too big inaccuracy and instability may result.
     * @return the new timestep
     */
    public double stepForward( double timeStep ) {
        //logger_.println(1, LOG_LEVEL, "stepForward: about to update (timeStep="+timeStep+')');

        double angle = RenderablePart.getAngle();
        double angularVelocity = RenderablePart.getAngularVelocity();
        double slingAngle = sling.getAngleWithLever();
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
        if (!projectile.isReleased()) {
            double tangentialForceAtHook = torque / lever.getSlingLeverLength();
            //System.out.println("tangentialForceAtHook="+tangentialForceAtHook);
            double slingAngleWithHorz = sling.getAngleWithHorz();

            forceFromHook_.set(-cos(slingAngleWithHorz), sin(slingAngleWithHorz));  //sin(PI - angle), -cos(PI + angle));
            forceFromHook_.scale( tangentialForceAtHook * sin(slingAngle));
            Vector2d gravityForce = new Vector2d(GRAVITY_VEC);
            gravityForce.scale(projectile.getMass());
            forceFromHook_.add(gravityForce);
            // also add a restoring force which is proportional to the distnace from the attachpoint on the sling
            // if we have not yet been released.

            Vector2d restoreForce = sling.getProjectileAttachPoint();
            restoreForce.sub(projectile.getPosition());
            restoreForce.scale(100.0);
            forceFromHook_.add(restoreForce);
            projectile.setForce(forceFromHook_, timeStep);
        }  else {
            Vector2d gravityForce = new Vector2d(GRAVITY_VEC);
            gravityForce.scale(projectile.getMass());
            projectile.setForce(gravityForce, timeStep);
        }


        // at the time when it is released, the only force acting on it will be gravity.
        if (!projectile.isReleased() && slingAngle >= (PI + sling.getReleaseAngle())) {
            System.out.println("##########################################################################");
            System.out.println("released!  slingAngle = "+slingAngle +" sling release angle = "+ sling.getReleaseAngle());
            System.out.println("##########################################################################");
            projectile.setReleased(true);
        }

        return timeStep;
    }

    private double calculateTorque(double angle, double slingAngle) {
        double primaryTorque = 0;
        if ( angle < MAX_LEVER_ANGLE) {
            primaryTorque = lever.getCounterWeightLeverLength() * sin(angle) * GRAVITY * counterWeight.getMass();
        }
        double dragTorque = 0;
        if (projectile.isOnRamp()) {
            // case when the projectile is still on the ramp
            dragTorque = -lever.getSlingLeverLength() * projectile.getMass() * GRAVITY * RAMP_FRICTION * sin(slingAngle);
        } else {
            // case when the projectile is no longer on the ramp
            double r = projectile.getDistanceFrom(lever.getFulcrumPosition());
            dragTorque = r * projectile.getMass() * GRAVITY * cos(PI - angle - slingAngle) * sin(angle) ;
        }
        //System.out.println("torque= primaryTorque("+primaryTorque+")" +
        //                   " + dragTorque("+dragTorque+")= "+(primaryTorque+dragTorque));
        return primaryTorque + dragTorque;
    }

    /**
     * Got this from a physics text
     * I = LEVER_MASS / 3  (b^3 + c^3) + projectileMass/3 * r^2
     * @return the calculated interia
     */
    private double calculateInertia() {
        return lever.getInertia() + projectile.getInertia(lever.getFulcrumPosition());
    }



    // api for tweaking Trebuchet params ////////////////////////////////////////

    public void setScale( double scale ) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public void setShowVelocityVectors( boolean show ) {
        showVelocityVectors = show;
    }

    public boolean getShowVelocityVectors() {
        return showVelocityVectors;
    }

    public void setShowForceVectors( boolean show ){
        showForceVectors = show;
    }

    public boolean getShowForceVectors() {
        return showForceVectors;
    }



    public double getCounterWeightLeverLength() {
        return lever.getCounterWeightLeverLength();
    }

    public void setCounterWeightLeverLength(double counterWeightLeverLength) {
        lever.setCounterWeightLeverLength(counterWeightLeverLength);
    }

    public double getSlingLeverLength() {
        return lever.getSlingLeverLength();
    }

    public void setSlingLeverLength(double slingLeverLength) {
        lever.setSlingLeverLength(slingLeverLength);
    }

    public double getCounterWeightMass() {
        return counterWeight.getMass();
    }

    public void setCounterWeightMass(double counterWeightMass) {
        this.counterWeight.setMass(counterWeightMass);
    }

    public double getSlingLength() {
        return sling.getLength();
    }

    public void setSlingLength(double slingLength) {
        this.sling.setLength(slingLength);
    }

    public double getProjectileMass() {
        return projectile.getMass();
    }

    public void setProjectileMass(double projectileMass) {
        this.projectile.setMass(projectileMass);
    }


    public double getSlingReleaseAngle() {
        return sling.getReleaseAngle();
    }

    public void setSlingReleaseAngle(double slingReleaseAngle) {
        this.sling.setReleaseAngle(slingReleaseAngle);
    }


    /**
     * Render the Environment on the screen
     */
    public void render( Graphics2D g ) {
        int i;

        g.setColor( Color.black ); // default

        // render each part
        for ( i = 0; i < NUM_PARTS; i++ ) {
            if (part[i] != null)
                part[i].render( g, getScale() );
        }
    }

}
