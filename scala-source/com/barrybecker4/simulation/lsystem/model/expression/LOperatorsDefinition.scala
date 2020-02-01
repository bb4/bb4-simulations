// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import com.barrybecker4.expression.Operator
import com.barrybecker4.expression.OperatorsDefinition
import com.barrybecker4.expression.TreeNode
import scala.collection.mutable.ListBuffer


/**
  * The expected binary operators in the text expression.
  * @author Barry Becker
  */
object LOperatorsDefinition {

  /** Defines the order of precedence for the operators. Those at the same level are evaluated from left to right. */
  private val OPERATOR_PRECEDENCE = Array.ofDim[Operator](0, 0)
}

class LOperatorsDefinition extends OperatorsDefinition {

  override def getOperatorPrecedence: Array[Array[Operator]] = LOperatorsDefinition.OPERATOR_PRECEDENCE

  /** @return true if the specified character is an operator */
  override def isOperator(ch: Char) = false

  /** @return true if the last node is an operator or there were no previous nodes  */
  override def isLastNodeOperator(nodes: ListBuffer[TreeNode]): Boolean =
    nodes.isEmpty || nodes.last.isOperator
}