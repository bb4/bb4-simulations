package com.barrybecker4.simulation.conway.model;


import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;

import java.util.*;

/**
 * The data for points in the conway life simulation
 * @author Barry Becker
 */
public class Conway {

    /** Since its on an infinite grid. Only store the grid locations where there his life. */
    private Map<Location, Integer> points;

    private static final List<Location> NBR_OFFSETS = Arrays.asList(new Location[] {
            new IntLocation(-1, -1), new IntLocation(-1, 0), new IntLocation(-1, -1),
            new IntLocation(0, -1), new IntLocation(0, 1),
            new IntLocation(1, -1), new IntLocation(1, 0), new IntLocation(1, 1)
    });

    public Conway() {
        points = new HashMap<>();
    }

    public void initialize() {
        genMap(100, 100);
    }

    public int getNumPoints() {
        return points.keySet().size();
    }

    public Set<Location> getCandidates() {
        Set<Location> candidates = new HashSet<>();
        for (Location c : points.keySet()) {
            candidates.add(c);
            for (Location offset : NBR_OFFSETS) {
                candidates.add(c.incrementOnCopy(offset));
            }
        }
        return candidates;
    }

    public Set<Location> getPoints() {
        return points.keySet();
    }

    public boolean isAlive(Location coord) {
        return points.containsKey(coord);
    }

    public int getNumNeighbors(Location c) {
        int numNbrs = 0;
        for (Location offset : NBR_OFFSETS) {
            if (isAlive(c.incrementOnCopy(offset))) {
                numNbrs++;
            }
        }
        return numNbrs;
    }

    public void setValue(Location coord, int value) {
        points.put(coord, value);
    }

    public Integer getValue(Location coord) {
        return points.containsKey(coord) ? points.get(coord) : null;
    }

    /** generate the initial random 2D data */
    private void genMap(int width, int length) {
        Random RAND = new Random(1);
        points.clear();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                double r = RAND.nextDouble();
                if (r > 0.7) {
                    setValue(new IntLocation(x, y), 1);
                }
            }
        }
    }
}
