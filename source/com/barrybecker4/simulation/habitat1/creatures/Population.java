/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.creatures;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.simulation.habitat1.model.HabitatGrid;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Everything we need to know about a population of a certain kind of creature.
 *
 * @author Barry Becker
 */
public class Population {

    private static final double SPAWN_RADIUS = 0.05;

    private CreatureType type;
    private int initialSize;
    private List<Creature> creatures;


    public Population(CreatureType type) {
        this.type = type;
        creatures = new ArrayList<>();
    }

    /**
     * Factory method to create an initial population of randomly distributed members.
     * @param type
     * @param initialSize
     * @return new population
     */
    static Population createPopulation(CreatureType type, int initialSize)  {

        Population pop = new Population(type);
        pop.createInitialSet(initialSize);
        return pop;
    }

    private void createInitialSet(int num) {
        this.initialSize = num;
        create();
    }

    /** Reset the population to its original size */
    public void reset() {
        create();
    }

    private void create() {
        creatures.clear();
        for (int i=0; i<initialSize; i++) {
            creatures.add(new Creature(type, new Point2d(MathUtil.RANDOM.nextDouble(), MathUtil.RANDOM.nextDouble())));
        }
    }

    /**
     * get a defensive copy.
     */
    public List<Creature> getCreatures() {
        return new ArrayList<Creature>(creatures);
    }

    /**
     * Increment to the next day.
     * Move the creatures around and see if they are close to something to eat.
     * Have children of gestation period is complete and they have spawned.
     * @param grid the habitat grid
     */
    public void nextDay(HabitatGrid grid) {

        List<Point2d> spawnLocations = new ArrayList<Point2d>();

        // Figure out if anything edible nearby.
        // Eat prey if there are things that we eat nearby.

        for (Creature creature : creatures) {
            boolean spawn = creature.nextDay(grid);

            if (spawn) {
                Point2d loc = creature.getLocation();
                spawnLocations.add(new Point2d(absMod(loc.x + SPAWN_RADIUS * MathUtil.RANDOM.nextDouble()),
                        absMod(loc.y + SPAWN_RADIUS * MathUtil.RANDOM.nextDouble())));
            }
        }

        for (Point2d newLocation : spawnLocations) {
            Creature newCrtr = new Creature(type, newLocation);
            creatures.add(newCrtr);
            grid.getCellForPosition(newLocation).addCreature(newCrtr);
        }
    }


    /**
     * Remove dead after next day is done.
     */
    void removeDead(HabitatGrid grid) {
        Iterator<Creature> creatureIt = creatures.iterator();
        while (creatureIt.hasNext())   {
             Creature creature = creatureIt.next();
             if (!creature.isAlive()) {
                 creatureIt.remove();
                 grid.getCellForPosition(creature.getLocation()).removeCreature(creature);
             }
        }
        // verify none dead still around
        for (Creature c : creatures) {
            assert c.isAlive();
        }
    }

    public CreatureType getType() {
        return type;
    }

    public int getSize() {
        return creatures.size();
    }

    public String getName() {
        return "Population of " + type.getName();
    }

    private double absMod(double value) {
        return Math.abs(value % 1.0);
    }
}
