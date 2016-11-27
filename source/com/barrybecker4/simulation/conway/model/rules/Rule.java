package com.barrybecker4.simulation.conway.model.rules;

import com.barrybecker4.simulation.conway.model.Conway;

/**
 * All conway game of life rules systems must implement this.
 */
public interface Rule {

    Conway applyRule(Conway oldConway, Conway newConway);

}
