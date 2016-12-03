package com.barrybecker4.simulation.conway.model.rules;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.simulation.conway.model.Conway;

import java.util.Set;

/**
 * The original Conway Life rule
 */
public class RuleB3S23 extends AbstractRule {

    void applyRuleToCandidate(Location c, Conway conway, Conway newConway) {
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
}
