// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

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

  def opFactor: Parser[Seq[LSystemNode]] = (PLUS.symbol | MINUS.symbol) ~ factor ^^ {
    case op ~ f => Seq(LSystemNode(op, ops)) ++ f
  }
  def theExp: Parser[Seq[LSystemNode]] = exp ^^ { Seq(_) }
  def factor: Parser[Seq[LSystemNode]] = rep(theExp | opFactor) ^^ { _.flatten.toSeq }

  /** One 'F' terminal (regex so it does not absorb following letters). */
  private def fAtom: Parser[LSystemNode] = F.symbol.r ^^ { _ => LSystemNode(F.symbol, ops) }

  /** A parenthesized subexpression as a single grouped node. */
  private def parenGroup: Parser[LSystemNode] = "(" ~> factor <~ ")" ^^ { fact =>
    val tn = LSystemNode("", ops)
    tn.hasParens = true
    tn.children = fact
    tn
  }

  def exp: Parser[LSystemNode] = fAtom | parenGroup

  def parseToTree(expression: String): LSystemNode = {
    val parsed: ParseResult[Seq[LSystemNode]] = parseAll(factor, expression)

    if (!parsed.successful) throw new IllegalArgumentException(parsed.toString)
    val seq = parsed.get
    if (seq.length > 1) {
      val root = LSystemNode("", ops)
      root.children = seq
      root
    } else seq.head
  }
}
