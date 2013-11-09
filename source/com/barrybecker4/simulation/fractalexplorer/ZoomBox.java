// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.common.geometry.Box;
import com.barrybecker4.common.geometry.IntLocation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Represents the zoom box used to zoom into a rectangular region.
 *
 * @author Barry Becker
 */
public class ZoomBox {

    private static final Color BOUNDING_BOX_COLOR = new Color(255, 100, 0);

    // corner positions while dragging.
    private IntLocation firstCorner;
    private IntLocation secondCorner;
    private Box box;

    public void setFirstCorner(int x, int y) {
        firstCorner = new IntLocation(y, x);
    }

    public void setSecondCorner(int x, int y) {
        secondCorner = new IntLocation(y, x);
    }

    public Box getBox() {
        return box; //new Box(firstCorner, secondCorner);
    }

    public void clearBox() {
        firstCorner = null;
        secondCorner = null;
    }

    public boolean isValidBox(){
        return box != null && firstCorner != null && !firstCorner.equals(secondCorner);
    }

    /**
     * Draw the bounding box if dragging.
     */
    public void render(Graphics g, double aspectRatio, boolean keepAspectRatio) {
        Graphics2D g2 = (Graphics2D) g;

        if (firstCorner == null || secondCorner == null) return;
        box = findBox(aspectRatio, keepAspectRatio);

        g2.setColor(BOUNDING_BOX_COLOR);
        IntLocation topLeft = box.getTopLeftCorner();
        g2.drawRect(topLeft.getX(), topLeft.getY(), box.getWidth(), box.getHeight());
    }

    private Box findBox(double aspectRatio, boolean keepAspectRatio) {
        Box box = new Box(firstCorner, secondCorner);

        IntLocation topLeft = box.getTopLeftCorner();
        int width = box.getWidth();
        int height = box.getHeight();

        if (keepAspectRatio)  {
            if (width > height) {
                height = (int)(width / aspectRatio);
            } else {
                width = (int)(height * aspectRatio);
            }
            box = new Box(topLeft, new IntLocation(topLeft.getY() + height, topLeft.getX() + width));
        }
        return box;
    }
}
