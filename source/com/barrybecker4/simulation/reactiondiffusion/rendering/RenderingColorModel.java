/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel;

import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * Determines pixel colors based on chemical concentrations and lighting models.
 *
 * @author Barry Becker
 */
public class RenderingColorModel {

    protected GrayScottModel model_;
    protected RDRenderingOptions options_;
    private ColorMap cmap_;

    /** the bigger this is the smaller the specular highlight will be. */
    private static final Color LIGHT_SOURCE_COLOR = Color.WHITE;


    /**
     * Constructor
     */
    RenderingColorModel(GrayScottModel model, ColorMap cmap, RDRenderingOptions options) {
        model_ = model;
        cmap_ = cmap;
        options_ = options;
    }

    public ColorMap getColorMap() {
        return cmap_;
    }

    /**
     * Get the color for a specific position based on chemical concentrations.
     * @return the color to use.
     */
    public Color getColorForPosition(int x, int y) {
        double concentration = getConcentration(x, y);
        return getColorForConcentration(concentration, x, y);
    }

    private double getConcentration(int x, int y) {

        return (options_.isShowingU() ? model_.getU(x, y) : 0.0)
              + (options_.isShowingV() ? model_.getV(x, y) : 0.0);
    }


    private Color getColorForConcentration(double concentration, int x, int y) {
        Color c = cmap_.getColorForValue(concentration);
        if (options_.getHeightScale() != 0) {
            c = adjustForLighting(c, concentration, x, y);
        }
        return c;
    }

    /**
     * Bump mapping is used to adjust the surface model before applying phong shading.
     * @param c color of surface
     * @param concentration the chemical concentration at this point.
     * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
     */
    private Color adjustForLighting(Color c, double concentration, int x, int y) {
        double xdelta = 0;
        double ydelta = 0;
        if (x < model_.getWidth() - 1) {
            xdelta = getConcentration(x+1, y) - concentration;
        }
        if (y < model_.getHeight() - 1) {
            ydelta = getConcentration(x, y+1) - concentration;
        }
        double htScale = options_.getHeightScale();
        Vector3d xVec = new Vector3d(1.0, 0.0, htScale * xdelta);
        Vector3d yVec = new Vector3d(0.0, 1.0, htScale * ydelta);
        Vector3d surfaceNormal = new Vector3d();
        surfaceNormal.cross(xVec, yVec);
        surfaceNormal.normalize();

        return computeColor(c, surfaceNormal);
    }

    /**
     * Diffuse the surface normal with the light source direction, to determine the shading effect.
     * @param color base color
     * @param surfaceNormal surface normal for lighting calculations.
     * @return color adjusted for lighting.
     */
    private Color computeColor(Color color, Vector3d surfaceNormal) {

        double diffuse = Math.abs(surfaceNormal.dot(RDRenderingOptions.LIGHT_SOURCE_DIR));
        double specular = options_.getSpecularExponent(surfaceNormal);

        Color cc = color.brighter();
        return new Color(
                (int)Math.min(255, cc.getRed() * diffuse + LIGHT_SOURCE_COLOR.getRed() * specular),
                (int)Math.min(255, cc.getGreen() * diffuse + LIGHT_SOURCE_COLOR.getGreen() * specular),
                (int)Math.min(255, cc.getBlue() * diffuse + LIGHT_SOURCE_COLOR.getBlue() * specular));
    }
}
