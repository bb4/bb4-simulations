// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagators

import com.barrybecker4.simulation.waveFunctionCollapse.model.IntArray
import com.barrybecker4.simulation.waveFunctionCollapse.model.data.Neighbor

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class SimpleTiledPropagator(
  tCounter: Int,
  action: Seq[IntArray],
  neighbors: Array[Neighbor],
  firstOccurrence: mutable.Map[String, Int],
  subset: Seq[String]
) extends Propagator {

  private val tempPropagator: Array[Array[Array[Boolean]]] = Array.fill(4)(null)

  for (d <- 0 to 3) {
    tempPropagator(d) = Array.fill(tCounter)(null)
    propagator(d) = Array.fill(tCounter)(null)
    for (t <- 0 until tCounter)
      tempPropagator(d)(t) = Array.fill(tCounter)(false)
  }

  for (neighbor <- neighbors) {
    val left = neighbor.left.split(" ").filter(_.nonEmpty)
    val right = neighbor.right.split(" ").filter(_.nonEmpty)

    if (subset == null || (subset.contains(left(0)) && subset.contains(right(0)))) {
      val leftPosition: Int = action(firstOccurrence(left(0)))(if (left.length == 1) 0 else left(1).toInt)
      val downPosition: Int = action(leftPosition)(1)
      val rightPosition: Int = action(firstOccurrence(right(0)))(if (right.length == 1) 0 else right(1).toInt)
      val upPosition: Int = action(rightPosition)(1)

      tempPropagator(0)(rightPosition)(leftPosition) = true
      tempPropagator(0)(action(rightPosition)(6))(action(leftPosition)(6)) = true
      tempPropagator(0)(action(leftPosition)(4))(action(rightPosition)(4)) = true
      tempPropagator(0)(action(leftPosition)(2))(action(rightPosition)(2)) = true

      tempPropagator(1)(upPosition)(downPosition) = true
      tempPropagator(1)(action(downPosition)(6))(action(upPosition)(6)) = true
      tempPropagator(1)(action(upPosition)(4))(action(downPosition)(4)) = true
      tempPropagator(1)(action(downPosition)(2))(action(upPosition)(2)) = true
    }
  }

  for (t2 <- 0 until tCounter) {
    for (t1 <- 0 until tCounter) {
      tempPropagator(2)(t2)(t1) = tempPropagator(0)(t1)(t2)
      tempPropagator(3)(t2)(t1) = tempPropagator(1)(t1)(t2)
    }
  }

  val sparsePropagator: Array[Array[ListBuffer[Int]]] = Array.fill(4)(null)

  for (d <- 0 to 3)
    sparsePropagator(d) = Array.fill(tCounter)(new ListBuffer[Int]())

  for (d <- 0 to 3) {
    for (t1 <- 0 until tCounter) {
      val sp: ListBuffer[Int] = sparsePropagator(d)(t1)
      val tp = tempPropagator(d)(t1)
      for (t2 <- 0 until tCounter)
        if (tp(t2)) sp.append(t2)

      val size = sp.size
      propagator(d)(t1) = new IntArray(size)
      for (st <- 0 until size)
        propagator(d)(t1)(st) = sp(st)
    }
  }
}
