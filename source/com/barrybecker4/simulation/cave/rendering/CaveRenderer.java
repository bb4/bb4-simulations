// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.rendering;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.simulation.cave.model.Cave;
import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.ui.renderers.OfflineGraphics;
import com.barrybecker4.ui.util.ColorMap;

import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Everything we need to know to compute the l-System tree.
 * Should make the tree automatically center.
 *
 * @author Barry Becker
 */
public class CaveRenderer {

    private static final Color FLOOR_COLOR = new Color(130, 255, 175);
    private static final Color CEIL_COLOR = new Color(70, 30, 10);

    private static final Color WALL_COLOR_LOW = new Color(80, 130, 10);
    private static final Color WALL_COLOR_MED = new Color(100, 160, 30);
    private static final Color WALL_COLOR_HIGH = new Color(120, 205, 75);

    private final double width;
    private final double height;

    private CaveProcessor cave;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;

    /** Constructor */
    public CaveRenderer(int width, int height, CaveProcessor cave)
            throws IllegalArgumentException {
        this.width = width;
        this.height = height;
        this.cave = cave;
        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), FLOOR_COLOR);
    }

    public int getWidth() {
        return (int) width;
    }
    public int getHeight() {
        return (int) height;
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

        double cellWidth = Math.max(1, (int)(width / cave.getWidth()));
        double cellHeight = Math.max(1, (int)(height / cave.getHeight()));
        Range range = cave.getRange();
        ColorMap cmap = new CaveColorMap(range);
        double ext = range.getExtent();

        for (int i = 0; i < cave.getWidth(); i++)  {
            for (int j = 0; j < cave.getHeight(); j++) {
                int xpos = (int) (i * cellWidth);
                int ypos = (int) (j * cellHeight);
                double value = cave.getValue(i, j);
                offlineGraphics_.setColor(cmap.getColorForValue(value));
                offlineGraphics_.fillRect(xpos, ypos, (int)cellWidth, (int)cellHeight);
            }
        }
    }

}
