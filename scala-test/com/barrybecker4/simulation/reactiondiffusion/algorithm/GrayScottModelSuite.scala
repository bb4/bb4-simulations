/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.FunSuite

/**
  * @author Barry
  */
class GrayScottModelSuite extends FunSuite {


  test("periodic mid") {
    assertResult(10 ) { GrayScottModel.getPeriodicXValue(10, 100) }
  }

  test("periodic low") {
    assertResult(93 ) { GrayScottModel.getPeriodicXValue(-7, 100) }
  }

  test("periodic high") {
     assertResult(7 ) { GrayScottModel.getPeriodicXValue(107, 100) }
  }
}
