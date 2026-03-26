// Copyright by Barry G. Becker, 2016-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

/**
  * Turns a tree into a string via in order traversal. Implements visitor pattern.
  * @author Barry Becker
  */
class LTreeSerializer {

  def serialize(node: LSystemNode): String = {
    val serialized = traverse(node)
    if (serialized.nonEmpty) serialized else "Invalid"
  }

  private def traverse(node: LSystemNode): String = {
    val sb = new StringBuilder
    traverseInto(sb, node)
    sb.toString
  }

  private def traverseInto(sb: StringBuilder, node: LSystemNode): Unit = {
    if (node.children.nonEmpty) {
      if (node.hasParens) sb.append('(')
      for (n <- node.children) traverseInto(sb, n)
      if (node.hasParens) sb.append(')')
    } else sb.append(node.getData)
  }
}
