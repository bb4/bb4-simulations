// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.util

import scala.util.Random


object SkewedRandom {
  private val RND: Random = Random(0)
}

/**
 * Allows generating skewed gaussian random numbers.
 */
class SkewedRandom(rnd: Random = RND) {

  /**
   * Derived from https://stackoverflow.com/questions/5853187/skewing-java-random-number-generation-toward-a-certain-number
   * Todo: move to bb4-math
   * @param min minimum value, inclusive
   * @param max maximum value, inclusive
   * @param skew degree to which the values cluster around the mode of the distribution.
   *             Higher values mean tighter clustering. If one, the standard deviation of 1.
   *             If 0, then values will either be high or low end of the range.
   *             Of 0.6, then very close to uniform distribution.
   * @param bias tendency of the mode to approach the min, max or midpoint value.
   *             Positive values bias toward max, negative values toward min
   * @return skewed gaussian random number
   */
  def nextSkewedGaussian(min: Double, max: Double, skew: Double, bias: Double): Double = {
    val range: Double = max - min;
    val mid: Double = min + range / 2.0;
    val unitGaussian: Double = rnd.nextGaussian();
    val biasFactor: Double = Math.exp(bias);
    mid + (range * (biasFactor / (biasFactor + Math.exp(-unitGaussian / skew)) - 0.5))
  }

}
