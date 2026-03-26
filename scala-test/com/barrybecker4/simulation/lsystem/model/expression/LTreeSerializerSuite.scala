// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression

import org.scalatest.funsuite.AnyFunSuite

class LTreeSerializerSuite extends AnyFunSuite {

  private val ops = new LOperatorsDefinition
  private val serializer = new LTreeSerializer

  test("serialize leaf F") {
    val leaf = LSystemNode("F", ops)
    assert(serializer.serialize(leaf) === "F")
  }

  test("serialize leaf operator symbol") {
    assert(serializer.serialize(LSystemNode("+", ops)) === "+")
  }

  test("serialize parenthesized single child") {
    val inner = LSystemNode("F", ops)
    val group = LSystemNode("", ops)
    group.hasParens = true
    group.children = Seq(inner)
    assert(serializer.serialize(group) === "(F)")
  }

  test("serialize nested parens") {
    val f = LSystemNode("F", ops)
    val inner = LSystemNode("", ops)
    inner.hasParens = true
    inner.children = Seq(f)
    val outer = LSystemNode("", ops)
    outer.hasParens = true
    outer.children = Seq(inner)
    assert(serializer.serialize(outer) === "((F))")
  }

  test("serialize empty leaf yields Invalid") {
    val empty = LSystemNode("", ops)
    assert(serializer.serialize(empty) === "Invalid")
  }

  test("round-trip KOCH_SNOWFLAKE via parser") {
    val parser = new LExpressionParser
    val expr = "F+F--F+F"
    val root = parser.parseToTree(expr)
    val again = parser.parseToTree(serializer.serialize(root))
    assert(serializer.serialize(again) === serializer.serialize(root))
  }
}
