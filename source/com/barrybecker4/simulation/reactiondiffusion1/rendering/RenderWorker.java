/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1.rendering;

import com.barrybecker4.simulation.common1.rendering.ColorRect;

import java.awt.*;

/**
 * Renders one of the rectangular strips.
 * @author Barry Becker
 */
public class RenderWorker implements Runnable {

    private int minX, maxX;
    private Graphics2D g2;
    private RDRenderer renderer;

    RenderWorker(int minX, int maxX, RDRenderer renderer, Graphics2D g2) {
        this.minX = minX;
        this.maxX = maxX;
        this.renderer = renderer;
        this.g2 = g2;
    }

    public void run() {
        if (maxX - minX > 0) {
            ColorRect colorRect = renderer.getColorRect(minX, maxX);
            renderer.renderStrip(minX, colorRect, g2);
        }
    }
}
