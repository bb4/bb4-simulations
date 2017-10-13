/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common1.rendering.ColorRect;
import com.barrybecker4.simulation.reactiondiffusion1.algorithm.GrayScottModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the state of the GrayScottController model to the screen.
 * @author Barry Becker
 */
public abstract class RDRenderer {

    protected GrayScottModel model;
    protected RDRenderingOptions options;
    private RenderingColorModel renderingModel;

    /**
     * Constructor
     */
    RDRenderer(GrayScottModel model, ColorMap cmap, RDRenderingOptions options) {
        this.model = model;
        this.options = options;
        renderingModel = new RenderingColorModel(model, cmap, options);
    }

    public ColorMap getColorMap() {
        return renderingModel.getColorMap();
    }

    /**
     * Draw the model representing the current state of the GrayScottController rd implementation.
     */
    public void render(Graphics2D g2) {

        int width = model.getWidth();

        int numProcs = options.getParallelizer().getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);
        int range = (width / numProcs);
        for (int i = 0; i < (numProcs - 1); i++) {
            int offset = i * range;
            workers.add(new RenderWorker(offset, offset + range, this, g2));
        }
        // leftover in the last strip, or all of it if only one processor.
        int currentX = range * (numProcs - 1);
        //System.out.println("range="+range +" last strip="+ (width - currentX));
        workers.add(new RenderWorker(currentX, width, this, g2));

        // blocks until all Callables are done running.
        options.getParallelizer().invokeAllRunnables(workers);

        postRender(g2);
    }

    /**
     * Determine the colors for a rectangular strip of pixels.
     * @return array of colors that will be used to define an image for quick rendering.
     */
    public ColorRect getColorRect(int minX, int maxX) {
        int ymax = model.getHeight();
        ColorRect colorRect = new ColorRect(maxX-minX, ymax);
        for (int x = minX; x < maxX; x++) {
            for (int y = 0; y < ymax; y++) {
                colorRect.setColor(x-minX, y, renderingModel.getColorForPosition(x, y));
            }
        }
        return colorRect;
    }

    /**
     * Renders a rectangular strip of pixels.
     * This is fast and does not need to be synchronized.
     */
    public abstract void renderStrip(int minX, ColorRect rect, Graphics2D g2);

    /**
     * Follow up step after rendering. Does nothing by default.
     */
    protected abstract void postRender(Graphics2D g2);
}
