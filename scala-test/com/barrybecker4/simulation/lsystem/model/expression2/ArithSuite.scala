// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression2

import org.scalatest.FunSuite

class ArithSuite extends FunSuite {

  test("arith") {

    val aparser = new Arith()
    val result: aparser.ParseResult[Any] = aparser.parseAll(aparser.expr, "2 + 3")
    println(result)
    assertResult("[1.6] parsed: ((2~List())~List((+~(3~List()))))") { result.toString}
  }
}
