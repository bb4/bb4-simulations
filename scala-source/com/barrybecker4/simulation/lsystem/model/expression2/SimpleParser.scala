// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression2

import scala.util.parsing.combinator._

/**
  * Parses any lower case word
  */
class SimpleParser extends RegexParsers {

  def word: Parser[String] = """[a-z]+""".r ^^ { _.toString }

}