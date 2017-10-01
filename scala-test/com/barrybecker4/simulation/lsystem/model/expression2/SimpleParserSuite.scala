// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression2

import org.scalatest.FunSuite

class SimpleParserSuite extends FunSuite {

  val sp = new SimpleParser

  test("simple parser") {
    assertResult("[1.4] parsed: foo") { sp.parse(sp.word, "foo").toString }
    assertResult("[1.6] parsed: barry") { sp.parse(sp.word, "barry").toString }
  }

  test("simple parser (first word in string)") {
    assertResult("[1.4] parsed: foo") { sp.parse(sp.word, "foo barry").toString }
  }

  test("simple parser (negative - has capital at start)") {
    println(sp.parse(sp.word, "Barry").toString)
    println("---")
    assertResult("[1.1] failure: string matching regex `[a-z]+' expected but `B' found\n\nBarry\n^") {
      sp.parse(sp.word, "Barry").toString
    }
  }

  test("simple parser (has capital in middle)") {
    assertResult("[1.3] parsed: ba") {
      sp.parse(sp.word, "baRry").toString
    }
  }

  test("Sentence") {
    assertResult("[1.7] parsed: johnny") { sp.parse(sp.word, "johnny come lately").toString }
  }

}
