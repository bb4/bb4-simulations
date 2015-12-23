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

    /** assertEquals that we get correct neighbor counts for different positions within the 5x5 cave. */
    @Test
    public void testNeighborCount() {

        Cave cave = new Cave(5, 5, 0);
        cave.setValue(0, 1, Cave.WALL);
        cave.setValue(1, 0, Cave.WALL);
        cave.setValue(2, 0, Cave.WALL);
        cave.setValue(2, 1, Cave.WALL);
        cave.setValue(2, 2, Cave.WALL);
        cave.setValue(2, 3, Cave.WALL);
        cave.setValue(3, 1, Cave.WALL);

        kernel = new BasicKernel(cave);

        assertEquals("count for 0,0", 7.0, kernel.countNeighbors(0, 0), EPS);
        assertEquals("count for 1,0", 6.0, kernel.countNeighbors(1, 0), EPS);
        assertEquals("count for 1,1", 5.0, kernel.countNeighbors(1, 1), EPS);
        assertEquals("count for 4,3", 3.0, kernel.countNeighbors(4, 3), EPS);
    }
}
