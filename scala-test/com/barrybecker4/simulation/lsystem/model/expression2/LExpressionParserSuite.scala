// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression2

import com.barrybecker4.common.expression.TreeNode
import org.scalatest.FunSuite

class LExpressionParserSuite extends FunSuite {

  /** instance under test */
  val parser = new LExpressionParser

  test("FOnlyExp") { verifyParse("F", "[1.2] parsed: List(F)") }
  test("TwoFFsExp") { verifyParse("FF", "[1.3] parsed: List(F, F)") }
  test("ExpF-F") { verifyParse("F-F", "[1.4] parsed: List(F, (-~List(F)))") }
  test("Exp-F-F") { verifyParse("-F-F", "[1.5] parsed: List((-~List(F, (-~List(F)))))") }
  test("Exp-F++F") { verifyParse("-F++F", "[1.6] parsed: List((-~List(F, (+~List((+~List(F)))))))") }
  test("SimpleExpParen") { verifyParse("(F)", "[1.4] parsed: List((((~List(F))~)))") }
  test("SimpleExpWithParen") { verifyParse("F(-F)", "[1.6] parsed: List(F, (((~List((-~List(F))))~)))") }
  test("ExpWithSpaces") { verifyParse("F (- F)", "[1.8] parsed: List(F, (((~List((-~List(F))))~)))") }
  test("TwoLevelNestedExp") {
    verifyParse("FF(F(-F))(++F)",
      "[1.15] parsed: List(F, F, (((~List(F, (((~List((-~List(F))))~))))~)), (((~List((+~List((+~List(F))))))~)))")
  }

  test("InvalidExp") { verifyParse("XXX", "[1.1] failure: `-' expected but `X' found\n\nXXX\n^") }

  /**  @param exp the expression to parse */
  private def verifyParse(exp: String) { verifyParse(exp, exp) }

  private def verifyParse(expression: String, expSerializedStr: String) {
    val root = parser.parseAll(parser.factor, expression)
    println(expression + " = " + root)
    // val serialized = serializer.serialize(root)
    assertResult(expSerializedStr) { root.toString }
  }
}
