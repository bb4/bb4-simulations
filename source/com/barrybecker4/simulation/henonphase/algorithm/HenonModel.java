/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase.algorithm;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.ui.renderers.OfflineGraphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything we need to know to compute the henon phase diagram.
 *
 * @author Barry Becker
 */
public class HenonModel  {

    private final int width;
    private final int height;

    private int numTravelors;
    private TravelerParams params;
    private boolean useUniformSeeds = true;
    private boolean connectPoints = false;

    private final List<Traveler> travelers;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;

    private ColorMap cmap_;


    public HenonModel(int width, int height, TravelerParams params,
                      boolean uniformSeeds, boolean connectPoints, int numTravelors, ColorMap cmap) {

        this.width = width;
        this.height = height;
        this.params = params;
        this.numTravelors = numTravelors;
        this.useUniformSeeds = uniformSeeds;
        this.connectPoints = connectPoints;
        this.cmap_ = cmap;

        travelers = new ArrayList<Traveler>(this.numTravelors);
        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), Color.BLACK);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void reset() {

        travelers.clear();

        double inc = 1.0 / numTravelors;
        double xpos = 0.0;

        for (int i=0; i < numTravelors; i++) {

            if (useUniformSeeds) {
                Color color = cmap_.getColorForValue(xpos);
                travelers.add(new Traveler(xpos, 0, color, params));
            }
            else {
                double randXPos = Math.random();
                Color color = cmap_.getColorForValue(randXPos);
                travelers.add(new Traveler(randXPos, 0, color, params));
            }
            xpos += inc;
        }
    }

    public BufferedImage getImage() {
        return offlineGraphics_.getOfflineImage();
    }

    /**
     * @param numSteps  number of steps to increment each traveler
     */
    public void increment(int numSteps) {

        for (final Traveler traveler : travelers) {

            offlineGraphics_.setColor(traveler.getColor());

            for (int i=0; i< numSteps; i++)   {
                int xpos = (int)(width * (traveler.getX()/2.0 + 0.5));
                int ypos = (int)(height * (traveler.getY()/2.0 + 0.5));

                if (connectPoints) {
                    int xposLast = (int)(width * (traveler.getLastX()/2.0 + 0.5));
                    int yposLast = (int)(height * (traveler.getLastY()/2.0 + 0.5));

                    offlineGraphics_.drawLine(xposLast, yposLast, xpos, ypos);
                }
                else {
                    offlineGraphics_.drawPoint(xpos, ypos);
                }

                traveler.increment();
            }
        }
    }

}
