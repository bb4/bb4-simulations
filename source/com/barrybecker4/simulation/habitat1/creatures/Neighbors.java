/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.creatures;

import com.barrybecker4.simulation.habitat1.model.Cell;
import com.barrybecker4.simulation.habitat1.model.HabitatGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures, but they are all represented by instance of this class.
 *
 * @author Barry Becker
 */
class Neighbors {

    /** only pursue prey that is this close to us */
    private static final double SMELL_NEIGHBOR_DISTANCE = 0.1;

    Creature nearestPrey;
    Creature nearestFriend;
    List<Creature> flockFriends;

    private Creature creature;
    private HabitatGrid grid;

    /**
     * Constructor
     */
    Neighbors(Creature creature, HabitatGrid grid) {
        flockFriends = new ArrayList<>();
        this.creature = creature;
        this.grid = grid;
        findNeighbors();
    }


    private void findNeighbors() {

        CreatureType type = creature.getType();

        nearestPrey = null;
        nearestFriend = null;
        double nearestPreyDistance = Double.MAX_VALUE;
        double nearestFriendDistance = Double.MAX_VALUE;

        List<Cell> cells = grid.getNeighborCells(grid.getCellForPosition(creature.getLocation()));

        for (Cell cell : cells) {
            for (Creature nearbyCreature : cell.getCreatures()) {

                double dist = nearbyCreature.getLocation().distance(creature.getLocation());
                if (dist < SMELL_NEIGHBOR_DISTANCE)  {
                    if (nearbyCreature.getType() == type)  {
                        flockFriends.add(nearbyCreature);
                        if (dist < nearestFriendDistance) {
                            nearestFriendDistance = dist;
                            nearestFriend = nearbyCreature;
                        }
                    }
                    else if (type.getPreys().contains(nearbyCreature.getType())) {
                        if (dist < nearestPreyDistance) {
                            nearestPreyDistance = dist;
                            nearestPrey = nearbyCreature;
                        }
                    }
                }
            }
        }
    }

}
