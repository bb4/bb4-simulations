package com.barrybecker4.simulation.conway1.model.rules;

import com.barrybecker4.simulation.conway1.model.Conway;

/**
 * All conway game of life rules systems must implement this.
 */
public interface Rule {

    /**
     * For each live point in the old conway, determine if there is a new point.
     * first create a big set of all the points that must be examined (this includes empty nbrs of live points)
     * @return the new conway set with points either added or removed according to some set of rules.
     */
    Conway applyRule(Conway oldConway, Conway newConway);

}
