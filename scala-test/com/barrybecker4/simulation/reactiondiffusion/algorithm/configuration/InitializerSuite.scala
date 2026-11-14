/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import org.scalatest.funsuite.AnyFunSuite

class InitializerSuite extends AnyFunSuite {

  test("all initializers stamp only in-bounds and leave a non-seed background") {
    Initializer.values.foreach { init =>
      val model = new GrayScottModel(40, 40)
      model.setInitializer(init)

      var minU = Double.MaxValue
      var maxU = Double.MinValue
      var minV = Double.MaxValue
      var maxV = Double.MinValue
      for (x <- 0 until model.getWidth; y <- 0 until model.getHeight) {
        val u = model.u(x, y)
        val v = model.v(x, y)
        assert(!u.isNaN && !v.isNaN)
        minU = math.min(minU, u)
        maxU = math.max(maxU, u)
        minV = math.min(minV, v)
        maxV = math.max(maxV, v)
      }
      // Background is u=1, v=0; seeds use lower u / higher v
      assert(maxU >= 0.99, s"$init expected background u near 1, maxU=$maxU")
      assert(minV <= 0.01, s"$init expected background v near 0, minV=$minV")
      assert(minU < maxU, s"$init should vary u")
      assert(maxV > minV, s"$init should vary v")
    }
  }

  test("CirclesInitializer seeds interior away from corner background") {
    val model = new GrayScottModel(80, 80)
    model.setInitializer(Initializer.CirclesInitializer)
    // Corner should remain background after stampSquare + circles
    assert(model.u(0, 0) == 1.0)
    assert(model.v(0, 0) == 0.0)
    // A point near the first circle center should be seeded
    val cx = 80 / 4 + 40
    val cy = 80 / 4 + 30
    assert(model.u(cx, cy) == Initializer.INITIAL_U)
    assert(model.v(cx, cy) == Initializer.INITIAL_V)
  }
}
