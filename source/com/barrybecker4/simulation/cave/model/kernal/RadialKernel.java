package com.barrybecker4.simulation.cave.model.kernal;

import com.barrybecker4.simulation.cave.model.Cave;

/**
 * Looks only at all neighbors a distance of 2 away and weight them by 1/distance
 *
 *   1/4   1/rt3  1/2  1/rt3  1/4
 *   1/rt3 1/rt2   1   1/rt2  1/rt3
 *   1/2    1      0    1     1/2
 *   1/rt3  1/rt2  1    etc...
 *
 * @author Barry Becker
 */
public class RadialKernel extends AbstractKernel {

    private static final double RT2_INV = 0.70710678118;
    private static final double RT3_INV = 0.57735026919;
    private static final double TOTAL_WEIGHT = 5 + 4 * RT2_INV + 8 * RT3_INV;

    private double[][] DISTANCE_LOOKUP = {
            {0,     1,       0.5},
            {1,   RT2_INV, RT3_INV},
            {0.5, RT3_INV,   0.25}
    };

    public RadialKernel(Cave cave) {
         super(cave);
    }

    public double countNeighbors(int x, int y) {

        double count = 0;
        for (int i = -2; i <= 2; i++) {
            int neighborX = x + i;
            for (int j = -2; j <= 2; j++) {
                int neighborY = y + j;
                double distance = DISTANCE_LOOKUP[Math.abs(i)][Math.abs(j)];
                // If we're looking at the middle point
                if (i == 0 && j == 0) {
                    // Do nothing, we don't want to add ourselves in!
                    continue;
                }
                // In case the index we're looking at it off the edge of the cave, or a filled neighbor
                //if (isNbr(neighborX, neighborY)) {
                 if (isOnEdge(neighborX, neighborY)) {
                    count += distance;
                }
                else {
                    count += distance * cave.getValue(neighborX, neighborY);
                }
            }
        }
        return count / TOTAL_WEIGHT;
    }
}
