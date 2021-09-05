// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.wave

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.Propagator
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave.{DX, DY, OPPOSITE}
import com.barrybecker4.simulation.waveFunctionCollapse.model.{DoubleArray, IntArray}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.Utils

import scala.util.Random
import scala.util.control.Breaks.{break, breakable}

object Wave {
  val OPPOSITE: IntArray = Array[Int](2, 3, 0, 1)
  val DX: IntArray = Array[Int](-1, 0, 1, 0)
  val DY: IntArray = Array[Int](0, 1, 0, -1)
}


class Wave(val FMX: Int, val FMY: Int) {

  private val waveCells: Array[WaveCell] = Array.fill(FMX * FMY)(null)

  private var stack: Array[(Int, Int)] = _
  private var weightLogWeights: DoubleArray = _
  private var sumOfWeights: Double = 0
  private var sumOfWeightLogWeights: Double = 0
  private var startingEntropy: Double = 0
  private var stackSize: Int = 0
  var hasObserved = false

  def get(i: Int): WaveCell = waveCells(i)
  def size(): Int = waveCells.length

  def init(tCounter: Int, weights: DoubleArray): Unit = {
    for (i <- 0 until size()) {
      val waveCell = new WaveCell()
      waveCells(i) = waveCell
      waveCell.enabled = Array.fill(tCounter)(false)
      waveCell.compatible = Array.fill(tCounter)(null)

      for (t <- 0 until tCounter) {
        if (waveCell.compatible != null) {
          waveCell.compatible(t) = Array.fill(4)(0)
        }
      }
    }

    weightLogWeights = Array.ofDim[Double](tCounter)
    sumOfWeights = 0.0
    sumOfWeightLogWeights = 0.0

    for (t <- 0 until tCounter) {
      weightLogWeights(t) = weights(t) * Math.log(weights(t))
      sumOfWeights += weights(t)
      sumOfWeightLogWeights += weightLogWeights(t)
    }

    startingEntropy = Math.log(sumOfWeights) - sumOfWeightLogWeights / sumOfWeights

    stack = Array.fill(waveCells.length * tCounter)(null)
    stackSize = 0
  }


  /** @return true if in observed state. i.e complete. */
  def observe(
    tCounter: Int, weights: DoubleArray,
    onBoundary: (Int, Int) => Boolean,
    random: Random): Option[Boolean] = {

    var min = 1E+3
    var argMin = -1

    for (i <- 0 until size()) {
      if (!onBoundary(i % FMX, i / FMX)) {
        val amount = waveCells(i).sumOfOnes
        if (amount == 0) return Some(false)

        val entropy = waveCells(i).entropy
        if (amount > 1 && entropy <= min) {
          val noise = 1E-6 * random.nextDouble()
          if (entropy + noise < min) {
            min = entropy + noise
            argMin = i
          }
        }
      }
    }

    if (argMin == -1) {
      hasObserved = true
      for (i <- 0 until size()) {
        breakable {
          val waveCell = waveCells(i)
          for (t <- 0 until tCounter) {
            if (waveCell.enabled != null && waveCell.enabled(t)) {
              waveCell.observed = t
              break()
            }
          }
        }
      }
      return Some(true)
    }

    val distribution = Array.fill(tCounter)(0.0)
    for (t <- 0 until tCounter) {
      distribution(t) = if (waveCells(argMin).enabled != null && waveCells(argMin).enabled(t)) weights(t) else 0.0
    }
    val r = Utils.randomFromArray(distribution, random.nextDouble())

    val waveCell = waveCells(argMin)
    for (t <- 0 until tCounter)
      if (waveCell.enabled != null && waveCell.enabled(t) != (t == r)) ban(argMin, t, weights)

    None
  }

  def ban(i: Int, t: Int, weights: DoubleArray): Unit = {
    val waveCell = waveCells(i)
    if (waveCell.enabled != null)
      waveCell.enabled(t) = false

    if (waveCell.compatible != null) {
      val comp = waveCell.compatible(t)
      for (d <- 0 to 3) comp(d) = 0
    }

    stack(stackSize) = (i, t)
    stackSize += 1

    waveCell.updateEntropy(weights(t), weightLogWeights(t))
  }

  def propagate(onBoundary: (Int, Int) => Boolean, weights: DoubleArray, propagator: Propagator): Unit = {
    while (stackSize > 0) {
      val e1 = stack(stackSize - 1)
      stackSize -= 1

      val i1 = e1._1
      val x1 = i1 % FMX
      val y1 = i1 / FMX

      for (d <- 0 to 3) {
        val dx = DX(d)
        val dy = DY(d)
        var x2 = x1 + dx
        var y2 = y1 + dy
        if (!onBoundary(x2, y2)) {
          if (x2 < 0) x2 += FMX
          else if (x2 >= FMX) x2 -= FMX
          if (y2 < 0) y2 += FMY
          else if (y2 >= FMY) y2 -= FMY

          val i2 = x2 + y2 * FMX
          val p = propagator.get(d, e1._2)
          val compat = waveCells(i2).compatible

          for (l <- 0 until (if (p == null) 0 else p.length)) {
            val t2 = if (p == null) 0 else p(l)
            val comp = if (compat == null) null else compat(t2)

            if (comp != null) {
              comp(d) -= 1
              if (comp(d) == 0) ban(i2, t2, weights)
            }
          }
        }
      }
    }
  }

  def clear(tCounter: Int, weights: DoubleArray, propagator: Propagator): Unit = {
    for (i <- 0 until size()) {
      val waveCell = waveCells(i)
      for (t <- 0 until tCounter) {
        if (waveCell != null) {
          waveCell.enabled(t) = true
          for (d <- 0 to 3)
            waveCell.compatible(t)(d) = propagator.get(OPPOSITE(d), t).length
        }
      }

      waveCell.sumOfOnes = weights.length
      waveCell.sumOfWeights = sumOfWeights
      waveCell.sumOfWeightLogWeights = sumOfWeightLogWeights
      waveCell.entropy = startingEntropy
    }
  }
}
