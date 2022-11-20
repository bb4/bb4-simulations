// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.voronoi.algorithm

import scala.util.Random

object ActiveList {
  private val RND = new Random(0)
}

/**
  * The active list maintains a list of up to maxPoints points and allows
  * selecting and deleting from them randomly in O(1) time.
  */
case class ActiveList(maxPoints: Int, rnd: Random = RND) {

  private val array: Array[Int] = Array.fill(maxPoints)(-1)
  private var currentArrayLength = 0

  def removeRandomElement(): Int = {
    if (currentArrayLength == 0) {
      throw new IllegalAccessException("Cannot remove an element when there are none!")
    }
    val index = rnd.nextInt(currentArrayLength)
    val value = array(index)
    if (index < currentArrayLength - 1) {
      array(index) = array(currentArrayLength - 1)
    }
    currentArrayLength -= 1
    value
  }

  def addElement(index: Int): Unit = {
    assert(index >= 0)
    array(currentArrayLength) = index
    currentArrayLength += 1
  }

  def isEmpty: Boolean = array.isEmpty

  def getSize: Int = currentArrayLength
}
