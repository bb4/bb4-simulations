// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import org.scalatest.FunSuite


/**
  * @author Barry Becker
  */
class LExpressionParserTest extends FunSuite {

  /** instance under test */
  private var parser = new LExpressionParser
  /** used to verify parsed tree */
  private var serializer = new LTreeSerializer

  test("FOnlyExp") { verifyParse("F") }
  test("SimpleExp") { verifyParse("F(-F)") }
  test("ExpWithSpaces") { verifyParse("F (- F)", "F(-F)") }
  test("TwoLevelNestedExp") { verifyParse("FF(F(-F))(++F)") }

  /**  @param exp the expression to parse */
  private def verifyParse(exp: String) { verifyParse(exp, exp) }

  private def verifyParse(exp: String, expSerializedStr: String) {
    val root = parser.parse(exp)
    val serialized = serializer.serialize(root)
    assertResult(expSerializedStr) { serialized }
  }
}
