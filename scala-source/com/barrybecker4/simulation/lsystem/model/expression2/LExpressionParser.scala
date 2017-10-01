// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression2


import scala.util.parsing.combinator._

class LExpressionParser extends RegexParsers {
  def factor: Parser[Any] = exp~rep("+"~exp | "-"~exp)
  def exp: Parser[Any] = f | factor | "("~factor~")"
  def f: Parser[Any] = "F"
}