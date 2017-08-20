// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model.rules

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.Conway

/**
  * Swarms rule
  */
class RuleB356S23Swarm extends AbstractRule {
  override private[rules] def applyRuleToCandidate(c: Location, conway: Conway, newConway: Conway) = {
    val numNbrs = conway.getNumNeighbors(c)
    val isAlive = conway.isAlive(c)
    if (isAlive) if (numNbrs == 2 || numNbrs == 3) newConway.setValue(c, conway.getValue(c) + 1)
    else if (numNbrs == 3 || numNbrs == 5 || numNbrs == 6) newConway.setValue(c, 1)
  }
}
