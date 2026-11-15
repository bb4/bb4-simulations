// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagation

import com.barrybecker4.simulation.waveFunctionCollapse.model.IntArray
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled.Neighbor
import com.barrybecker4.simulation.waveFunctionCollapse.utils.WfcDebug

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class SimpleTiledPropagatorState(
  tCounter: Int,
  action: Seq[IntArray],
  neighbors: Array[Neighbor],
  firstOccurrence: mutable.Map[String, Int],
  subsets: Seq[String]
) extends PropagatorState {

  init()

  private def init(): Unit = {
    val tempPropagator = initTempPropagator()
    populateTempPropagator(tempPropagator)
    compactToSparse(tempPropagator)
  }

  private def initTempPropagator(): Array[Array[Array[Boolean]]] = {
    val tempPropagator: Array[Array[Array[Boolean]]] = Array.fill(4)(null)
    for (d <- 0 to 3) {
      tempPropagator(d) = Array.fill(tCounter)(null)
      state(d) = Array.fill(tCounter)(null)
      for (t <- 0 until tCounter)
        tempPropagator(d)(t) = Array.fill(tCounter)(false)
    }
    tempPropagator
  }

  private def tileActionIndex(tokens: Array[String]): Int = {
    val base = firstOccurrence(tokens(0))
    val sub = if (tokens.length == 1) 0 else tokens(1).toInt
    action(base)(sub)
  }

  private def markHorizontalAdjacency(
      tempPropagator: Array[Array[Array[Boolean]]],
      leftPosition: Int,
      rightPosition: Int): Unit = {
    tempPropagator(0)(rightPosition)(leftPosition) = true
    tempPropagator(0)(action(rightPosition)(6))(action(leftPosition)(6)) = true
    tempPropagator(0)(action(leftPosition)(4))(action(rightPosition)(4)) = true
    tempPropagator(0)(action(leftPosition)(2))(action(rightPosition)(2)) = true
  }

  private def markVerticalAdjacency(
      tempPropagator: Array[Array[Array[Boolean]]],
      downPosition: Int,
      upPosition: Int): Unit = {
    tempPropagator(1)(upPosition)(downPosition) = true
    tempPropagator(1)(action(downPosition)(6))(action(upPosition)(6)) = true
    tempPropagator(1)(action(upPosition)(4))(action(downPosition)(4)) = true
    tempPropagator(1)(action(downPosition)(2))(action(upPosition)(2)) = true
  }

  private def populateTempPropagator(tempPropagator: Array[Array[Array[Boolean]]]): Unit = {
    if (neighbors != null) {
      for (neighbor <- neighbors) {
        val left = neighbor.left.split(" ").filter(_.nonEmpty)
        val right = neighbor.right.split(" ").filter(_.nonEmpty)

        if (subsets == null || (subsets.contains(left(0)) && subsets.contains(right(0)))) {
          val leftPosition = tileActionIndex(left)
          val downPosition = action(leftPosition)(1)
          val rightPosition = tileActionIndex(right)
          val upPosition = action(rightPosition)(1)

          markHorizontalAdjacency(tempPropagator, leftPosition, rightPosition)
          markVerticalAdjacency(tempPropagator, downPosition, upPosition)
        }
      }
    } else if (WfcDebug.enabled) println("no neighbors for this")

    for (t2 <- 0 until tCounter) {
      for (t1 <- 0 until tCounter) {
        tempPropagator(2)(t2)(t1) = tempPropagator(0)(t1)(t2)
        tempPropagator(3)(t2)(t1) = tempPropagator(1)(t1)(t2)
      }
    }
  }

  private def compactToSparse(tempPropagator: Array[Array[Array[Boolean]]]): Unit = {
    for (d <- 0 to 3) {
      for (t1 <- 0 until tCounter) {
        val sp = new ListBuffer[Int]()
        val tp = tempPropagator(d)(t1)
        for (t2 <- 0 until tCounter)
          if (tp(t2)) sp.append(t2)

        val size = sp.size
        state(d)(t1) = new IntArray(size)
        for (st <- 0 until size)
          state(d)(t1)(st) = sp(st)
      }
    }
  }
}
