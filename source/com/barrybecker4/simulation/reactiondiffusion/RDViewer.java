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

    private GrayScottController grayScott_;
    private RDRenderer onScreenRenderer_;
    private RDRenderer offScreenRenderer_;

    private boolean useFixedSize_ = false;
    private boolean useOfflineRendering = false;

    private Container parent_;
    private int oldWidth;
    private int oldHeight;

    RDRenderingOptions renderOptions_;
    ColorMap cmap_;


    /**
     * Constructor
     */
    public RDViewer(GrayScottController grayScott, Container parent) {
        grayScott_ = grayScott;
        parent_ = parent;
        oldWidth = parent_.getWidth();
        oldHeight = parent_.getHeight();
        cmap_ = new RDColorMap();
        renderOptions_ = new RDRenderingOptions();
    }

    public RDRenderingOptions getRenderingOptions() {
        return renderOptions_;
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
        return cmap_;
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
            w = parent_.getWidth();
            h = parent_.getHeight();
        }
        initRenderers(w, h);
    }

    private void initRenderers(int w, int h) {
        if (w != oldWidth || h != oldHeight) {
            grayScott_.setSize(w, h);
            onScreenRenderer_ = null;
            offScreenRenderer_ = null;
            oldWidth = w;
            oldHeight = h;
        }
    }

    private RDRenderer getOffScreenRenderer() {
        if (offScreenRenderer_ == null) {
           offScreenRenderer_ = new RDOffscreenRenderer(grayScott_.getModel(), cmap_, renderOptions_, parent_);
        }
        return offScreenRenderer_;
    }

    private RDRenderer getOnScreenRenderer() {
        if (onScreenRenderer_ == null) {
           onScreenRenderer_ = new RDOnscreenRenderer(grayScott_.getModel(), cmap_, renderOptions_);
        }
        return onScreenRenderer_;
    }

    private RDRenderer getRenderer() {
        return  (useOfflineRendering) ? getOffScreenRenderer() : getOnScreenRenderer();
    }
}
