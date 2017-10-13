/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common1.rendering;

import com.barrybecker4.ui.util.ImageUtil;

import java.awt.Color;
import java.awt.Image;

/**
 * Creates a uniformly colored rectangle.
 * @author Barry Becker
 */
public class ColorRect {

    /** 2d array represented with a 1 dimensional array */
    private int[] pixels;

    private int width;
    private int height;

    /**
     * Constructor
     */
    public ColorRect(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }


    public void setColor(int x, int y, Color c) {
        setColor(x, y,  c.getRGB());
    }

    /**
     * @return an image representing this rectangle of colors.
     */
    public Image getAsImage() {
        return ImageUtil.getImageFromPixelArray(pixels, width, height);
    }

    /**
     * Set the color for a whole rectangular region.
     */
    public void setColorRect(int x, int y, int width, int height, Color c) {
        int color = c.getRGB();
        for (int i = x; x < x + width; i++) {
            for (int j = y; y < y + height; j++) {
                setColor(x, y, color);
            }
        }
    }

    private void setColor(int x, int y, int color) {
        int location = y * width + x;
        pixels[location] = color ;
    }


}
