/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid1.ui;

import com.barrybecker4.simulation.fluid1.model.Grid;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handle mouse interactions - converting them in to physical manifestations.
 *
 * @author Barry Becker
 */
public class InteractionHandler implements MouseListener, MouseMotionListener {

    static final double DEFAULT_FORCE = 3.0f;
    static final double DEFAULT_SOURCE_DENSITY = 1.0f;

    private double force_ = DEFAULT_FORCE;
    private double sourceDensity_ = DEFAULT_SOURCE_DENSITY;

    private Grid grid;
    private double scale;

    private int currentX, currentY;
    private int lastX, lastY;
    private boolean mouse1Down, mouse3Down;

    /**
     * Constructor
     */
    InteractionHandler(Grid grid,  double scale) {
        this.scale = scale;
        this.grid = grid;
    }

    public void setForce(double force) {
        force_ = force;
    }

    public void setSourceDensity(double sourceDensity) {
        sourceDensity_ = sourceDensity;
    }


    /**
     * Make waves or adds ink when dragging depending on the mouse key held down.
     */
    public void mouseDragged(MouseEvent e) {

        currentX = e.getX();
        currentY = e.getY();
        int i = (int) (currentX / scale);
        int j = (int) (currentY / scale);

        // apply the change to a convolution kernel area
        int startX = Math.max(1, i - 1);
        int stopX = Math.min(grid.getWidth(), i + 1);
        int startY = Math.max(1, j - 1);
        int stopY = Math.min(grid.getHeight(), j + 1);


        for (int ii=startX; ii<stopX; ii++) {
             for (int jj=startY; jj<stopY; jj++) {
                 double weight = (ii == i && jj == j)? 1.0f : 0.3f;
                 applyChange(ii, jj, weight);
             }
        }

        lastX = currentX;
        lastY = currentY;
    }


    /**
     * Make waves or adds ink depending on which mouse key is being held down.
     */
    private void applyChange(int i, int j, double weight) {

        // if the left mouse is down, make waves
        if (mouse1Down) {
            double fu = (weight * force_ * (currentX - lastX) / scale);
            double fv = (weight *force_ * (currentY - lastY) / scale);
            grid.incrementU(i, j, fu);
            grid.incrementV(i, j, fv);
        }
        else if (mouse3Down) {
            // if the right mouse is down, add ink (density)
            grid.incrementDensity(i, j, weight * sourceDensity_);
        }
        else {
            System.out.println("dragged with no button down");
        }
    }

    public void mouseMoved(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        lastX = currentX;
        lastY = currentY;
    }

    /**
     * The following methods implement MouseListener
     */
    public void mouseClicked(MouseEvent e) {}

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
