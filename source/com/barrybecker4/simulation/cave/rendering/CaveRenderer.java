// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.rendering;

import com.barrybecker4.simulation.cave.model.CaveMap;
import com.barrybecker4.ui.renderers.OfflineGraphics;

import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Everything we need to know to compute the l-System tree.
 * Should make the tree automatically center.
 *
 * @author Barry Becker
 */
public class CaveRenderer {

    private static final Color WALL_COLOR = new Color(160, 100, 30);
    private static final Color FLOOR_COLOR = new Color(150, 190, 255);

    private final int width;
    private final int height;

    private CaveMap cave;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;

    /** Constructor */
    public CaveRenderer(int width, int height, CaveMap cave)
            throws IllegalArgumentException {
        this.width = width;
        this.height = height;
        this.cave = cave;
        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), WALL_COLOR);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void reset() {
    }

    public BufferedImage getImage() {
        return offlineGraphics_.getOfflineImage();
    }

    /**
     * draw the tree
     */
    public void render() {
        drawCave();
    }

    /**
     * Draw the floor of the cave
     */
    private void drawCave() {

        int cellWidth = Math.max(1, width / cave.getWidth());
        int cellHeight = Math.max(1, height / cave.getHeight());
        offlineGraphics_.setColor(FLOOR_COLOR);

        for (int i = 0; i < cave.getWidth(); i++)  {
            for (int j = 0; j < cave.getHeight(); j++) {
               if (!cave.isWall(i, j)) {
                   offlineGraphics_.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
               }
            }
        }
    }

}
