package com.barrybecker4.simulation.conway.model.kernal;

import com.barrybecker4.simulation.conway.model.Conway;

/**
 * Looks only at immediate neighbors
 * @author Barry Becker
 */
public class BasicKernel extends AbstractKernel {

    private static final double TOTAL_WEIGHT = 8;

    public BasicKernel(Conway conway) {
        super(conway);
    }

    public double countNeighbors(int x, int y) {

        double count = 0;
        for (int i = -1; i < 2; i++) {
            int neighborX = x + i;
            for (int j = -1; j < 2; j++) {
                int neighborY = y + j;
                // If we're looking at the middle point
                if (i == 0 && j == 0) {
                    // Do nothing, we don't want to add ourselves in!
                    continue;
                }
                // In case the index we're looking at it off the edge of the map, or a filled neighbor
                if (isOffEdge(neighborX, neighborY)) {
                    count += 1.0;
                }
                else {
                    count += cave.getValue(neighborX, neighborY);
                }
            }
        }
        return count / 8;
    }
}
