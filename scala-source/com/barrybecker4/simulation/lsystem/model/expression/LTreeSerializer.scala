// Copyright by Barry G. Becker, 2016-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

/**
  * Turns a tree into a string via in order traversal. Implements visitor pattern.
  * @author Barry Becker
  */
class LTreeSerializer {

  def serialize(node: LSystemNode): String = {
    var serialized = ""
    if (node != null) serialized = traverse(node)
    if (serialized.length > 0) serialized
    else "Invalid"
  }

  /** processing for inner nodes */
  private def traverse(node: LSystemNode): String = {
    var text: String = ""
    if (node.children.nonEmpty) {
      text += (if (node.hasParens) "(" else "")
      for (n <- node.children)
        text += traverse(n)
      text + (if (node.hasParens) ")" else "")
    }
    else text + node.getData
  }
}
