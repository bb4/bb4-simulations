package com.barrybecker4.simulation.conway.model;

import com.barrybecker4.common.concurrency.RunnableParallelizer;
import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;

import java.util.Set;

/**
 * @author Barry Becker
 */
public class ConwayProcessor {

    /** cells die if less than this */
    public static final boolean DEFAULT_USE_PARALLEL = true;

    private Conway conway;
    /** Manages the worker threads. */
    private RunnableParallelizer parallelizer;


    /** Constructor that allows you to specify the dimensions of the conway */
    public ConwayProcessor() {
        this(DEFAULT_USE_PARALLEL);
    }

    public ConwayProcessor(boolean useParallel) {
        conway = new Conway();
        conway.initialize();
        setUseParallel(useParallel);
    }

    public void setUseParallel(boolean parallelized) {
        parallelizer =
             parallelized ? new RunnableParallelizer() : new RunnableParallelizer(1);
    }

    public Set<Location> getPoints() {
        return conway.getPoints();
    }

    public void setAlive(int i, int j) {
        conway.setValue(new IntLocation(i, j), 1);
    }

    /**
     * Compute the next step of the simulation.
     */
    public void nextPhase() {
        Conway newConway = new Conway(); //conway.createCopy();

        // for each live point in the old conway, determine if there is a new point.
        // first create a big set of all the points that must be examined (this includes empty nbrs of live points)
        Set<Location> candidates = conway.getCandidates();

        // Loop through all the candidates, apply the life-rule, and update the new grid appropriately.
        for (Location c : candidates) {
            int numNbrs = conway.getNumNeighbors(c);
            boolean isAlive = conway.isAlive(c);
            //System.out.println("isAlive = " + isAlive + " numNbr = " + numNbrs);
            if (!isAlive && numNbrs == 3) {
                newConway.setValue(c, 1);
            } else if (isAlive && (numNbrs == 2 || numNbrs == 3)) {
                newConway.setValue(c, conway.getValue(c) + 1);
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
        //System.out.println("new numpoints = " + newConway.getNumPoints() + " old numpoints = " + conway.getNumPoints());
        conway = newConway;
    }

    /**
     * Compute the next step of the simulation
     *
    public void nextPhase(int minPoint, int maxPoint, Conway newCave) {
        // Loop over each point of the map
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


    public int getValue (Location c) {
        return conway.getValue(c);
    }

    public String toString() {
        return conway.toString();
    }

    public static void main(String[] args) {
        ConwayProcessor cave = new ConwayProcessor(DEFAULT_USE_PARALLEL);
        cave.nextPhase();
    }

}
