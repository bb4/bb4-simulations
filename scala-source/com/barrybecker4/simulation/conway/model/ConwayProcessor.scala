// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.rules.{Rule, TotalisticLifeRule}
import ConwayProcessor.RuleType
import ConwayProcessor.RuleType.{AmoebaB34S456, HighlifeB36S23, SwarmB356S23, TraditionalB3S23}

/**
  * @author Barry Becker
  */
object ConwayProcessor {

  val DEFAULT_USE_PARALLEL = false

  enum RuleType:
    case TraditionalB3S23, AmoebaB34S456, HighlifeB36S23, SwarmB356S23

  val DEFAULT_RULE_TYPE: RuleType = RuleType.TraditionalB3S23
}

class ConwayProcessor private[model](var useParallel: Boolean = ConwayProcessor.DEFAULT_USE_PARALLEL) {

  private var conway = new Conway
  private var wrapGrid = false
  private var width = -1
  private var height = -1
  private var rule: Rule = TotalisticLifeRule(Set(3), Set(2, 3))
  conway.initialize()

  private[model] def setWrap(wrapGrid: Boolean, width: Int, height: Int): Unit = {
    this.wrapGrid = wrapGrid
    this.width = width
    this.height = height
  }

  private[model] def setRuleType(ruleType: RuleType): Unit = {
    rule = ruleType match {
      case TraditionalB3S23 => TotalisticLifeRule(Set(3), Set(2, 3))
      case AmoebaB34S456 => TotalisticLifeRule(Set(3, 4), Set(4, 5, 6))
      case HighlifeB36S23 => TotalisticLifeRule(Set(3, 6), Set(2, 3))
      case SwarmB356S23 => TotalisticLifeRule(Set(3, 5, 6), Set(2, 3))
    }
  }

  def getPoints: Set[Location] = conway.getPoints

  private[model] def setAlive(row: Int, col: Int): Unit =
    conway.setValue(IntLocation(row, col), 1)

  private[model] def setDead(row: Int, col: Int): Unit =
    conway.setValue(IntLocation(row, col), 0)

  private[model] def clearGridForTesting(): Unit =
    conway.clearForTesting()

  /** Compute the next step of the simulation. */
  private[model] def nextPhase(): Unit = {
    val newConway = new Conway
    newConway.setWrapping(wrapGrid, width, height)
    conway = rule.applyRule(conway, newConway, useParallel)
  }

  def getValue(c: Location): Int = conway.getValue(c)

  def isAlive(c: Location): Boolean = conway.isAlive(c)

  override def toString: String = conway.toString
}
