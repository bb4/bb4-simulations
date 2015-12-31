package com.barrybecker4.simulation.common.rendering.bumps;

import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions;

import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * This can be used to apply bump map rendering to a height field
 *
 * @author Barry Becker
 */
public class BumpMapper {

    /** the bigger this is the smaller the specular highlight will be. */
    private static final Color LIGHT_SOURCE_COLOR = Color.WHITE;

    /** the bigger this is the smaller the specular highlight will be. */
    private static final double SPECULAR_HIGHLIGHT_EXP = 4.0;

    private static final Vector3d HALF_ANGLE;

    public static final Vector3d LIGHT_SOURCE_DIR = new Vector3d(1.0, 1.0, 1.0);

    static {
        LIGHT_SOURCE_DIR.normalize();
        HALF_ANGLE = new Vector3d(0, 0, 1);
        HALF_ANGLE.add(LIGHT_SOURCE_DIR);
        HALF_ANGLE.normalize();
    }

    /**
     * Bump mapping is used to adjust the surface model before applying phong shading.
     * @param c color of surface
     * @param field a height field used to perturb the normal.
     * @param htScale amount to scale the height field values by
     * @param specExp the specular exponent to use.
     * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
     */
    public Color adjustForLighting(Color c, HeightField field, double htScale, double specExp, int x, int y) {
        double xdelta = 0;
        double ydelta = 0;
        double centerValue = field.getValue(x, y);
        if (x < field.getWidth() - 1) {
            xdelta = field.getValue(x + 1, y) - centerValue;
        }
        if (y < field.getHeight() - 1) {
            ydelta = field.getValue(x, y + 1) - centerValue;
        }

        Vector3d xVec = new Vector3d(1.0, 0.0, htScale * xdelta);
        Vector3d yVec = new Vector3d(0.0, 1.0, htScale * ydelta);
        Vector3d surfaceNormal = new Vector3d();
        surfaceNormal.cross(xVec, yVec);
        surfaceNormal.normalize();

        return computeColor(c, surfaceNormal, specExp);
    }

    /**
     * Diffuse the surface normal with the light source direction, to determine the shading effect.
     * @param color base color
     * @param surfaceNormal surface normal for lighting calculations.
     * @return color adjusted for lighting.
     */
    private Color computeColor(Color color, Vector3d surfaceNormal, double specExp) {

        double diffuse = Math.abs(surfaceNormal.dot(LIGHT_SOURCE_DIR));
        double specular = getSpecularExponent(surfaceNormal, specExp);

        Color cc = color.brighter();
        return new Color(
                (int)Math.min(255, cc.getRed() * diffuse + LIGHT_SOURCE_COLOR.getRed() * specular),
                (int)Math.min(255, cc.getGreen() * diffuse + LIGHT_SOURCE_COLOR.getGreen() * specular),
                (int)Math.min(255, cc.getBlue() * diffuse + LIGHT_SOURCE_COLOR.getBlue() * specular));
    }

    public double getSpecularExponent(Vector3d surfaceNormal, double specExp) {
        double specular = 0;
        if (specExp > 0)  {
           specular = specExp * Math.pow(Math.abs(surfaceNormal.dot(HALF_ANGLE)), SPECULAR_HIGHLIGHT_EXP);
        }
        return specular;
    }
}
