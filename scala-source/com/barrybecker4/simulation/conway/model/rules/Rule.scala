// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model.rules

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.Conway


/**
  * All conway game of life rules systems must implement this.
  * @author Barry Becker
  */
trait Rule {

  /**
    * For each live point in the old conway, determine if there is a new point.
    * first create a big set of all the points that must be examined (this includes empty nbrs of live points)
    *
    * @return the new conway set with points either added or removed according to some set of rules.
    */
  def applyRule(conway: Conway, newConway: Conway): Conway = {
    val candidates = conway.getCandidates
    // Loop through all the candidates, apply the life-rule, and update the new grid appropriately.
    for (c <- candidates) {
      applyRuleToCandidate(c, conway, newConway)
    }
    newConway
  }

  private[rules] def applyRuleToCandidate(c: Location, conway: Conway, newConway: Conway): Unit
}
