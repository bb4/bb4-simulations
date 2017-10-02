// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import com.barrybecker4.common.expression.TreeNode

import scala.util.parsing.combinator._
//import java.util.List
import scala.collection.JavaConverters._


class LExpressionParser2 extends RegexParsers {

  val ops = new LOperatorsDefinition


  def plusFactor: Parser[Seq[TreeNode]] = "+" ~ factor ^^ { case "+" ~ f => Seq(new TreeNode("+", ops)) ++ f}
  def minusFactor: Parser[Seq[TreeNode]] = "-" ~ factor ^^ { case "-" ~ f => Seq(new TreeNode("-", ops)) ++ f}
  def theExp: Parser[Seq[TreeNode]] = exp ^^ { e => Seq(e) }

  def factor: Parser[Seq[TreeNode]] = rep(theExp | plusFactor | minusFactor) ^^ { _.flatten.toSeq }


  def exp: Parser[TreeNode] = ("F".r | "("~>factor<~")") ^^ {
    case f: String => new TreeNode("F", ops)
    case fact: Seq[TreeNode] => {
      val tn = new TreeNode("", ops)
      tn.hasParens = true
      tn.children = fact.asJava
      tn
    }
    case x => throw new UnsupportedOperationException("Unexpected: " + x.getClass.getName )
  }

  def parseToTree(expression: String): Option[TreeNode] = {
    val parsed: ParseResult[Seq[TreeNode]] = parseAll(factor, expression)
    println("parsed = " + parsed)

    if (parsed.isEmpty) return None
    if (parsed.get.length > 1) {
      val root = new TreeNode("", ops)
      //root.hasParens = true
      root.children = parsed.get.asJava
      Some(root)
    } else {
      Some(parsed.get.head)
    }
  }
}