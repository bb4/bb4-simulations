package com.barrybecker4.simulation.cave.model.kernal;

import com.barrybecker4.simulation.cave.model.Cave;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class BasicKernalTest {

    /** instance under test */
    private BasicKernel kernel;

    private static final double EPS = 0.0000000000001;

    private static final double FLOOR = 0.1;
    private static final double CEILING = 0.9;

    /** assertEquals that we get correct neighbor counts for different positions within the 5x5 cave. */
    @Test
    public void testNeighborCount() {

        Cave cave = new Cave(5, 5, FLOOR, CEILING);
        cave.setValue(0, 1, 0.2);
        cave.setValue(1, 0, 0.5);
        cave.setValue(2, 0, 0.9);
        cave.setValue(2, 1, 0.8);
        cave.setValue(2, 2, 0.1);
        cave.setValue(2, 3, 0.7);
        cave.setValue(3, 1, 0.6);

        kernel = new BasicKernel(cave);

        assertEquals("count for 0,0", 0.7606486480925898, kernel.countNeighbors(0, 0), EPS);    // 7.0
        assertEquals("count for 1,0", 0.7520196215146719, kernel.countNeighbors(1, 0), EPS);    // 6.0
        assertEquals("count for 1,1", 0.5960481515908456, kernel.countNeighbors(1, 1), EPS);    // 5.0
        assertEquals("count for 4,3", 0.796974165870401, kernel.countNeighbors(4, 3), EPS);
    }
}
