// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import com.barrybecker4.common.expression.ExpressionParser
import com.barrybecker4.common.expression.Tokens
import com.barrybecker4.common.expression.TreeNode
import java.util


/**
  * Parses the text form of an L-system expression into a tree representation.
  * See https://en.wikipedia.org/wiki/L-system
  * @author Barry Becker
  */
class LExpressionParser() extends ExpressionParser(new LOperatorsDefinition) {
  /**
    * Recursive method to find all the tree nodes for the terms a the current level.
    * For example, given this expression
    * FF(F(-F)(++F))
    * the items in []'s represent the array of nodes returned.
    * [FF][F(-F)][++F]
    * The parts that were in ()'s become their own subtrees via recursive calls.
    *
    * @param exp the expression to get the nodes at the current level for
    * @return array of nodes representing terms that the current level.
    * @throws Error if there is a syntax error causing the expression to be invalid
    */
  override protected def getNodesAtLevel(exp: String): util.List[TreeNode] = {
    var pos = 0
    val nodes = new util.ArrayList[TreeNode]
    var token = ""
    var ch = exp.charAt(pos)
    while (pos < exp.length && !(token == "" + Tokens.RIGHT_PAREN.getSymbol)) {
      if (ch == ' ') {
        // spaces are ignored
      }
      else if (ch == Tokens.LEFT_PAREN.getSymbol) {
        if (token.length > 0) {
          nodes.add(new TreeNode(token, opDef))
          token = ""
        }
        val closingParenPos = findClosingParen(exp, pos + 1)
        // this method will make the recursive call
        processSubExpression(exp, pos + 1, token, closingParenPos, nodes)
        pos = closingParenPos
      }
      else if (LToken.isTerminal(ch)) token += ch
      else throw new IllegalStateException("Unexpected character " + ch + " in expression: " + exp)
      pos += 1
      if (pos < exp.length) ch = exp.charAt(pos)
    }
    // add the final node
    if (token.length > 0) pushNodesForToken(token, nodes)
    nodes
  }

  /**
    * Parse a parenthesized sub expression recursively.
    * @return current token. May have been reset to "".
    */
  override protected def processSubExpression(exp: String, pos: Int, token: String,
                                              closingParenPos: Int, nodes: util.List[TreeNode]): String = {
    // recursive call for sub expression
    val subTree = parse(exp.substring(pos, closingParenPos))
    subTree.hasParens = true
    nodes.add(subTree)
    token
  }

  /** Converts a list of nodes to a single node by reducing them to subtrees in order of operator precedence. */
  override protected def makeTreeFromNodes(nodes: util.List[TreeNode]): TreeNode = {
    val node = new TreeNode("", opDef)
    node.children.addAll(nodes)
    nodes.clear()
    nodes.add(node)
    nodes.get(0)
  }

  override protected def pushNodesForToken(token: String, nodes: util.List[TreeNode]): Unit = {
    if (token == null) return
    nodes.add(new TreeNode(token, opDef))
  }
}
