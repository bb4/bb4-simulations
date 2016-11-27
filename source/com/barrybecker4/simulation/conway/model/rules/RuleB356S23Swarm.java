package com.barrybecker4.simulation.conway.model.rules;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.simulation.conway.model.Conway;

import java.util.Set;

/**
 * The original Conway Life rule
 */
public class RuleB356S23Swarm implements Rule {

    @Override
    public Conway applyRule(Conway conway, Conway newConway) {
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
            else if (numNbrs == 3 || numNbrs == 5 || numNbrs == 6) {
                newConway.setValue(c, 1);
            }
        }
        return newConway;
    }
}
