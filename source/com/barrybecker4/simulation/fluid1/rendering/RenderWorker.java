/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid1.rendering;

import com.barrybecker4.simulation.common.rendering.ModelImage;

/**
 * Renders one of the rectangular strips.
 * @author Barry Becker
 */
public class RenderWorker implements Runnable {

    private ModelImage modelImage;
    private int minY, maxY;

    RenderWorker(ModelImage modelImage, int minY, int maxY) {
        this.modelImage = modelImage;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void run() {
        modelImage.updateImage(minY, maxY);
    }
}
