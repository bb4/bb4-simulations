package com.barrybecker4.simulation.common.rendering.bumps;

import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * This can be used to apply bump map rendering to a height field.
 * The constants at the top could be parameters to the constructor.
 *
 * @author Barry Becker
 */
public class BumpMapper {

    /** The angle at which light will hit the height field surface. Must be normalized to length 1. */
    public static final Vector3d DEFAULT_LIGHT_SOURCE_DIR = new Vector3d(1.0, 1.0, 1.0);
    static {
        DEFAULT_LIGHT_SOURCE_DIR.normalize();
    }

    /** the bigger this is the smaller the specular highlight will be. */
    private static final Color DEFAULT_LIGHT_SOURCE_COLOR = Color.WHITE;

    /** the bigger this is the smaller the specular highlight will be. */
    private static final double SPECULAR_HIGHLIGHT_EXP = 4.0;

    //private static final Vector3d HALF_ANGLE;

    private static final double DEFAULT_SPECULAR_PERCENT = 0.1;


    /**
     * Bump mapping is used to adjust the surface model before applying phong shading.
     * @param c color of surface
     * @param field a height field used to perturb the normal.
     * @param htScale amount to scale the height field values by
     * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
     */
    public Color adjustForLighting(Color c, int x, int y,  HeightField field, double htScale) {
        return adjustForLighting(c, x, y, field, htScale, DEFAULT_SPECULAR_PERCENT, DEFAULT_LIGHT_SOURCE_DIR);
    }

    /**
     * Bump mapping is used to adjust the surface model before applying Phong shading.
     * @param c color of surface
     * @param field a height field used to perturb the normal.
     * @param htScale amount to scale the height field values by
     * @param specPercent amount of specular highlighting to add to Phong model
     * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
     */
    public Color adjustForLighting(Color c, int x, int y,
                                   HeightField field, double htScale, double specPercent, Vector3d lightSourceDir) {
        double xdelta = 0;
        double ydelta = 0;
        //double centerValue = field.getValue(x, y);

        double posXOffsetValue = field.getValue(Math.min(field.getWidth() - 1, x + 1), y);
        double negXOffsetValue = field.getValue(Math.max(0, x - 1), y);
        xdelta = posXOffsetValue - negXOffsetValue;

        double posYOffsetValue = field.getValue(x, Math.min(field.getHeight() - 1, y + 1));
        double negYOffsetValue = field.getValue(x, Math.max(0, y - 1));
        ydelta = posYOffsetValue - negYOffsetValue;

        Vector3d xVec = new Vector3d(1.0, 0.0, htScale * xdelta);
        Vector3d yVec = new Vector3d(0.0, 1.0, htScale * ydelta);
        Vector3d surfaceNormal = new Vector3d();
        surfaceNormal.cross(xVec, yVec);
        surfaceNormal.normalize();

        return computeColor(c, surfaceNormal, specPercent, lightSourceDir);
    }

    /**
     * Diffuse the surface normal with the light source direction, to determine the shading effect.
     * @param color base color
     * @param surfaceNormal surface normal for lighting calculations.
     * @param specPct amount of specular highlighting to add to phong model
     * @param lightSourceDir normalized unit vector to light source (it must be normalized to length 1)
     * @return color adjusted for lighting.
     */
    private Color computeColor(Color color, Vector3d surfaceNormal, double specPct, Vector3d lightSourceDir) {

        double diffuse = Math.abs(surfaceNormal.dot(lightSourceDir));
        double specular = getSpecularExponent(surfaceNormal, specPct, lightSourceDir);

        Color cc = color.brighter();
        return new Color(
            (int)Math.min(255, cc.getRed() * diffuse + DEFAULT_LIGHT_SOURCE_COLOR.getRed() * specular),
            (int)Math.min(255, cc.getGreen() * diffuse + DEFAULT_LIGHT_SOURCE_COLOR.getGreen() * specular),
            (int)Math.min(255, cc.getBlue() * diffuse + DEFAULT_LIGHT_SOURCE_COLOR.getBlue() * specular));
    }

    /**
     * @param specPct amount of specular highlighting to add to Phong model
     * @return specular contribution to add in
     */
    private double getSpecularExponent(Vector3d surfaceNormal, double specPct, Vector3d lightSourceDir) {
        double specular = 0;
        if (specPct > 0)  {
            Vector3d halfAngle = computeHalfAngle(lightSourceDir);
           specular = specPct * Math.pow(Math.abs(surfaceNormal.dot(halfAngle)), SPECULAR_HIGHLIGHT_EXP);
        }
        return specular;
    }

    private Vector3d computeHalfAngle(Vector3d lightSourceDir) {
        Vector3d halfAngle = new Vector3d(0, 0, 1);
        halfAngle.add(lightSourceDir);
        halfAngle.normalize();
        return halfAngle;
    }
}
