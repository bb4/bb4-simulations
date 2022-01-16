// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.math.random.SkewedRandom
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions

import scala.util.Random


object BoxSplitter {
  private val RND: Random = Random(0)
}

case class BoxSplitter(roomOptions: RoomOptions, rnd: Random = RND) {

  val marginWidth: Int = roomOptions.getMaxPaddedWidth
  val marginHeight: Int = roomOptions.getMaxPaddedHeight
  val minDimension: Int = roomOptions.getMinPaddedDim
  val skewedRnd: SkewedRandom = SkewedRandom(rnd)

  private val skew = roomOptions.randomSkew
  private val bias = roomOptions.randomBias

  assert(marginWidth > minDimension)
  assert(marginHeight > minDimension)

  def splitHorizontally(box: Box): (Box, Box) = {
    val leftX = box.getTopLeftCorner.getX
    val rightX = box.getBottomRightCorner.getX
    val splitX = findSplit(leftX, rightX, marginWidth)

    val leftBox = Box(box.getTopLeftCorner.getY, leftX, box.getBottomRightCorner.getY, splitX)
    val rightBox = Box(box.getTopLeftCorner.getY, splitX, box.getBottomRightCorner.getY, rightX)

    (leftBox, rightBox)
  }

  def splitVertically(box: Box): (Box, Box) = {
    val topY = box.getTopLeftCorner.getY
    val bottomY = box.getBottomRightCorner.getY
    val splitY = findSplit(topY, bottomY, marginHeight)

    val bottomBox = Box(bottomY, box.getTopLeftCorner.getX, splitY, box.getBottomRightCorner.getX)
    val topBox = Box(splitY, box.getTopLeftCorner.getX, topY, box.getBottomRightCorner.getX)

    (bottomBox, topBox)
  }

  // try using gaussian here
  private def findSplit(low: Int, high: Int, margin: Int): Int = {

    var diff = high - margin - (low + margin)
    var padding = margin
    if (diff < 0) {
      diff = high - minDimension - (low + minDimension)
      padding = minDimension
    }
    //val randomOffset = rnd.nextInt(diff + 1)
    val randomOffset = (skewedRnd.nextSkewedGaussian(0, diff, skew,bias)).toInt

    val middle = low + padding + randomOffset
    assert(middle != low && middle != high,
      s"The middle=$middle was unexpectedly the same as low=$low or high=$high")
    assert(middle - low >= minDimension, "middle - low = " + (middle - low))
    assert(high - middle >= minDimension, "high - middle = " + (high - middle))
    middle
  }
}
