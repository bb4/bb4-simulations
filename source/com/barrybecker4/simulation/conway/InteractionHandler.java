/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.conway;

import com.barrybecker4.simulation.conway.model.ConwayModel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handle mouse interactions - converting them in to physical manifestations.
 * Using this handler, you can lower the gave walls.
 *
 * @author Barry Becker
 */
public class InteractionHandler implements MouseListener, MouseMotionListener {

    ConwayModel cave_;

    /** amount of the effect */
    double scale_;

    private int currentX, currentY;
    private int brushRadius = 1;
    private boolean mouse1Down, mouse3Down;

    /**
     * Constructor
     */
    public InteractionHandler(ConwayModel cave, double scale) {
        cave_ = cave;
        scale_ = scale;
    }

    public void setScale(double scale) {
        scale_ = scale;
    }

    /**
     * Lowers (or raises) cave walls when dragging.
     * Left mouse lowers; right mouse drag raises.
     */
    public void mouseDragged(MouseEvent e) {

        currentX = e.getX();
        currentY = e.getY();
        doBrush();
    }

    private void doBrush() {
        int i = (int) (currentX / scale_);
        int j = (int) (currentY / scale_);

        // apply the change to a convolution kernel area
        int startX = Math.max(1, i - brushRadius);
        int stopX = Math.min(cave_.getWidth(), i + brushRadius);
        int startY = Math.max(1, j - brushRadius);
        int stopY = Math.min(cave_.getHeight(), j + brushRadius);
        // adjust by this so that there is not a discontinuity at the periphery
        double minWt = 0.9 / brushRadius;

        for (int ii = startX; ii < stopX; ii++) {
             for (int jj=startY; jj<stopY; jj++) {
                 double weight = getWeight(i, j, ii, jj, minWt);
                 applyChange(ii, jj, weight);
             }
        }
        cave_.doRender();
    }

    /**
     * @return the weight is 1 / distance.
     */
    private double getWeight(int i, int j, int ii, int jj, double minWt) {
        double deltaX = (double)i - ii;
        double deltaY = (double)j - jj;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance < 0.5) {
            distance = 1.0;
        }
        return 1.0 / distance - minWt ;
    }

    /**
     * Make waves or adds ink depending on which mouse key is being held down.
     */
    private void applyChange(int i, int j, double weight) {

        double sign = 1;
        // if the left mouse is down, make waves
        if (mouse1Down) {
        }
        else if (mouse3Down) {
            sign = -1;
        }

        cave_.incrementHeight(i, j, sign * weight);
    }


    public void mouseMoved(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
    }

    /**
     * The following methods implement MouseListener
     */
    public void mouseClicked(MouseEvent e) {
        doBrush();
    }

    /**
     *Remember the mouse button that is pressed.
     */
    public void mousePressed(MouseEvent e) {
        mouse1Down = ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK);
        mouse3Down = ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK);
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}
