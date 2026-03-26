// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import org.scalatest.funsuite.AnyFunSuite

class ParameterDistributionTypeSuite extends AnyFunSuite {

  test("every ParameterDistributionType has non-empty unique name and non-null param") {
    val names = scala.collection.mutable.Set[String]()
    for (pdt <- ParameterDistributionType.values) {
      assert(pdt.name.nonEmpty, s"empty name for $pdt")
      assert(pdt.param != null, s"null param for $pdt")
      assert(!names.contains(pdt.name), s"duplicate name: ${pdt.name}")
      names += pdt.name
    }
  }

  test("ParameterDistributionType ordinal round-trips with combo-aligned names") {
    val vals = ParameterDistributionType.values
    for (i <- vals.indices) {
      val pdt = ParameterDistributionType.fromOrdinal(i)
      assert(pdt.ordinal === i)
      assert(vals(i) === pdt)
      assert(vals(i).name === pdt.name)
    }
  }
}
