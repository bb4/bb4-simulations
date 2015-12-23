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
public class CaveProcessor {

    /** The density is the chance that a cell starts as part of the cave area (alive) */
    public static final double DEFAULT_DENSITY = .35;
    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_WIDTH = 32;
    /** cells die if less than this */
    public static final int DEFAULT_STARVATION_LIMIT = 4;
    /** Cells are born if more than this many neighbors */
    public static final int DEFAULT_BIRTH_THRESHOLD = 2;

    public enum KernelType {BASIC, RADIAL}
    public static final KernelType DEFAULT_KERNEL_TYPE = KernelType.BASIC;

    private int starvationLimit;
    private int birthThreshold;
    private Cave cave;
    private Kernel kernel;

    /** Default no argument constructor */
    public CaveProcessor() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /** Constructor that allows you to specify the dimensions of the cave */
    public CaveProcessor(int width, int height) {
        this(width, height,
                DEFAULT_DENSITY, DEFAULT_STARVATION_LIMIT, DEFAULT_BIRTH_THRESHOLD, KernelType.BASIC);
    }

    public CaveProcessor(int width, int height,
              double density, int starvationLimit, int birthThreshold, KernelType kernelType) {
        this.starvationLimit = starvationLimit;
        this.birthThreshold = birthThreshold;
        cave = new Cave(width, height, density);
        switch (kernelType) {
            case BASIC: kernel = new BasicKernel(cave); break;
            case RADIAL: kernel = new RadialKernel(cave); break;
        }
    }

    public int getWidth() {
        return cave.getWidth();
    }

    public int getHeight() {
        return cave.getHeight();
    }

    /**
     * Compute the next step of the simulation
     * The new value is at each point based on simulation rules:
     * - if a cell is alive but has too few neighbors, kill it.
     * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
     */
    public void nextPhase() {
        Cave newCave = cave.createCopy();
        // Loop over each row and column of the map
        for (int x = 0; x < cave.getWidth(); x++) {
            for (int y = 0; y < cave.getHeight()/*map[0].length*/; y++) {
                int neibNum = (int) kernel.countNeighbors(x, y);
                byte newValue;
                if (cave.isWall(x, y)) {
                    // if rock, it continues to be rock if enough neighbors (or too few)
                    newValue =  (neibNum < starvationLimit) ? Cave.NEW_FLOOR : Cave.WALL;
                }
                else {
                    // becomes rock if enough rock neighbors
                    newValue = (neibNum > birthThreshold) ? Cave.NEW_WALL : Cave.FLOOR;
                }
                newCave.setValue(x, y, newValue);
            }
        }
        cave = newCave;
    }

    public byte getValue (int x, int y) {
        return cave.getValue(x, y);
    }

    public boolean isWall(int x, int y) {
        return cave.isWall(x, y);
    }

    public void printCave() {
        cave.print();
    }

    public String toString() {
        return cave.toString();
    }

    public static void main(String[] args) {
        CaveProcessor cave = new CaveProcessor(32, 32, 0.35, 3, 2, KernelType.BASIC);
        cave.printCave();
        cave.nextPhase();
        cave.printCave();
    }

}
