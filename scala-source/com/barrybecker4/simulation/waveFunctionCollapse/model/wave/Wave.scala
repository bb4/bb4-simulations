// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.wave

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.PropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave.{DX, DY, INITIAL_ENTROPY, UNSET, DEFAULT_NOISE_SCALE, OPPOSITE}
import com.barrybecker4.simulation.waveFunctionCollapse.model.{DoubleArray, IntArray}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.Utils

import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


object Wave {
  val OPPOSITE: IntArray = Array[Int](2, 3, 0, 1)
  val DX: IntArray = Array[Int](-1, 0, 1, 0)
  val DY: IntArray = Array[Int](0, 1, 0, -1)

  private val INITIAL_ENTROPY: Double = 1E+3
  private val UNSET: Int = -1
  private val DEFAULT_NOISE_SCALE = 1E-6  // was 1E-6
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
    println("Wave.init stack total size = " + (waveCells.length * tCounter))
    stackSize = 0
  }


  /**
    * @return true if in observed state (i.e. complete),
    *         false if done but inconsistent,
    *         None if not done yet  */
  def observe(
    tCounter: Int, weights: DoubleArray,
    onBoundary: (Int, Int) => Boolean,
    random: Random): Option[Boolean] = {
    synchronized {
      //println("observe thread = " + Thread.currentThread().getName)
      var min = INITIAL_ENTROPY
      var argMin = UNSET

      for (i <- 0 until size()) {
        if (!onBoundary(i % FMX, i / FMX)) {
          val amount = waveCells(i).sumOfOnes
          if (amount == 0)
            return Some(false) // inconsistent state

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

      if (argMin == UNSET) {
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
        if (waveCell.enabled != null && waveCell.enabled(t) != (t == r))
          ban(argMin, t, weights)

      None
    }
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

    // getting called by "Thread-0" and "AWT-EventQueue-0"
    waveCell.updateEntropy(weights(t), weightLogWeights(t))
  }

  private def getStackNullIndices: Seq[Int] =
    stack.zipWithIndex.take(stackSize).filter({ case (value, _) => value == null }).map(v => v._2)

  def propagate(onBoundary: (Int, Int) => Boolean, weights: DoubleArray, propState: PropagatorState): Unit = {
    while (stackSize > 0) {
      stackSize -= 1
      val e1 = stack(stackSize)

      if (e1 == null) {
        println(s"WARNING: e1 was null at position $stackSize of total = ${stack.length}. ")
        //throw new IllegalStateException(s"e1 was null at position ${stackSize - 1} of total = ${stack.length}. ")
      }
      else doPropagation(e1, onBoundary, weights, propState)
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
