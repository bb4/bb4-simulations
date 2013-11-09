/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.common.math.ComplexNumberRange;
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Create a zoom box while dragging.
 * Maintain aspect if control key or shift key while dragging.
 *
 * @author Barry Becker
 */
public class ZoomHandler implements MouseListener, MouseMotionListener {

    FractalAlgorithm algorithm_;

    /** if control or shift key held down while dragging, maintain aspect ratio */
    private boolean keepAspectRatio = false;

    /** the physical representation of the dragged rectangle */
    private ZoomBox zoomBox;

    /**
     * Constructor
     */
    public ZoomHandler(FractalAlgorithm algorithm) {
        algorithm_ = algorithm;
        zoomBox = new ZoomBox();
    }

    /**
     * Remember the location of the mouse when pressed,
     * and determine if aspect ration should be preserved based on control/shit key.
     */
    public void mousePressed(MouseEvent e) {
        keepAspectRatio = determineIfKeepAspectRation(e);
        zoomBox.setFirstCorner(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {
        zoomBox.setSecondCorner(e.getX(), e.getY());
    }

    private boolean determineIfKeepAspectRation(MouseEvent e) {
       return e.isControlDown() || e.isShiftDown();
    }

    public void mouseReleased(MouseEvent e) {

        if (zoomBox.isValidBox())   {
            ComplexNumberRange range = algorithm_.getRange(zoomBox.getBox());
            algorithm_.setRange(range);
        }
        zoomBox.clearBox();
    }

    /**
     * Draw the bounding box rectangle when dragging.
     */
    public void render(Graphics g, double aspectRatio) {
        zoomBox.render(g, aspectRatio, keepAspectRatio);
    }

    // unused mouse interface methods
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
