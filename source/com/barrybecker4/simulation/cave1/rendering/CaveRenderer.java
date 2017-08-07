// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave1.rendering;

import com.barrybecker4.simulation.cave1.model.CaveProcessor;
import com.barrybecker4.simulation.common.rendering.bumps.BumpMapper;
import com.barrybecker4.ui.renderers.OfflineGraphics;
import com.barrybecker4.ui.util.ColorMap;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Draws the 2D cave model by applying the processor to it.
 *
 * @author Barry Becker
 */
public class CaveRenderer {

    private static final Color FLOOR_COLOR = new Color(130, 255, 175);

    private final double width;
    private final double height;

    private CaveProcessor cave;

    /** offline rendering is fast  */
    private OfflineGraphics offlineGraphics;

    private final BumpMapper bmapper;
    private ColorMap cmap;

    /** Constructor */
    public CaveRenderer(int width, int height, CaveProcessor cave, ColorMap cmap)
            throws IllegalArgumentException {
        this.width = width;
        this.height = height;
        this.cave = cave;
        this.cmap = cmap;
        offlineGraphics = new OfflineGraphics(new Dimension(width, height), FLOOR_COLOR);
        bmapper = new BumpMapper();
    }

    public int getWidth() {
        return (int) width;
    }
    public int getHeight() {
        return (int) height;
    }

    public BufferedImage getImage() {
        return offlineGraphics.getOfflineImage();
    }

    /**
     * Draw the floor of the cave.
     * Synchronized so we do not end up calling it multiple times from the same thread until processing is done.
     */
    public synchronized void render(double bumpHeight, double specularPct, double lightAzymuthAngle,
                                    double lightDescensionAngle) {
       double cellWidth = Math.max(1, (int)(width / cave.getWidth()));
        double cellHeight = Math.max(1, (int)(height / cave.getHeight()));

        Vector3d lightVector = bumpHeight > 0 ?
                computeSphericalCoordinateUnitVector(lightAzymuthAngle, lightDescensionAngle) : null;

        for (int i = 0; i < cave.getWidth(); i++)  {
            for (int j = 0; j < cave.getHeight(); j++) {
                int xpos = (int) (i * cellWidth);
                int ypos = (int) (j * cellHeight);
                double value = cave.getValue(i, j);
                Color color = cmap.getColorForValue(value);
                if (bumpHeight > 0) {
                    color = bmapper.adjustForLighting(color, i, j, cave, bumpHeight, specularPct, lightVector);
                }
                offlineGraphics.setColor(color);
                offlineGraphics.fillRect(xpos, ypos, (int)cellWidth, (int)cellHeight);
            }
        }
    }

    /**
     * See http://mathworld.wolfram.com/SphericalCoordinates.html
     * @param theta azymuthal angle in radians
     * @param phi angle of descension (pi/2 - elevation) in radians
     * @return unit vector defined by spherical coordinates
     */
    private Vector3d computeSphericalCoordinateUnitVector(double theta, double phi) {
        return new Vector3d(Math.cos(theta) * Math.sin(phi), Math.sin(theta) * Math.sin(phi), Math.cos(phi));
    }
}
