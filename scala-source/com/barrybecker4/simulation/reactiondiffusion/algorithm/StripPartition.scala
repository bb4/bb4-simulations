/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

/**
  * Half-open column (or row) ranges for parallel strips covering `[0, length)`.
  */
object StripPartition {

  /**
    * @param length   total size to cover (e.g. grid width)
    * @param numStrips number of strips (at least 1; capped by length when length > 0)
    * @return array of `(minInclusive, maxExclusive)` pairs
    */
  def ranges(length: Int, numStrips: Int): Array[(Int, Int)] = {
    require(length >= 0, s"length must be non-negative, got $length")
    if (length == 0) return Array.empty
    val n = math.max(1, math.min(numStrips, length))
    val range = length / n
    val result = Array.ofDim[(Int, Int)](n)
    var i = 0
    while (i < n - 1) {
      val offset = i * range
      result(i) = (offset, offset + range)
      i += 1
    }
    result(n - 1) = (range * (n - 1), length)
    result
  }
}
