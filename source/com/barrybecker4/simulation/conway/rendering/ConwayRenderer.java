// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.rendering;

import com.barrybecker4.simulation.conway.model.ConwayProcessor;
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
public class ConwayRenderer {

    private static final Color FLOOR_COLOR = new Color(130, 255, 175);

    private final double width;
    private final double height;
    private ConwayProcessor processor;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;
    private ColorMap cmap;

    /** Constructor */
    public ConwayRenderer(int width, int height, ConwayProcessor processor, ColorMap cmap)
            throws IllegalArgumentException {
        this.width = width;
        this.height = height;
        this.processor = processor;
        this.cmap = cmap;
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
     * Draw the floor of the processor
     */
    public void render() {
       double cellWidth = Math.max(1, (int)(width / processor.getWidth()));
        double cellHeight = Math.max(1, (int)(height / processor.getHeight()));

        for (int i = 0; i < processor.getWidth(); i++)  {
            for (int j = 0; j < processor.getHeight(); j++) {
                int xpos = (int) (i * cellWidth);
                int ypos = (int) (j * cellHeight);
                double value = processor.getValue(i, j);
                Color color = cmap.getColorForValue(value);
                offlineGraphics_.setColor(color);
                offlineGraphics_.fillRect(xpos, ypos, (int)cellWidth, (int)cellHeight);
            }
        }
    }
}
