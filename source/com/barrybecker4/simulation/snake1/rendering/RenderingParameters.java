/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.rendering;

/**
 * Tweakable rendering parameters.
 *
 * @author Barry Becker
 */
public class RenderingParameters {

    /** scales the size of the snakes geometry */
    public static final double SCALE = 0.9;

    private boolean showVelocityVectors = false;
    private boolean showForceVectors = false;
    private boolean drawMesh = false;
    private double scale = SCALE;


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

    public void setShowForceVectors( boolean show ) {
        showForceVectors = show;
    }

    public boolean getShowForceVectors() {
        return showForceVectors;
    }

    public void setDrawMesh( boolean use ) {
        drawMesh = use;
    }

    public boolean getDrawMesh() {
        return drawMesh;
    }
}
