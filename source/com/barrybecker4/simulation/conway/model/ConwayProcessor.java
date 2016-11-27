package com.barrybecker4.simulation.conway.model;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;

import java.util.Set;

/**
 * @author Barry Becker
 */
public class ConwayProcessor {

    /** cells die if less than this */
    public static final boolean DEFAULT_USE_PARALLEL = true;
    private boolean wrapGrid = false;
    private int width = -1;
    private int height = -1;

    private Conway conway;
    /** Manages the worker threads. */
    //private RunnableParallelizer parallelizer;

    /** Constructor that allows you to specify the dimensions of the conway */
    public ConwayProcessor() {
        this(DEFAULT_USE_PARALLEL);
    }

    ConwayProcessor(boolean useParallel) {
        conway = new Conway();
        conway.initialize();
        setUseParallel(useParallel);
    }

    void setWrap(boolean wrapGrid, int width, int height) {
        this.wrapGrid = wrapGrid;
        this.width = width;
        this.height = height;
    }

    void setUseParallel(boolean parallelized) {
        //parallelizer =
        //     parallelized ? new RunnableParallelizer() : new RunnableParallelizer(1);
    }

    public Set<Location> getPoints() {
        return conway.getPoints();
    }

    void setAlive(int row, int col) {
        conway.setValue(new IntLocation(row, col), 1);
    }

    /**
     * Compute the next step of the simulation.
     */
    void nextPhase() {
        Conway newConway = new Conway();
        newConway.setWrapping(wrapGrid, width, height);

        // for each live point in the old conway, determine if there is a new point.
        // first create a big set of all the points that must be examined (this includes empty nbrs of live points)
        Set<Location> candidates = conway.getCandidates();

        // Loop through all the candidates, apply the life-rule, and update the new grid appropriately.
        for (Location c : candidates) {
            int numNbrs = conway.getNumNeighbors(c);
            boolean isAlive = conway.isAlive(c);
            if (isAlive) {
                if ((numNbrs == 2 || numNbrs == 3)) {
                    newConway.setValue(c, conway.getValue(c) + 1);
                }
            }
            else if (numNbrs == 3) {
                newConway.setValue(c, 1);
            }
        }

        /*
        int numThreads = parallelizer.getNumThreads();
        List<Runnable> workers = new ArrayList<>(numThreads + 1);
        int range = conway.getNumPoints() / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int offset = i * range;
            workers.add(new Worker(offset, offset + range, newConway));
        }

        // blocks until all Callables are done running.
        parallelizer.invokeAllRunnables(workers);
        */
        conway = newConway;
    }

    public Integer getValue (Location c) {
        return conway.getValue(c);
    }

    public String toString() {
        return conway.toString();
    }

    /**
     * Compute the next step of the simulation
     *
    public void nextPhase(int minPoint, int maxPoint, Conway newCave) {
        for (int x = minPoint; x < maxPoint; x++) {
            double oldValue = conway.getValue(x, y);
            double newValue = oldValue + neibNum;
            newCave.setValue(x, y, newValue);
        }
        conway = newCave;
    }*/

    /**
     * Runs one of the chunks.
     *
    private class Worker implements Runnable {
        private int minX_, maxX_;
        private Conway newCave_;

        public Worker(int minPoint, int maxPoint, Conway newCave) {
            minX_ = minPoint;
            maxX_ = maxPoint;
            newCave_ = newCave;
        }

        @Override
        public void run() {
            nextPhase(minX_, maxX_, newCave_);
        }
    }*/


    public static void main(String[] args) {
        ConwayProcessor cave = new ConwayProcessor(DEFAULT_USE_PARALLEL);
        cave.nextPhase();
    }
}
