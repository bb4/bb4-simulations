package com.barrybecker4.simulation.conway.model.rules;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.simulation.conway.model.Conway;

import java.util.Set;

/**
 * Grows like an amoeba
 */
public class RuleB34S456Amoeba extends AbstractRule {

    void applyRuleToCandidate(Location c, Conway conway, Conway newConway) {
        int numNbrs = conway.getNumNeighbors(c);
        boolean isAlive = conway.isAlive(c);
        if (isAlive) {
            if ((numNbrs == 4 || numNbrs == 5 || numNbrs == 6)) {
                newConway.setValue(c, conway.getValue(c) + 1);
            }
        }
        else if (numNbrs == 3 || numNbrs == 4) {
            newConway.setValue(c, 1);
        }
    }
}
