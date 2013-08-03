// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.rendering;

import com.barrybecker4.simulation.snake.Snake;

import java.awt.*;

/**
 *  Responsible for drawing the snake onscreen.
 *  @author Barry Becker
 */
public class SnakeRenderer {

    private SegmentRenderer segmentRenderer;

    /**
     * Constructor
     */
    public SnakeRenderer(RenderingParameters params) {
        segmentRenderer = new SegmentRenderer(params);
    }

    /**
     * Render the Environment on the screen
     */
    public void render( Snake snake, Graphics2D g ) {

        int i;
        g.setColor( Color.black );

        // render each segment of the snake
        for ( i = 0; i < snake.getNumSegments(); i++ )
            segmentRenderer.render(snake.getSegment(i), g);
    }
}
