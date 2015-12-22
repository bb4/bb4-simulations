package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.simulation.cave.model.kernal.BasicKernel;
import com.barrybecker4.simulation.cave.model.kernal.Kernel;
import com.barrybecker4.simulation.cave.model.kernal.RadialKernel;

import java.util.Random;

/**
 * This Cave simulation program is based on work by Michael Cook
 * See http://gamedevelopment.tutsplus.com/tutorials/generate-random-cave-levels-using-cellular-automata--gamedev-9664
 * See http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
 * @author Brian Becker
 * @author Barry Becker
 */
public class CaveMap {

    /** The density is the chance that a cell starts as part of the cave area (alive) */
    public static final double DEFAULT_DENSITY = .35;
    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_WIDTH = 32;
    /** cells die if less than this */
    public static final int DEFAULT_STARVATION_LIMIT = 4;
    /** Cells are born if more than this many neighbors */
    public static final int DEFAULT_BIRTH_THRESHOLD = 2;

    private static final int SEED = 0;
    private static final Random RAND = new Random();

    public enum KernelType {BASIC, RADIAL}
    public static final KernelType DEFAULT_KERNEL_TYPE = KernelType.BASIC;

    private int width;
    private int height;
    private double density;
    private int starvationLimit;
    private int birthThreshold;
    /** boolean array. A true value indicates solid rock */
    private boolean[][] map;
    private Kernel kernel;

    /** Default no argument constructor */
    public CaveMap() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /** Constructor that allows you to specify the dimensions of the cave */
    public CaveMap(int width, int height) {
        this(width, height, DEFAULT_DENSITY, DEFAULT_STARVATION_LIMIT, DEFAULT_BIRTH_THRESHOLD, KernelType.BASIC);
    }

    public CaveMap(int width, int height,
                   double density, int starvationLimit, int birthThreshold, KernelType kernelType) {
        this.width = width;
        this.height = height;
        this.density = density;
        this.starvationLimit = starvationLimit;
        this.birthThreshold = birthThreshold;
        map = genMap();
        switch (kernelType) {
            case BASIC: kernel = new BasicKernel(map); break;
            case RADIAL: kernel = new RadialKernel(map); break;
        }
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
                int neibNum = (int) kernel.countNeighbors(x, y);
                if (map[x][y]) {
                    // if rock, it continues to be rock if enough neighbors (or too few)
                    newMap[x][y] = neibNum > starvationLimit; // || neibNum < 2;
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

    public static void main(String[] args) {
        CaveMap cave = new CaveMap(32, 32, 0.35, 3, 2, KernelType.BASIC);
        cave.printMap();
        cave.nextPhase();
        cave.printMap();
    }

}
