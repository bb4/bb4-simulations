// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression


object LToken {
  val F = LToken("F")
  val PLUS = LToken("+")
  val MINUS = LToken("-")

  def isTerminal(symbol: String): Boolean = symbol == F.symbol || symbol == PLUS.symbol || symbol == MINUS.symbol
}

case class LToken(symbol: String) {
  def isTerminal: Boolean = LToken.isTerminal(symbol)
}

