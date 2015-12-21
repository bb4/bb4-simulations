package com.barrybecker4.simulation.cave.model;

import java.util.Random;

/**
 * This Cave simulation program is based on work by Michael Cook
 * See http://gamedevelopment.tutsplus.com/tutorials/generate-random-cave-levels-using-cellular-automata--gamedev-9664
 * @author Brian Becker
 * @author Barry Becker
 */
public class CaveMap {

    /** The density is the chance that a cell starts as part of the cave area (alive) */
    public static final double DEFAULT_DENSITY = .35;
    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_WIDTH = 32;
    /** cells die if less than this */
    public static final int DEFAULT_STARVATION_LIMIT = 3;
    /** Cells are born if more than this many neighbors */
    public static final int DEFAULT_BIRTH_THRESHOLD = 2;

    private static final int SEED = 0;
    private static final Random RAND = new Random();

    private int width;
    private int height;
    private double density;
    private int starvationLimit;
    private int birthThreshold;
    /** boolean array. A true value indicates solid rock */
    private boolean[][] map;

    /** Default no argument constructor */
    public CaveMap() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /** Constructor that allows you to specify the dimensions of the cave */
    public CaveMap(int width, int height) {
        this(width, height, DEFAULT_DENSITY, DEFAULT_STARVATION_LIMIT, DEFAULT_BIRTH_THRESHOLD);
    }

    public CaveMap(int width, int height, double density, int starvationLimit, int birthThreshold) {
        this.width = width;
        this.height = height;
        this.density = density;
        this.starvationLimit = starvationLimit;
        this.birthThreshold = birthThreshold;
        map = genMap();
    }

    /**
     * Compute the next step of the simulation
     * The new value is at each point based on simulation rules:
     * - if a cell is alive but has too few neighbors, kill it.
     * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
     */
    public void nextPhase() {
        boolean[][] newMap = new boolean[width][height];
        // Loop over each row and column of the map
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                int neibNum = neighborCount(x, y);
                if (map[x][y]) {
                    // if rock, it continues to be rock if not enough neighbors
                    newMap[x][y] = neibNum > starvationLimit;
                }
                else {
                    // becomes rock if enough rock neighbors
                    newMap[x][y] = neibNum > birthThreshold;
                }
            }
        }
        map = newMap;
    }

    public boolean isWall(int x, int y) {
        return map[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void printMap() {
        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[x][y]) bldr.append("0");
                else bldr.append(" ");
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }

    /** generate the initial random 2D map data */
    private boolean[][] genMap() {
        RAND.setSeed(SEED);
        boolean[][] theMap = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                theMap[x][y] = RAND.nextDouble() < density;
            }
        }
        return theMap;
    }

    int neighborCount(int x, int y) {
        int count = 0;
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
                if (neighborX < 0 || neighborY < 0 ||
                    neighborX >= map.length || neighborY >= map[0].length ||
                    map[neighborX][neighborY]) {
                    count++;
                }
            }
        }
        return count;
    }

}
