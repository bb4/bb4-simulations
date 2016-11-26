package com.barrybecker4.simulation.conway.model;

import com.barrybecker4.common.concurrency.RunnableParallelizer;
import com.barrybecker4.common.math.Range;
import com.barrybecker4.simulation.cave.model.Cave;
import com.barrybecker4.simulation.common.rendering.bumps.HeightField;

import java.util.ArrayList;
import java.util.List;

/**
 * This Cave simulation program is based on work by Michael Cook
 * See http://gamedevelopment.tutsplus.com/tutorials/generate-random-cave-levels-using-cellular-automata--gamedev-9664
 * See http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
 *
 * Using concurrency in 4 core (8 thread) processor increases speed from 3.7 fps to about 6.6 fps.
 * But much of that is because of rendering time. We could use parallel rendering to make that faster too.
 * @author Brian Becker
 * @author Barry Becker
 */
public class ConwayProcessor implements HeightField {

    /** The density is the chance that a cell starts as part of the cave area (alive) */
    public static final double DEFAULT_FLOOR_THRESH = .2;
    public static final double DEFAULT_CEIL_THRESH = .8;

    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_WIDTH = 32;

    /** cells die if less than this */
    public static final double DEFAULT_LOSS_FACTOR = 0.5;
    public static final boolean DEFAULT_USE_PARALLEL = true;

    /** Cells are born if more than this many neighbors */
    public static final double DEFAULT_EFFECT_FACTOR = 0.2;

    public enum KernelType {BASIC, RADIAL3, RADIAL5, RADIAL7, RADIAL9,
        RADIAL11, RADIAL13, RADIAL15, RADIAL17, RADIAL19}
    public static final KernelType DEFAULT_KERNEL_TYPE = KernelType.RADIAL9;

    private double lossFactor;
    private double effectFactor;
    private Cave cave;
    /** Manages the worker threads. */
    private RunnableParallelizer parallelizer;

    /** Default no argument constructor */
    public ConwayProcessor() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /** Constructor that allows you to specify the dimensions of the cave */
    public ConwayProcessor(int width, int height) {
        this(width, height,
           DEFAULT_FLOOR_THRESH, DEFAULT_CEIL_THRESH, DEFAULT_LOSS_FACTOR, DEFAULT_EFFECT_FACTOR, DEFAULT_USE_PARALLEL);
    }

    public ConwayProcessor(int width, int height,
                           double floorThresh, double ceilThresh, double lossFactor, double effectFactor, boolean useParallel) {
        this.lossFactor = lossFactor;
        this.effectFactor = effectFactor;
        cave = new Cave(width, height, floorThresh, ceilThresh);
        setUseParallel(useParallel);
    }

    public int getWidth() {
        return cave.getWidth();
    }

    public int getHeight() {
        return cave.getLength();
    }

    public void setLossFactor(double loss) {
        lossFactor = loss;
    }

    public void setEffectFactor(double scale) {
        effectFactor = scale;
    }

    public void setFloorThresh(double floor) {
       cave.setFloorThresh(floor);
    }

    public void setCeilThresh(double ceil) {
       cave.setCeilThresh(ceil);
    }

    public void incrementHeight(int x, int y, double amount) {
        cave.incrementHeight(x, y, amount);
    }

    public void setUseParallel(boolean parallelized) {
        parallelizer =
             parallelized ? new RunnableParallelizer() : new RunnableParallelizer(1);
    }

    /**
     * Compute the next step of the simulation
     * The new value is at each point based on simulation rules:
     * - if a cell is alive but has too few neighbors, kill it.
     * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
     */
    public void nextPhase() {
        Cave newCave = cave.createCopy();

        int numThreads = parallelizer.getNumThreads();
        List<Runnable> workers = new ArrayList<>(numThreads + 1);
        int range = cave.getWidth() / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int offset = i * range;
            workers.add(new Worker(offset, offset + range, newCave));
        }

        // blocks until all Callables are done running.
        parallelizer.invokeAllRunnables(workers);

        cave = newCave;
    }

    /**
     * Compute the next step of the simulation
     * The new value is at each point based on simulation rules:
     * - if a cell is alive but has too few neighbors, kill it.
     * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
     */
    public void nextPhase(int minX, int maxX, Cave newCave) {

        // Loop over each row and column of the map
        for (int x = minX; x < maxX; x++) {
            for (int y = 0; y < cave.getLength(); y++) {
                double neibNum = 1; //kernel.countNeighbors(x, y);
                double oldValue = cave.getValue(x, y);
                double newValue = oldValue + (neibNum - lossFactor) * effectFactor;
                newCave.setValue(x, y, newValue);
            }
        }
        cave = newCave;
    }

    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {
        private int minX_, maxX_;
        private Cave newCave_;

        public Worker(int minX, int maxX, Cave newCave) {
            minX_ = minX;
            maxX_ = maxX;
            newCave_ = newCave;
        }

        @Override
        public void run() {
            nextPhase(minX_, maxX_, newCave_);
        }
    }


    public double getValue (int x, int y) {
        return cave.getValue(x, y);
    }

    public Range getRange() {
        return cave.getRange();
    }

    public void printCave() {
        cave.print();
    }

    public String toString() {
        return cave.toString();
    }

    public static void main(String[] args) {
        ConwayProcessor cave = new ConwayProcessor(32, 32, 0.25, 0.8, 3, 2, DEFAULT_USE_PARALLEL);
        cave.printCave();
        cave.nextPhase();
        cave.printCave();
    }

}
