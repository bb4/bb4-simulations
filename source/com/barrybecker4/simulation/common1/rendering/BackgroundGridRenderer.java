/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common1.rendering;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * Use this for drawing the background grid.
 *
 * @author Barry Becker
 */
public class BackgroundGridRenderer {

    private Color gridColor;

    /**
     * @param gridColor the color for the grid.
     */
    public BackgroundGridRenderer(Color gridColor) {
       this.gridColor = gridColor;
    }

    public void drawGridBackground(Graphics2D g2, double cellSize,
                                  int xDim, int yDim, Vector2d offset) {
        // draw the grid background
        g2.setColor( gridColor );
        int xMax = (int) (cellSize * xDim) - 1;
        int yMax = (int) (cellSize * yDim) - 1;
        int j;
        double pos = offset.y % cellSize;
        for ( j = 0; j <= yDim; j++ ) {
            int ht = (int) (pos + j * cellSize);
            g2.drawLine( 1, ht, xMax, ht );
        }
        pos = offset.x % cellSize;
        for ( j = 0; j <= xDim; j++ ) {
            int w = (int) (pos + j * cellSize);
            g2.drawLine( w, 1, w, yMax );
        }
    }

}
