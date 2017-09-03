/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.creatures;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.simulation.habitat1.model.Cell;
import com.barrybecker4.simulation.habitat1.model.HabitatGrid;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.List;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures, but they are all represented by instance of this class.
 *
 * @author Barry Becker
 */
public class Creature  {

    /** When this close we are considered on top ot the prey */
    private static final double THRESHOLD_TO_PREY = 0.001;

    private CreatureType type;

    private Point2d location;
    private double direction;
    private double speed;
    private int numDaysPregnant;

    /** if becomes too large, then starve */
    private int hunger;
    private boolean alive;
    /** chasing prey */
    private boolean pursuing;
    /** not mature (capable of having children) until eaten at least once (and can eat). */
    private boolean mature;

    /**
     * Constructor
     */
    Creature(CreatureType type, Point2d location) {
        this.type = type;
        this.location = location;

        numDaysPregnant = 0; //(int) (MathUtil.RANDOM.nextDouble() * type.getGestationPeriod()/5);
        hunger = 0; //(int)(MathUtil.RANDOM.nextDouble() * type.getStarvationThreshold()/6);


        this.direction = randomDirection();
        this.speed = type.getNormalSpeed();
        alive = true;

        mature = (speed == 0);
    }

    private double randomDirection() {
        return 2.0 * Math.PI * MathUtil.RANDOM.nextDouble();
    }

    public Vector2d getVelocity() {
        return new Vector2d(Math.sin(direction) * speed, Math.cos(direction) * speed);
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isPursuing() {
        return pursuing;
    }

    public String getName() {
        return type.getName();
    }

    public CreatureType getType() {
        return type;
    }

    public Point2d getLocation() {
        return location;
    }

    public double getDirection() {
        return direction;
    }


    private Point2d computeNewPosition() {
        Vector2d vel = getVelocity();
        return new Point2d( absMod(location.x + vel.x),  absMod(location.y + vel.y));
    }

    private double absMod(double value) {
        double newValue = value % 1.0;
        return newValue<0 ? 1- newValue :  newValue;
    }

    public double getSize() {
        return type.getSize();
    }

    public String toString() {
        return getName() + " hunger="  + hunger + " pregnant=" + numDaysPregnant + " alive="+ alive;
    }

    /**
     * @return true if new child spawned
     */
    public boolean nextDay(HabitatGrid grid) {
        boolean spawn = false;
        numDaysPregnant++;
        hunger++;

        if (hunger >= type.getStarvationThreshold()) {
            alive = false;
        }

        if (numDaysPregnant >= type.getGestationPeriod()) {
            // if very hungary, abort the fetus
            if (hunger > type.getStarvationThreshold() / 2) {
                numDaysPregnant = 0;
            }
            else {
                // spawn new child.
                spawn = true;
                numDaysPregnant = 0;
            }
        }
        if (speed > 0) {
            Neighbors nbrs = new Neighbors(this, grid);

            if (nbrs.nearestPrey != null && nbrs.nearestPrey.isAlive())
                moveTowardPreyAndEatIfPossible(nbrs.nearestPrey);
            else {
                flock(nbrs.flockFriends, nbrs.nearestFriend);
            }
            moveToNewLocation(grid);
        }


        // else move toward friends and swarm
        return spawn;
    }

    private void moveTowardPreyAndEatIfPossible(Creature nearestPrey) {

        //System.out.println(this +" chasing "  + nearestPrey);
        pursuing = true;
        speed = type.getMaxSpeed();
        double distance = nearestPrey.getLocation().distance(location);

        if (distance < THRESHOLD_TO_PREY) {
            //System.out.println( this + " about to eat " + nearestPrey);
            eat(nearestPrey);
            direction = randomDirection();
        }
        else {
            direction = MathUtil.getDirectionTo(getLocation(), nearestPrey.getLocation());
            if (distance < type.getMaxSpeed()) {
                speed = distance;
            }
        }
    }

    /**
     * Flock with nbrs
     * Move toward the center of mass of neighbors and turn in same direction as nearest friend.
     * @param friends
     */
    private void flock(List<Creature> friends, Creature nearestFriend) {
        if (nearestFriend == null)
            return;
        Point2d centerOfMass = new Point2d(0, 0);

        for (Creature c : friends) {
            centerOfMass.add(c.getLocation());
        }
        centerOfMass.scale(1.0/friends.size());

        double directionToCOM = MathUtil.getDirectionTo(getLocation(), centerOfMass);

        direction = (nearestFriend.direction + directionToCOM) / 2.0;
    }

    private void moveToNewLocation(HabitatGrid grid) {
        location = computeNewPosition();

        Cell oldCell = grid.getCellForPosition(location);
        Cell newCell = grid.getCellForPosition(location) ;
        if (newCell != oldCell) {
            newCell.addCreature(this);
            oldCell.removeCreature(this);
        }
    }

    /**
     * @param creature  the creature we will now eat.
     */
    private void eat(Creature creature) {
        hunger -= creature.type.getNutritionalValue();
        creature.kill();
        hunger = Math.max(0, hunger);
        pursuing = false;
        speed = type.getNormalSpeed();
        mature = true;
    }
}
