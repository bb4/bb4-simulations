// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import com.barrybecker4.expression.OperatorsDefinition

/** Parse tree for L-system expressions. Replaces bb4-expression TreeNode (removed in bb4-expression 2.0). */
class LSystemNode(val opDef: OperatorsDefinition, private val symbolData: String) {

  var children: Seq[LSystemNode] = Seq.empty
  var hasParens: Boolean = false

  def getData: String = symbolData
}

object LSystemNode {

  def apply(symbol: String, opDef: OperatorsDefinition): LSystemNode =
    new LSystemNode(opDef, symbol)
}
