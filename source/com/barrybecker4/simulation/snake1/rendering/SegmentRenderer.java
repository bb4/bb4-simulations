/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.rendering;

import com.barrybecker4.simulation.snake1.geometry.Edge;
import com.barrybecker4.simulation.snake1.geometry.Particle;
import com.barrybecker4.simulation.snake1.geometry.Segment;

import java.awt.*;

/**
 *  Render a segment of a snakes body. It is composed of edges.
 *
 *  @author Barry Becker
 */
public class SegmentRenderer {

    // rendering attributes
    private static final Color FORCE_COLOR = new Color( 230, 0, 20, 100 );
    private static final Color FRICTIONAL_FORCE_COLOR = new Color( 50, 10, 0, 70 );
    private static final Color VELOCITY_COLOR = new Color( 80, 100, 250, 100 );

    private static final double VECTOR_SIZE = 130.0;
    private static final BasicStroke VECTOR_STROKE = new BasicStroke( 1 );

    private RenderingParameters renderParams = new RenderingParameters();
    private EdgeRenderer edgeRenderer;
    private Graphics2D g;

    /**
     * constructor for the head segment
     */
    public SegmentRenderer(RenderingParameters params) {
        renderParams = params;
        edgeRenderer = new EdgeRenderer(params);
    }

    /**
     * @param segment the segment to render.
     */
    public void render(Segment segment, final Graphics2D g)  {

        this.g = g;

        Edge[] edges = segment.getEdges();
        Particle[] particles = segment.getParticles();

        if (renderParams.getDrawMesh()) {
            for ( int i = 0; i < edges.length; i++ ) {
                if ( i != 3 ) edgeRenderer.render(edges[i], g);
            }
        }
        else {
            edgeRenderer.render(edges[0], g);
            edgeRenderer.render(edges[2], g);
        }

        if ( segment.isHead() ) edgeRenderer.render(edges[1], g);
        if ( segment.isTail() ) edgeRenderer.render(edges[3], g);

        // draw the force and velocity vectors acting on each particle
        if (renderParams.getShowForceVectors()) {
            renderForceVectors(particles);
        }

        if (renderParams.getShowVelocityVectors()) {
            renderVelocityVectors(particles);
        }
    }

    private void renderForceVectors(Particle[] particles) {
        g.setStroke( VECTOR_STROKE );
        g.setColor( FORCE_COLOR );
        double scale = renderParams.getScale();

        for (Particle particle : particles) {
            int x = (int) (scale * particle.x);
            int y = (int) (scale * particle.y);
            g.drawLine(x, y,
                    (int) (x + VECTOR_SIZE * particle.force.x),
                    (int) (y + VECTOR_SIZE * particle.force.y));
        }

        g.setColor( FRICTIONAL_FORCE_COLOR );
        for (Particle particle : particles) {
            g.drawLine((int) (scale * particle.x), (int) (scale * particle.y),
                    (int) (scale * particle.x + VECTOR_SIZE * particle.frictionalForce.x),
                    (int) (scale * particle.y + VECTOR_SIZE * particle.frictionalForce.y));
        }
    }

    private void renderVelocityVectors(Particle[] particles) {
        g.setStroke( VECTOR_STROKE );
        g.setColor( VELOCITY_COLOR );
        double scale = renderParams.getScale();

        for (Particle particle : particles) {
            g.drawLine((int) (scale * particle.x), (int) (scale * particle.y),
                    (int) (scale * particle.x + VECTOR_SIZE * particle.velocity.x),
                    (int) (scale * particle.y + VECTOR_SIZE * particle.velocity.y));
        }
    }
}