/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

/**
  * Flat concentration grid with fully periodic (toroidal) indexing.
  * Layout is column-major (`index = x * height + y`) so x-strips are contiguous blocks.
  */
final class ConcentrationField(val width: Int, val height: Int) {
  require(width > 0 && height > 0, s"width and height must be positive, got ${width}x$height")

  private val data: Array[Double] = Array.ofDim[Double](width * height)

  private def index(x: Int, y: Int): Int = x * height + y

  def apply(x: Int, y: Int): Double = data(index(x, y))

  def update(x: Int, y: Int, value: Double): Unit = { data(index(x, y)) = value }

  def add(x: Int, y: Int, amount: Double): Unit = { data(index(x, y)) += amount }

  /** Discrete Laplacian: sum of 4 periodic neighbors minus 4 * center. */
  def laplacian(x: Int, y: Int): Double = {
    val center = apply(x, y)
    apply(ConcentrationField.wrap(x + 1, width), y) +
      apply(ConcentrationField.wrap(x - 1, width), y) +
      apply(x, ConcentrationField.wrap(y + 1, height)) +
      apply(x, ConcentrationField.wrap(y - 1, height)) -
      4.0 * center
  }

  def copyFrom(other: ConcentrationField): Unit = {
    require(width == other.width && height == other.height)
    Array.copy(other.data, 0, data, 0, data.length)
  }

  def fill(value: Double): Unit = {
    var i = 0
    while (i < data.length) {
      data(i) = value
      i += 1
    }
  }
}

object ConcentrationField {
  /** Periodic boundary index in `[0, max)`. */
  def wrap(i: Int, max: Int): Int = java.lang.Math.floorMod(i, max)
}
