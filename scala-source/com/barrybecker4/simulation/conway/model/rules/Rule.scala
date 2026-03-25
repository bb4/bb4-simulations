// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model.rules

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.Conway
import scala.collection.parallel.CollectionConverters._

/**
  * All Conway Game of Life rule implementations must extend this trait.
  * @author Barry Becker
  */
trait Rule {

  /**
    * For each candidate cell (live cells and neighbors of live cells), apply the rule to `newConway`.
    * Synchronized so two threads never apply rules concurrently on shared rule state (parallelism is
    * confined to processing the candidate set in `applyRule`).
    *
    * @return the new generation grid.
    */
  def applyRule(conway: Conway, newConway: Conway, parallel: Boolean): Conway = synchronized {
    val candidates = conway.getCandidates
    if parallel then candidates.par.foreach(c => applyRuleToCandidate(c, conway, newConway))
    else candidates.foreach(c => applyRuleToCandidate(c, conway, newConway))
    newConway
  }

  private[rules] def applyRuleToCandidate(c: Location, conway: Conway, newConway: Conway): Unit
}
