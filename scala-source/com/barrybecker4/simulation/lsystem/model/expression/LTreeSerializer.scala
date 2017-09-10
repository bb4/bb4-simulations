// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import com.barrybecker4.common.expression.Tokens
import com.barrybecker4.common.expression.TreeNode
import scala.collection.JavaConverters._


/**
  * Turns a tree into a string via in order traversal. Implements visitor pattern.
  * @author Barry Becker
  */
class LTreeSerializer {

  private[expression] def serialize(node: TreeNode) = {
    var serialized = ""
    if (node != null) serialized = traverse(node)
    if (serialized.length > 0) serialized
    else "Invalid"
  }

  /** processing for inner nodes */
  private def traverse(node: TreeNode): String = {
    var text: String = ""
    if (node.children.size > 0) {
      text += (if (node.hasParens) Tokens.LEFT_PAREN.getSymbol
      else "")

      for (n <- node.children.asScala) {
        text += traverse(n)
      }
      text += (if (node.hasParens) Tokens.RIGHT_PAREN.getSymbol.toString else "")
    }
    else text += node.getData
    text
  }
}
