/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.common.math.LinearUtil;
import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.simulation.liquid.model.*;
import junit.framework.TestCase;

import javax.vecmath.Vector2d;

/**
 * @author Barry Becker
 */
public class ParticleAdvectorTest extends TestCase {

    /** delta time */
    private static final double DT = 0.1;
    private static final int DIM = 6;

    /** instance under test. */
    private ParticleAdvector particleAdvector;


    public void testAdvectInUpdateInAbsenceOfFlow() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL);
        particleAdvector = new ParticleAdvector(grid);

        Particles particles = createParticles(2.1, 2.5, grid);
        Particle part = particles.iterator().next();

        particleAdvector.advectParticles(DT, particles);

        assertEquals("Unexpected age for particle", 0.1, part.getAge());
        Vector2d pos =  new Vector2d(part.x, part.y);
        assertTrue("Unexpected new particle position: " + pos,
            LinearUtil.appxVectorsEqual(new Vector2d(2.1, 2.5), pos , MathUtil.EPS_MEDIUM));
    }

    public void testAdvectInUpdateInNorthEastFlow() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL);

        particleAdvector = new ParticleAdvector(grid);

        Particles particles = createParticles(2.1, 2.5, grid);
        Particle part = particles.iterator().next();

        double newDt = particleAdvector.advectParticles(DT, particles);

        assertEquals("Unexpected age for particle", 0.1, part.getAge());
        Vector2d pos =  new Vector2d(part.x, part.y);
        assertTrue("Unexpected new particle position: " + pos,
            LinearUtil.appxVectorsEqual(new Vector2d(2.105, 2.505), pos , MathUtil.EPS_MEDIUM));
        assertEquals("Timestep unexpectedly changed", DT, newDt);
    }


    public void testAdvectInUpdateInNorthEastNonUniformFlow() {

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL);

        particleAdvector = new ParticleAdvector(grid);

        Particles particles = createParticles(2.1, 2.5, grid);
        Particle part = particles.iterator().next();

        double newDt = particleAdvector.advectParticles(DT, particles);

        assertEquals("Unexpected age for particle", 0.1, part.getAge());
        Vector2d pos =  new Vector2d(part.x, part.y);
        assertTrue("Unexpected new particle position: " + pos,
            LinearUtil.appxVectorsEqual(new Vector2d(2.1021351463835316, 2.5021703194194225), pos , MathUtil.EPS_MEDIUM));
        assertEquals("Timestep unexpectedly changed", DT, newDt);
    }


    private Particles createParticles(double x, double y, Grid grid) {

        Particles particles = new Particles();
        particles.addParticle(x, y, grid);
        return particles;
    }
}
