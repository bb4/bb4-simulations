package com.barrybecker4.simulation.cave1.model.kernal;

import com.barrybecker4.simulation.cave1.model.Cave;

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

    /** kernal size. Must be odd, like 3, 5, 7, 9, etc */
    private int size = 5;

    private double[][] distanceLookup;
    private double totalWeight;


    public RadialKernel(Cave cave, int size) {
        super(cave);
        assert size > 2&& size % 2 == 1;
        this.size = size;
        initDistanceLookup(size);
    }

    private void initDistanceLookup(int size) {
        distanceLookup = new double[size][size];
        double sum = 0;
        int range = size / 2;
        for (int i = 0; i <= range; i++)  {
            for (int j = 0; j <= range; j++) {
                double value = Math.sqrt(i*i + j*j);
                distanceLookup[i][j] = value;
                sum += (i==0 || j==0) ? 2 * value : 4 * value;
            }
        }

        totalWeight = sum;
    }

    public double countNeighbors(int x, int y) {

        double count = 0;
        int range = size / 2;

        for (int i = -range; i <= range; i++) {
            int neighborX = x + i;
            for (int j = -range; j <= range; j++) {
                int neighborY = y + j;
                double distance = distanceLookup[Math.abs(i)][Math.abs(j)];
                // If we're looking at the middle point
                if (i == 0 && j == 0) {
                    // Do nothing, we don't want to add ourselves in!
                    continue;
                }
                 if (isOffEdge(neighborX, neighborY)) {
                    count += distance;
                }
                else {
                    count += distance * cave.getValue(neighborX, neighborY);
                }
            }
        }
        return count / totalWeight;
    }

}
