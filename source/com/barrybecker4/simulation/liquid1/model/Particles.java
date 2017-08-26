/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

import java.util.HashSet;
import java.util.Random;

/**
 *  A set of particles in a grid. These particles represent the fluid.
 *
 *  @author Barry Becker
 */
public class Particles extends HashSet<Particle> {

    /** ensure that the runs are the same  */
    private static final Random RANDOM = new Random(1);

    /**
     * @param x cell location x
     * @param y cell location y
     * @param numParticles  number of particles to add.
     * @param grid  some grid to add to.
     */
    public void addRandomParticles( double x, double y, int numParticles, Grid grid)  {

        for ( int i = 0; i < numParticles; i++ ) {
            addParticle(x + RANDOM.nextDouble(), y + RANDOM.nextDouble(), grid);
        }
    }

    public void addParticle( double x, double y, Grid grid) {

        Cell cell = grid.getCell((int)x, (int)y);
        Particle p = new Particle( x, y, cell);
        this.add(p);
        cell.incParticles();
    }

}
