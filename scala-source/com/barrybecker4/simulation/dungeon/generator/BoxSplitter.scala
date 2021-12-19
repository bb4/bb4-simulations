// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.Box

import scala.util.Random

object BoxSplitter {
  val RND = Random(0)
}

case class BoxSplitter(marginWidth: Int, marginHeight: Int, minDimension: Int, rnd: Random = RND) {

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
    assert (high - low >= 2 * minDimension)
    var diff = high - margin - (low + margin)
    var padding = margin
    if (diff < 0) {
      diff = high - minDimension - (low + minDimension)
      padding = minDimension
    }
    val middle = low + padding + rnd.nextInt(diff + 1)
    assert(middle != low && middle != high, s"The middle=$middle was unexpectedly the same as low=$low or high=$high")
    middle
  }
}
