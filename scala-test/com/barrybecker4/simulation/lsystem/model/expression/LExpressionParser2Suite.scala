/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.lsystem.model.expression

import org.scalatest.FunSuite

class LExpressionParser2Suite extends FunSuite {

  /** instance under test */
  val parser = new LExpressionParser2
  val serializer = new LTreeSerializer

  test("FOnlyExp") { verifyParse("F") }
  test("TwoFFsExp") {verifyParse("FF")}
  test("ExpF-F") { verifyParse("F-F") }
  test("Exp-F-F") { verifyParse("-F-F") }
  test("Exp-F++F") { verifyParse("-F++F") }
  test("SimpleExpParen") { verifyParse("(F)") }
  test("SimpleExpWithParen") { verifyParse("F(-F)") }
  test("ExpWithSpaces") { verifyParse("F (- F)", Some("F(-F)")) }
  test("TwoLevelNestedExp") {
    verifyParse("FF(F(-F))(++F)")
  }

  // some negative tests
  test("InvalidExp") { verifyParse("XXX", None) }
  test("Mismatched parens") { verifyParse("F)-F", None) }

  /**  @param exp the expression to parse */
  private def verifyParse(exp: String) { verifyParse(exp, Some(exp)) }

  private def verifyParse(expression: String, expSerializedStr: Option[String]) {
    //val root = parser.parseAll(parser.factor, expression)
    val root = parser.parseToTree(expression)
    println("expression = " + root)

    if (expSerializedStr.isEmpty)
      assertResult(None) {root}
    // val serialized = serializer.serialize(root)
    else {
      println("serialized = "  + serializer.serialize(root.get))
      assertResult(expSerializedStr.get) { serializer.serialize(root.get) }
    }
  }
}
