// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.rendering;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.simulation.cave.model.Cave;
import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.simulation.common.rendering.bumps.BumpMapper;
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

    private final double width;
    private final double height;

    private CaveProcessor cave;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;
    private final BumpMapper bmapper;

    /** Constructor */
    public CaveRenderer(int width, int height, CaveProcessor cave)
            throws IllegalArgumentException {
        this.width = width;
        this.height = height;
        this.cave = cave;
        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), FLOOR_COLOR);
        bmapper = new BumpMapper();
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
     * Draw the floor of the cave
     */
    public void render(boolean useBumpmapping, double bumpHeight) {
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
                Color color = cmap.getColorForValue(value);
                if (useBumpmapping) {
                    color = bmapper.adjustForLighting(color, cave, bumpHeight, 1.0, i, j);
                }
                offlineGraphics_.setColor(color);
                offlineGraphics_.fillRect(xpos, ypos, (int)cellWidth, (int)cellHeight);
            }
        }
    }
}
