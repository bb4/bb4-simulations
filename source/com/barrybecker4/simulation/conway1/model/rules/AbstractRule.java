package com.barrybecker4.simulation.conway1.model.rules;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.simulation.conway1.model.Conway;

import java.util.Set;

/**
 * The original Conway Life rule
 */
public abstract class AbstractRule implements Rule {

    @Override
    public Conway applyRule(Conway conway, Conway newConway) {
        Set<Location> candidates = conway.getCandidates();

        // Loop through all the candidates, apply the life-rule, and update the new grid appropriately.
        for (Location c : candidates) {
            applyRuleToCandidate(c, conway, newConway);
        }
        return newConway;
    }

    abstract void applyRuleToCandidate(Location c, Conway conway, Conway newConway);
}
