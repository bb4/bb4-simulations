// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model.rules

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.Conway

/**
  * Totalistic life rule: a dead cell with neighbor count in `birth` is born with age 1;
  * a live cell with neighbor count in `survival` survives with age incremented by 1.
  */
class TotalisticLifeRule(birth: Set[Int], survival: Set[Int]) extends Rule {

  override private[rules] def applyRuleToCandidate(c: Location, conway: Conway, newConway: Conway): Unit = {
    val numNbrs = conway.getNumNeighbors(c)
    val alive = conway.isAlive(c)
    if alive then
      if survival.contains(numNbrs) then newConway.setValue(c, conway.getValue(c) + 1)
    else if birth.contains(numNbrs) then newConway.setValue(c, 1)
  }
}
