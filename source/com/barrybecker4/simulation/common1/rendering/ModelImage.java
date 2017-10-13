/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common1.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.ui.util.ImageUtil;
import com.barrybecker4.simulation.common1.RectangularModel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Responsible for converting a model into an offscreen image.
 * This allows us to avoid threading issues when rendering because we
 * can draw the whole image at once.
 *
 * @author Barry Becker
 */
public class ModelImage {

    private RectangularModel model;
    private ColorMap cmap;
    private BufferedImage image;
    private int scale = 1;
    private boolean useLinearInterpolation = false;

    /**
     * constructor
     */
    public ModelImage(RectangularModel model, ColorMap cmap, int scale) {
        this.cmap = cmap;
        this.model = model;
        this.scale = scale;
        createBufferedImage();
    }

    /**
     * constructor
     */
    public ModelImage(RectangularModel model, ColorMap cmap) {
        this(model, cmap, 1);
    }

    public ColorMap getColorMap() {
        return cmap;
    }

    public void setUseLinearInterpolation(boolean useInterp) {
        useLinearInterpolation = useInterp;
    }

    /**
     * Update the global image with a new strip of just computed pixels.
     * @param lastRow row to render from
     * @param currentRow row to render to
     */
    public void updateImage(int lastRow, int currentRow)  {

        updateImageSizeIfNeeded();
        int width = model.getWidth();
        int rectHeight = (currentRow - lastRow);
        if (rectHeight <= 0) return;

        int[] pixels = new int[scale * width * scale * rectHeight];

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < rectHeight; y ++) {

                if (scale > 1) {
                    setColorRect(x, y, lastRow, pixels);
                }
                else {
                    Color c = cmap.getColorForValue(model.getValue(x, y + lastRow));
                    pixels[y * width + x] = c.getRGB();
                }
            }
        }

        image.setRGB(0, scale * lastRow, scale * width, scale * rectHeight, pixels, 0,
                scale * width);
    }


    /**
     * Determine the colors for a rectangular strip of pixels.
     */
    private void setColorRect(int xStart, int yStart, int lastRow, int[] pixels) {

        int width = scale * model.getWidth();
        int xScaledStart = scale * xStart;
        int yScaledStart = scale * yStart;
        int yPos = yStart + lastRow;

        if (useLinearInterpolation) {

            float[] colorLL = new float[4];
            float[] colorLR = new float[4];
            float[] colorUL = new float[4];
            float[] colorUR = new float[4];

            cmap.getColorForValue( model.getValue(xStart, yPos)).getComponents(colorLL);
            cmap.getColorForValue( model.getValue(xStart+1, yPos)).getComponents(colorLR);
            cmap.getColorForValue( model.getValue(xStart, yPos + 1)).getComponents(colorUL);
            cmap.getColorForValue( model.getValue(xStart + 1, yPos + 1)).getComponents(colorUR);

            for (int xx =0; xx < scale; xx++) {
                 for (int yy =0; yy < scale; yy++) {
                     double xrat = (double) xx / scale;
                     double yrat = (double) yy / scale;
                     Color c = ImageUtil.interpolate(xrat, yrat, colorLL, colorLR, colorUL, colorUR);

                     pixels[(yScaledStart + yy) * width + xScaledStart + xx] = c.getRGB();
                 }
            }
        }
        else {
            int color = cmap.getColorForValue(model.getValue(xStart, yPos)).getRGB();
            for (int xx = 0; xx < scale; xx++) {
               for (int yy =0; yy < scale; yy++) {
                    pixels[(yScaledStart + yy) * width + xScaledStart + xx] = color;
               }
            }
        }
    }

    /**
     * @return the accumulated image so far.
     */
    public Image getImage() {
        return image;
    }

    private void updateImageSizeIfNeeded() {

        if  (image.getWidth() != scale * model.getWidth() || image.getHeight() != scale * model.getHeight()) {
            createBufferedImage();
        }
    }

    /**
     *  Create the buffered image to draw into.
     */
    private void createBufferedImage() {
        image = new BufferedImage(scale * model.getWidth(), scale * model.getHeight(), BufferedImage.TYPE_INT_RGB);
    }
}
