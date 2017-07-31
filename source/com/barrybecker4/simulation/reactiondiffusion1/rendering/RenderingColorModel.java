/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1.rendering;

import com.barrybecker4.simulation.common.rendering.bumps.BumpMapper;
import com.barrybecker4.simulation.common.rendering.bumps.HeightField;
import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.reactiondiffusion1.algorithm.GrayScottModel;

import java.awt.*;

/**
 * Determines pixel colors based on chemical concentrations and lighting models.
 *
 * @author Barry Becker
 */
public class RenderingColorModel {

    protected GrayScottModel model;
    protected RDRenderingOptions options;
    private ColorMap cmap;
    private BumpMapper bmapper;
    private HeightField heightMap;


    RenderingColorModel(GrayScottModel model, ColorMap cmap, RDRenderingOptions options) {
        this.model = model;
        this.cmap = cmap;
        this.options = options;
        bmapper = new BumpMapper();
        heightMap = new HeightMap();
    }

    public ColorMap getColorMap() {
        return cmap;
    }

    /**
     * Get the color for a specific position based on chemical concentrations.
     * @return the color to use.
     */
    Color getColorForPosition(int x, int y) {

        double concentration = heightMap.getValue(x, y);
        Color c = cmap.getColorForValue(concentration);
        if (options.getHeightScale() != 0) {
            double htScale = options.getHeightScale();
            c = bmapper.adjustForLighting(c, x, y, heightMap, htScale, options.getSpecular(),
                    BumpMapper.DEFAULT_LIGHT_SOURCE_DIR);
        }

        return c;
    }

    private class HeightMap implements HeightField {

        public int getWidth() {
            return model.getWidth();
        }

        public int getHeight() {
            return model.getHeight();
        }

        public double getValue(int x, int y) {
            return (options.isShowingU() ? model.getU(x, y) : 0.0)
                    + (options.isShowingV() ? model.getV(x, y) : 0.0);
        }
    }
}
