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

    private boolean showVelocityVectors_ = false;
    private boolean showForceVectors_ = false;
    private boolean drawMesh_ = false;
    private double scale_ = SCALE;


    public void setScale( double scale ) {
        scale_ = scale;
    }

    public double getScale() {
        return scale_;
    }

    public void setShowVelocityVectors( boolean show ) {
        showVelocityVectors_ = show;
    }

    public boolean getShowVelocityVectors() {
        return showVelocityVectors_;
    }

    public void setShowForceVectors( boolean show ) {
        showForceVectors_ = show;
    }

    public boolean getShowForceVectors() {
        return showForceVectors_;
    }

    public void setDrawMesh( boolean use ) {
        drawMesh_ = use;
    }

    public boolean getDrawMesh() {
        return drawMesh_;
    }
}
