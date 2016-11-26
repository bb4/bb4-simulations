// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.rendering;

import com.barrybecker4.common.geometry.Location;
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

    /** render the live cels on the grid */
    public void render() {

        for (Location c : processor.getPoints())  {
            int value = processor.getValue(c);
            Color color = cmap.getColorForValue(value);
            offlineGraphics_.setColor(color);
            offlineGraphics_.fillRect(c.getX(), c.getY(), 1, 1);
        }
    }
}
