// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression2

import scala.util.parsing.combinator._


class LExpressionParser extends RegexParsers {
  def factor: Parser[Any] = rep(exp | "+"~factor | "-"~factor) //^^ { _.toString }
  def exp: Parser[Any] = "F".r | "("~factor~")" //^^ { _.toString }
}