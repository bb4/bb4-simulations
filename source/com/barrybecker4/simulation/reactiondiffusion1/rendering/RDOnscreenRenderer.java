/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common1.rendering.ColorRect;
import com.barrybecker4.simulation.reactiondiffusion1.algorithm.GrayScottModel;

import java.awt.*;

/**
 * Renders the state of the GrayScottController model to the screen.
 * @author Barry Becker
 */
public class RDOnscreenRenderer extends RDRenderer {

    /**
     * Constructor
     */
    public RDOnscreenRenderer(GrayScottModel model, ColorMap cmap, RDRenderingOptions options) {
        super(model, cmap, options);
    }

    /**
     * Renders a rectangular strip of pixels.
     */
    @Override
    public void renderStrip(int minX, ColorRect rect, Graphics2D g2) {

        Image img = rect.getAsImage();
        if (g2 != null)  {
            g2.drawImage(img, minX, 0, null);
        }
    }

    @Override
    protected void postRender(Graphics2D g2) {}
}
