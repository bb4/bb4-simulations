/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering;

import com.barrybecker4.simulation.common.rendering.ColorRect;

import java.awt.*;

/**
 * Renders one of the rectangular strips.
 * @author Barry Becker
 */
public class RenderWorker implements Runnable {

    private int minX_, maxX_;
    private Graphics2D g2_;
    private RDRenderer renderer_;

    public RenderWorker(int minX, int maxX, RDRenderer renderer, Graphics2D g2) {
        minX_ = minX;
        maxX_ = maxX;
        renderer_ = renderer;
        g2_ = g2;
    }

    public void run() {

        ColorRect colorRect = renderer_.getColorRect(minX_, maxX_);
        renderer_.renderStrip(minX_, colorRect, g2_);
    }
}
