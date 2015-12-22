package com.barrybecker4.simulation.cave.model.kernal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class BasicKernalTest {

    /** instance under test */
    private BasicKernel kernel;

    private static final double EPS = 0.0000000000001;

    /** assertEquals that we get correct neighbor counts for different positions within the 5x5 cave. */
    @Test
    public void testNeighborCount() {

        boolean map[][] = new boolean[][] {
                {false, true, false, false, false},
                {true, false, false, false, false},
                {true, true, true, true, false},
                {false, true, false, false, false},
                {false, false, false, false, false}
        };
        kernel = new BasicKernel(map);

        assertEquals("count for 0,0", 7.0, kernel.countNeighbors(0, 0), EPS);
        assertEquals("count for 1,0", 6.0, kernel.countNeighbors(1, 0), EPS);
        assertEquals("count for 1,1", 5.0, kernel.countNeighbors(1, 1), EPS);
        assertEquals("count for 4,3", 3.0, kernel.countNeighbors(4, 3), EPS);
    }
}
