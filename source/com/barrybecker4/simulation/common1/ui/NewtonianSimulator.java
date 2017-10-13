/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common1.ui;

/**
 * Use this for Newtonian physics type simulations.
 *
 * @author Barry Becker
 */
public abstract class NewtonianSimulator extends Simulator {

    /**
     * @param name the name fo the simulator (eg Snake, Liquid, or Trebuchet)
     */
    public NewtonianSimulator(String name) {
        super(name);
    }

    public abstract void setShowVelocityVectors( boolean show );
    public abstract boolean getShowVelocityVectors();

    public abstract void setShowForceVectors( boolean show );
    public abstract boolean getShowForceVectors();

    public abstract void setDrawMesh( boolean use );
    public abstract boolean getDrawMesh();

    public abstract void setStaticFriction( double staticFriction );
    public abstract double getStaticFriction();

    public abstract void setDynamicFriction( double dynamicFriction );
    public abstract double getDynamicFriction();
}
