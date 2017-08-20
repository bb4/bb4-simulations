// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model.rules

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.Conway

/**
  * The original Conway Life rule
  * @author Barry Becker
  */
abstract class AbstractRule extends Rule {
  override def applyRule(conway: Conway, newConway: Conway): Conway = {
    val candidates = conway.getCandidates
    // Loop through all the candidates, apply the life-rule, and update the new grid appropriately.
    for (c <- candidates) {
      applyRuleToCandidate(c, conway, newConway)
    }
    newConway
  }

  private[rules] def applyRuleToCandidate(c: Location, conway: Conway, newConway: Conway): Unit
}
