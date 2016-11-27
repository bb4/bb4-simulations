package com.barrybecker4.simulation.conway.model;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The data for points in the conway life simulation
 * @author Barry Becker
 */
class Conway {

    /** Since its on an infinite grid. Only store the grid locations where there his life. */
    private Map<Location, Integer> points;

    private boolean wrap = false;
    private int width = -1;
    private int height = -1;

    private static final List<Location> NBR_OFFSETS = Arrays.asList(new Location[] {
            new IntLocation(-1, -1), new IntLocation(-1, 0), new IntLocation(-1, 1),
            new IntLocation(0, -1), new IntLocation(0, 1),
            new IntLocation(1, -1), new IntLocation(1, 0), new IntLocation(1, 1)
    });

    Conway() {
        points = new ConcurrentHashMap<>();
    }

    void setWrapping(boolean wrap, int width, int height) {
        this.wrap = wrap;
        this.width = width;
        this.height = height;
    }
    public void initialize() {
        //genMap(100, 100);
        addGlider();
    }

    Set<Location> getCandidates() {
        Set<Location> candidates = new HashSet<>();
        for (Location c : points.keySet()) {
            candidates.add(keepInBounds(c));
            for (Location offset : NBR_OFFSETS) {
                candidates.add(keepInBounds(c.incrementOnCopy(offset)));
            }
        }
        return candidates;
    }

    private Location keepInBounds(Location c) {
        return wrap ? new IntLocation((c.getRow() + height) % height, (c.getCol() + width) % width) : c;
    }

    Set<Location> getPoints() {
        return points.keySet();
    }

    boolean isAlive(Location coord) {
        return points.containsKey(coord);
    }

    int getNumNeighbors(Location c) {
        int numNbrs = 0;
        for (Location offset : NBR_OFFSETS) {
            if (isAlive(keepInBounds(c.incrementOnCopy(offset)))) {
                numNbrs++;
            }
        }
        return numNbrs;
    }

    void setValue(Location coord, int value) {
        points.put(coord, value);
    }

    public Integer getValue(Location coord) {
        return points.get(coord);
    }

    private void addGlider() {
        setValue(new IntLocation(10, 10), 1);
        setValue(new IntLocation(11, 11), 1);
        setValue(new IntLocation(11, 12), 1);
        setValue(new IntLocation(10, 12), 1);
        setValue(new IntLocation(9, 12), 1);
    }

    /** generate the initial random 2D data */
    private void genMap(int width, int length) {
        Random RAND = new Random(1);
        points.clear();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                double r = RAND.nextDouble();
                if (r > 0.7) {
                    setValue(new IntLocation(y, x), 1);
                }
            }
        }
    }
}
