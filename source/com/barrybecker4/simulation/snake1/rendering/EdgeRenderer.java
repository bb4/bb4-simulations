/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.snake1.geometry.Edge;
import com.barrybecker4.simulation.snake1.geometry.Particle;

import java.awt.*;

/**
 *  Draws a snake edge (line geometry). It is modeled as a spring to simulate muscles.
 *
 *  @author Barry Becker
 */
public final class EdgeRenderer {

    private static final double EDGE_SCALE = 30.0;

    /** show the edge different colors depending on percentage stretched  ( one being 100% stretched)  */
    private static final double stretchVals_[] = {0.3, 0.9, 1.0, 1.1, 3.0};
    private static final Color stretchColors_[] = {
        new Color( 255, 0, 0, 200 ),
        new Color( 230, 120, 57, 250 ),
        new Color( 50, 90, 60, 250 ),
        new Color( 70, 120, 210, 200 ),
        new Color( 10, 10, 255, 100 )
    };
    private static final ColorMap stretchColorMap_ =
            new ColorMap( stretchVals_, stretchColors_ );

    private RenderingParameters renderParams;

    /**
     * Constructor
     */
    EdgeRenderer(RenderingParameters params) {
        renderParams = params;
    }

    public void render(Edge edge, Graphics2D g) {
        //graphics.setColor(EDGE_COLOR);
        g.setColor( stretchColorMap_.getColorForValue( edge.getLength() / edge.getRestingLength() ) );

        double ratio = edge.getRestingLength() / edge.getLength();
        double width = EDGE_SCALE * Math.max(0, (ratio - 0.95));
        BasicStroke stroke =
                new BasicStroke( (float) width );
        g.setStroke( stroke );
        double scale = renderParams.getScale();
        Particle part1 = edge.getFirstParticle();
        Particle part2 = edge.getSecondParticle();
        int x1 = (int) (scale * part1.x);
        int y1 = (int) (scale * part1.y);
        int x2 = (int) (scale * part2.x);
        int y2 = (int) (scale * part2.y);

        g.drawLine(x1, y1, x2, y2);
    }
}
