/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.*;
import junit.framework.TestCase;
import org.junit.Test;

import javax.vecmath.Vector2d;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Barry Becker
 */
public class VelocityInterpolatorTest  {

    private static final int DIM = 6;

    private static final double[][] TEST_COORDS = {
        {2.1, 3.7}, {2.0, 2.0}, {2.5, 2.0}, {2.0, 2.5}, {2.1, 2.9}, {3.5, 2.0}
    };

    /** instance under test. */
    private VelocityInterpolator interpolator;

    /*
    @Test
    public void testUniformEastFlowInterpolation() {

        Vector2d velocity = new Vector2d(1.0, 0.0);
        verifyUniformField(velocity);
    }

    @Test
    public void testUniformNorthFlowInterpolation() {

        Vector2d velocity = new Vector2d(0.0, 1.0);
        verifyUniformField(velocity);
    }

    @Test
    public void testUniformSouthFlowInterpolation() {

        Vector2d velocity = new Vector2d(0.0, -1.0);
        verifyUniformField(velocity);
    }

    @Test
    public void testUniformNorthEastFlowInterpolation() {

        Vector2d velocity = new Vector2d(1.0, 1.0);
        verifyUniformField(velocity);
    }  */

    private void verifyUniformField(Vector2d expectedVelocity) {

        Grid grid = new UniformGrid(DIM, DIM, expectedVelocity);

        interpolator = new VelocityInterpolator(grid);

        for (double[] testCoord : TEST_COORDS) {
            double x = testCoord[0];
            double y = testCoord[1];
            Cell cell = grid.getCell((int) x, (int) y);

            Particle point = new Particle(x, y, cell);
            Vector2d vel = interpolator.findVelocity(point);
            assertEquals("Unexpected interpolated velocity for point " + point, expectedVelocity, vel);
        }
    }

    @Test
    public void testInterpolateVelocities() {
        CellBlock cb = new CellBlock();
        Particle particle;
        cb.setPressures(1.0);
        cb.setAllCellParticles(5);

        Cell cell = cb.get(0,0);
        //particle = new Particle(1.1, 1.1, cell);
        //verifyParticleVelocity(particle, cb,  new Vector2d(0.0, 0.0));

        cb.get(-1, 1).initializeVelocity(1.0, 0.0);  // upper left
        cb.get(0, 1).initializeVelocity(.9, 0.0);     // upper middle
        cb.get(-1, 0).initializeVelocity(.91, 1.0);  // middle left
        cb.get(0, 0).initializeVelocity(0.7, 0.7);   // center
        cb.get(1, 0).initializeVelocity(0.0, 0.4);   // right middle
        cb.get(-1, -1).initializeVelocity(0.5, 0.6);   // left bottom
        cb.get(0, -1).initializeVelocity(0.3, 0.3);    // middle bottom
        cb.get(1, -1).initializeVelocity(.1, 0.0);   // right bottom

        particle = new Particle(1.1, 1.1, cell);  // lower left
        verifyParticleVelocity(particle, cb,  new Vector2d(0.7254, 0.46));

        particle = new Particle(1.9, 1.1, cell);  // lower right
        verifyParticleVelocity(particle, cb,  new Vector2d(0.5606, 0.22));

        particle = new Particle(1.1, 1.9, cell); // upper left
        verifyParticleVelocity(particle, cb,  new Vector2d(0.9294, 0.78));

        particle = new Particle(1.9, 1.9, cell); // upper right
        verifyParticleVelocity(particle, cb,  new Vector2d(0.7966, 0.54));

        particle = new Particle(1.5, 1.5, cell); // center
        verifyParticleVelocity(particle, cb,  new Vector2d(0.805, 0.5));

        cb.setVelocities(0.6, 0.7);
        particle = new Particle(1.1, 1.1, cell);
        verifyParticleVelocity(particle, cb,  new Vector2d(0.6, 0.7));
    }

    private void verifyParticleVelocity(Particle particle, CellBlock cb, Vector2d expectedVelocity) {

        Cell cell = cb.getAbsolute(1, 1);
        int i = (int) particle.x;
        int j = (int) particle.y;
        if (i>2 || j>2)
            System.out.println( "i="+i+" j="+j);
        assert (i<3 && j<3): "i="+i+" j="+j;

        int ii = ((particle.x - i) > 0.5) ? (i + 1) : (i - 1);
        int jj = ((particle.y - j) > 0.5) ? (j + 1) : (j - 1);
         if (ii>2 || jj>2)
             System.out.println( "ii="+ii+" jj="+jj);
        System.out.println( "i="+i+" j="+j +  "    ii="+ii+" jj="+jj);

        VelocityInterpolator interpolator = new VelocityInterpolator(null);
        Vector2d  vel =
                interpolator.interpolateVelocity( particle, cell,
                        cb.getAbsolute(ii, j), cb.getAbsolute(i, jj),
                        cb.getAbsolute(i - 1, j), cb.getAbsolute(i - 1, jj), // u
                        cb.getAbsolute(i, j - 1), cb.getAbsolute(ii, j - 1));  // v

        if (!vel.epsilonEquals(expectedVelocity, 0.00000000001))
            System.out.println("vel for "+particle+" was "+ vel);
        //Assert.assertTrue("vel for particle "+particle +" was "+
        // vel, vel.epsilonEquals(expectedVelocity, 0.00000000001));
    }

    private double getRandomCoord() {
        return 1 +  Math.random() * (DIM - 2);
    }
}

