// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import com.barrybecker4.common.expression.TreeNode
import scala.util.parsing.combinator._
import LToken._

/**
  * Parses the text form of an L-system expression into a tree representation.
  * See https://en.wikipedia.org/wiki/L-system
  * I originally implemented a custom parser, but this version leverages scala's parser combinator library.
  * @author Barry Becker
  */
class LExpressionParser extends RegexParsers {

  val ops = new LOperatorsDefinition

  def opFactor: Parser[Seq[TreeNode]] = (PLUS.symbol | MINUS.symbol) ~ factor ^^ {
    case op ~ f => Seq(new TreeNode(op, ops)) ++ f
  }
  def theExp: Parser[Seq[TreeNode]] = exp ^^ { Seq(_) }
  def factor: Parser[Seq[TreeNode]] = rep(theExp | opFactor) ^^ { _.flatten.toSeq }

  def exp: Parser[TreeNode] = (F.symbol.r | "("~>factor<~")") ^^ {
    case f: String => new TreeNode(F.symbol, ops)
    case fact: Seq[_] =>
      val tn = new TreeNode("", ops)
      tn.hasParens = true
      tn.children = fact.asInstanceOf[Seq[TreeNode]]
      tn
    case x => throw new UnsupportedOperationException("Unexpected: " + x.getClass.getName )
  }

  def parseToTree(expression: String): TreeNode = {
    val parsed: ParseResult[Seq[TreeNode]] = parseAll(factor, expression)

    if (parsed.isEmpty) throw new IllegalArgumentException(parsed.toString)
    if (parsed.get.length > 1) {
      val root = new TreeNode("", ops)
      root.children = Seq(parsed.get:_*)
      root
    } else parsed.get.head
  }
}
