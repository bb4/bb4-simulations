// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import org.scalatest.funsuite.AnyFunSuite
import com.barrybecker4.common.testsupport.strip
import com.barrybecker4.expression.TreeNode

class LExpressionParserSuite extends AnyFunSuite {

  /** instance under test */
  val parser = new LExpressionParser
  val serializer = new LTreeSerializer

  test("FOnlyExp") { verifyParse("F") }
  test("TwoFFsExp") {verifyParse("FF")}
  test("ExpF-F") { verifyParse("F-F") }
  test("Exp-F-F") { verifyParse("-F-F") }
  test("Exp-F++F") { verifyParse("-F++F") }
  test("SimpleExpParen") { verifyParse("(F)") }
  test("SimpleExpWithParen") { verifyParse("F(-F)") }
  test("ExpWithSpaces") { verifyParse("F (- F)", "F(-F)") }
  test("TwoLevelNestedExp") {verifyParse("FF(F(-F))(++F)")}

  // some negative tests
  test("InvalidExp") {
    val caught = intercept[IllegalArgumentException] {verifyParse("XXX", "")}
    assert(strip(caught.getMessage) === "[1.1] failure: '-' expected but 'X' found\n\nXXX\n^")
  }
  test("Mismatched parens") {
    val caught = intercept[IllegalArgumentException] {verifyParse("F)-F", "") }
    assert(strip(caught.getMessage) === "[1.2] failure: '-' expected but ')' found\n\nF)-F\n ^")
  }

  /**  @param exp the expression to parse */
  private def verifyParse(exp: String): Unit = { verifyParse(exp, exp) }

  private def verifyParse(expression: String, expSerializedStr: String): Unit = {
    //val root = parser.parseAll(parser.factor, expression)
    val root = parser.parseToTree(expression)
    //println("expression = " + root)

    if (expSerializedStr.isEmpty) {
      assertResult(TreeNode()) { root }
    }
    else {
      println("serialized = "  + serializer.serialize(root))
      assertResult(expSerializedStr) { serializer.serialize(root) }
    }
  }
}
