/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController;
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDColorMap;
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDOffscreenRenderer;
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDOnscreenRenderer;
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderer;
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions;

import java.awt.*;

/**
 * Reaction diffusion viewer.
 */
public class RDViewer {

    private static final int FIXED_SIZE_DIM = 250;

    private GrayScottController grayScott;
    private RDRenderer onScreenRenderer;
    private RDRenderer offScreenRenderer;

    private boolean useFixedSize_ = false;
    private boolean useOfflineRendering = false;

    private Container parent;
    private int oldWidth;
    private int oldHeight;

    private RDRenderingOptions renderOptions;
    private ColorMap cmap;


    /**
     * Constructor
     */
    RDViewer(GrayScottController grayScott, Container parent) {
        this.grayScott = grayScott;
        this.parent = parent;
        oldWidth = this.parent.getWidth();
        oldHeight = this.parent.getHeight();
        cmap = new RDColorMap();
        renderOptions = new RDRenderingOptions();
    }

    RDRenderingOptions getRenderingOptions() {
        return renderOptions;
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        useFixedSize_ = fixed;
    }

    public boolean getUseFixedSize() {
        return useFixedSize_;
    }

    public void setUseOffscreenRendering(boolean use) {
        useOfflineRendering = use;
    }

    public boolean getUseOffScreenRendering() {
        return useOfflineRendering;
    }

    public ColorMap getColorMap() {
        return cmap;
    }

    public void paint( Graphics g )  {
        checkDimensions();

        Graphics2D g2 = (Graphics2D) g;
        getRenderer().render(g2);
    }

    /**
     * Sets to new size if needed.
     */
    private void checkDimensions() {
        int w = FIXED_SIZE_DIM;
        int h = FIXED_SIZE_DIM;

        if (!useFixedSize_) {
            w = parent.getWidth();
            h = parent.getHeight();
        }
        initRenderers(w, h);
    }

    private void initRenderers(int w, int h) {
        if (w != oldWidth || h != oldHeight) {
            grayScott.setSize(w, h);
            onScreenRenderer = null;
            offScreenRenderer = null;
            oldWidth = w;
            oldHeight = h;
        }
    }

    private RDRenderer getOffScreenRenderer() {
        if (offScreenRenderer == null) {
           offScreenRenderer = new RDOffscreenRenderer(grayScott.getModel(), cmap, renderOptions, parent);
        }
        return offScreenRenderer;
    }

    private RDRenderer getOnScreenRenderer() {
        if (onScreenRenderer == null) {
           onScreenRenderer = new RDOnscreenRenderer(grayScott.getModel(), cmap, renderOptions);
        }
        return onScreenRenderer;
    }

    private RDRenderer getRenderer() {
        return  (useOfflineRendering) ? getOffScreenRenderer() : getOnScreenRenderer();
    }
}
