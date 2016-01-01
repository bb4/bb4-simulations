/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering;

import com.barrybecker4.simulation.common.rendering.bumps.BumpMapper;
import com.barrybecker4.simulation.common.rendering.bumps.HeightField;
import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel;

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
    private BumpMapper bmapper;
    private HeightField heightMap;


    /**
     * Constructor
     */
    RenderingColorModel(GrayScottModel model, ColorMap cmap, RDRenderingOptions options) {
        model_ = model;
        cmap_ = cmap;
        options_ = options;
        bmapper = new BumpMapper();
        heightMap = new HeightMap();
    }

    public ColorMap getColorMap() {
        return cmap_;
    }

    /**
     * Get the color for a specific position based on chemical concentrations.
     * @return the color to use.
     */
    public Color getColorForPosition(int x, int y) {

        double concentration = heightMap.getValue(x, y);
        Color c = cmap_.getColorForValue(concentration);
        if (options_.getHeightScale() != 0) {
            double htScale = options_.getHeightScale();
            c = bmapper.adjustForLighting(c, x, y, heightMap, htScale, options_.getSpecular(),
                    BumpMapper.DEFAULT_LIGHT_SOURCE_DIR);
        }

        return c;
    }

    private class HeightMap implements HeightField {

        public int getWidth() {
            return model_.getWidth();
        }

        public int getHeight() {
            return model_.getHeight();
        }

        public double getValue(int x, int y) {
            return (options_.isShowingU() ? model_.getU(x, y) : 0.0)
                    + (options_.isShowingV() ? model_.getV(x, y) : 0.0);
        }
    }
}
