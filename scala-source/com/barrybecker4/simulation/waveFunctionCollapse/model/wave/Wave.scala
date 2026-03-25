// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.wave

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.PropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave.{DEFAULT_NOISE_SCALE, DX, DY, INCONSISTENT, INITIAL_ENTROPY, OPPOSITE, UNSET}
import com.barrybecker4.simulation.waveFunctionCollapse.model.{DoubleArray, IntArray}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.Utils

import scala.compiletime.uninitialized

import scala.util.Random


object Wave {
  val OPPOSITE: IntArray = Array[Int](2, 3, 0, 1)
  val DX: IntArray = Array[Int](-1, 0, 1, 0)
  val DY: IntArray = Array[Int](0, 1, 0, -1)

  private val INITIAL_ENTROPY: Double = 1E+3
  private val UNSET: Int = -1
  private val INCONSISTENT: Int = -2
  private val DEFAULT_NOISE_SCALE = 1E-6
}

/**
  * The state of the solver
  */
class Wave(val FMX: Int, val FMY: Int, allowInconsistencies: Boolean = true) {

  private val waveCells: Array[WaveCell] = Array.fill(FMX * FMY)(null)

  /** Pair stacks avoid storing null as a dummy tuple in Scala 3. */
  private var stackCell: Array[Int] = uninitialized
  private var stackTile: Array[Int] = uninitialized
  private var weightLogWeights: DoubleArray = uninitialized
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

    val cap = waveCells.length * tCounter
    stackCell = Array.ofDim(cap)
    stackTile = Array.ofDim(cap)
    stackSize = 0
  }


  /**
    * Finds the most constrained variable and picks a value to assign by weighted random sampling.
    *
    * @return true if in observed state (i.e. complete),
    *         false if done but inconsistent,
    *         None if not done yet
    */
  def observe(
    tCounter: Int, weights: DoubleArray,
    onBoundary: (Int, Int) => Boolean,
    random: Random): Option[Boolean] = {
    synchronized {
      val minEntropy = calculateMinEntropy(onBoundary, random)

      if (minEntropy == INCONSISTENT)
        Some(false)
      else if (minEntropy == UNSET) {
        hasObserved = true
        markFullyObserved(tCounter)
        Some(true)
      }
      else {
        collapseMinEntropyCell(minEntropy, tCounter, weights, random)
        None
      }
    }
  }

  private def markFullyObserved(tCounter: Int): Unit = {
    for (i <- 0 until size()) {
      val waveCell = waveCells(i)
      var t = 0
      while (t < tCounter) {
        if (waveCell.enabled != null && waveCell.enabled(t)) {
          waveCell.observed = t
          t = tCounter
        } else t += 1
      }
    }
  }

  private def collapseMinEntropyCell(
      minEntropyIdx: Int,
      tCounter: Int,
      weights: DoubleArray,
      random: Random): Unit = {
    val distribution = Array.fill(tCounter)(0.0)
    for (t <- 0 until tCounter)
      distribution(t) =
        if (waveCells(minEntropyIdx).enabled != null && waveCells(minEntropyIdx).enabled(t)) weights(t) else 0.0

    val r = Utils.randomFromArray(distribution, random.nextDouble())
    val waveCell = waveCells(minEntropyIdx)
    for (t <- 0 until tCounter)
      if (waveCell.enabled != null && waveCell.enabled(t) != (t == r))
        ban(minEntropyIdx, t, weights)
  }

  /**
    * Entropy is used to identify the most constrained cell (least risky choice).
    * A uniform distribution of possibilities has the highest entropy.
    */
  private def calculateMinEntropy(onBoundary: (Int, Int) => Boolean, random: Random): Int = {
    var min = INITIAL_ENTROPY
    var argMin = UNSET
    var inconsistent = false
    var i = 0
    while (i < size() && !inconsistent) {
      if (!onBoundary(i % FMX, i / FMX)) {
        val amount = waveCells(i).sumOfOnes
        if (amount == 0)
          inconsistent = true
        else {
          val entropy = waveCells(i).entropy
          if (amount > 1 && entropy <= min) {
            val noise = DEFAULT_NOISE_SCALE * random.nextDouble()
            if (entropy + noise < min) {
              min = entropy + noise
              argMin = i
            }
          }
        }
      }
      i += 1
    }
    if (inconsistent) INCONSISTENT else argMin
  }

  /** remove patterns from the domains of the cells */
  def ban(i: Int, t: Int, weights: DoubleArray): Unit = {
    if (i >= waveCells.length) return
    val waveCell = waveCells(i)

    if (allowInconsistencies && waveCells(i).sumOfOnes == 1) {
      return
    }

    if (waveCell.enabled != null)
      waveCell.enabled(t) = false

    if (waveCell.compatible != null) {
      val comp = waveCell.compatible(t)
      for (d <- 0 to 3) comp(d) = 0
    }

    stackCell(stackSize) = i
    stackTile(stackSize) = t
    stackSize += 1

    waveCell.updateEntropy(weights(t), weightLogWeights(t))
  }

  def propagate(onBoundary: (Int, Int) => Boolean, weights: DoubleArray, propState: PropagatorState): Unit = {
    while (stackSize > 0) {
      stackSize -= 1
      val cellIdx = stackCell(stackSize)
      val tileIdx = stackTile(stackSize)
      doPropagation((cellIdx, tileIdx), onBoundary, weights, propState)
    }
  }

  private def doPropagation(
    e1: (Int, Int), onBoundary: (Int, Int) => Boolean,
    weights: DoubleArray, propState: PropagatorState): Unit = {

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
        val p = propState.get(d, e1._2)
        val compat = waveCells(i2).compatible

        for (l <- 0 until (if (p == null) 0 else p.length)) {
          val t2 = p(l)
          val comp = if (compat == null) null else compat(t2)

          if (comp != null) {
            comp(d) -= 1
            if (comp(d) == 0) ban(i2, t2, weights)
          }
        }
      }
    }
  }

  def clear(tCounter: Int, weights: DoubleArray, propagator: PropagatorState): Unit = {
    for (i <- 0 until size()) {
      val waveCell = waveCells(i)
      for (t <- 0 until tCounter) {
        waveCell.enabled(t) = true
        for (d <- 0 to 3)
          waveCell.compatible(t)(d) = propagator.get(OPPOSITE(d), t).length
      }

      waveCell.sumOfOnes = weights.length
      waveCell.sumOfWeights = sumOfWeights
      waveCell.sumOfWeightLogWeights = sumOfWeightLogWeights
      waveCell.entropy = startingEntropy
    }
  }
}
