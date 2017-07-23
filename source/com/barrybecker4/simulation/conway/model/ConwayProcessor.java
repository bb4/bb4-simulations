package com.barrybecker4.simulation.conway.model;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.simulation.conway.model.rules.*;

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
    private Rule rule = new RuleB3S23(); // new RuleB34S456Amoeba();

    public enum RuleType {TraditionalB3S23, AmoebaB34S456, HighlifeB36S23, SwarmB356S23}
    public static final RuleType DEFAULT_RULE_TYPE = RuleType.TraditionalB3S23;

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

    void setRuleType(RuleType type) {
        switch (type) {
            case TraditionalB3S23: rule = new RuleB3S23(); break;
            case AmoebaB34S456: rule = new RuleB34S456Amoeba(); break;
            case HighlifeB36S23: rule = new RuleB36S23Highlife(); break;
            case SwarmB356S23: rule = new RuleB356S23Swarm(); break;
        }
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

        conway = rule.applyRule(conway, newConway);

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
    }

    public Integer getValue (Location c) {
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
