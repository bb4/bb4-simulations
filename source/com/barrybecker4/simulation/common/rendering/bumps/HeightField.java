package com.barrybecker4.simulation.common.rendering.bumps;

/**
 * @author Barry Becker
 */
public interface HeightField {

    int getWidth();

    int getHeight();

    /** @return the heigt value for the specified coordinates */
    double getValue(int x, int y);
}
