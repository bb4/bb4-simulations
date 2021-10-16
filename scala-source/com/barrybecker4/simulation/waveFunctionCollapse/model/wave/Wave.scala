// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.wave

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.PropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave.{DEFAULT_NOISE_SCALE, DX, DY, INCONSISTENT, INITIAL_ENTROPY, OPPOSITE, UNSET}
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
  private val INCONSISTENT: Int = -2
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

    println(s"creating stack of size ${waveCells.length} * $tCounter" )
    stack = Array.fill(waveCells.length * tCounter)(null)
    println("Wave.init stack total size = " + (waveCells.length * tCounter))
    stackSize = 0
  }


  /**
    * Finds the most constrained variable and picks a value to assign by weighted random sampling.
    *
    * function Observe(wave_matrix):
    *   # find the minimum entropy cell in grid
    *   cell = FindMinimumEntropy(wave_matrix)
    *   If any cell has zero possibilities:
    *     # this is contradictory state, where a node has an empty domain
    *     return paradox failure exception
    *   If all cells have exactly one possibility:
    *     return wave_matrix
    *   In node with the least entropy (ties broken randomly):
    *     Assign a value via weighted random sample of the cell’s current domain.
    *   Add node to the propagation stack.

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
        return Some(false)
      else if (minEntropy == UNSET) {
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
      for (t <- 0 until tCounter)
        distribution(t) = if (waveCells(minEntropy).enabled != null && waveCells(minEntropy).enabled(t)) weights(t) else 0.0

      val r = Utils.randomFromArray(distribution, random.nextDouble())

      val waveCell = waveCells(minEntropy)
      for (t <- 0 until tCounter)
        if (waveCell.enabled != null && waveCell.enabled(t) != (t == r))
          ban(minEntropy, t, weights)

      None
    }
  }

  /***
    * Entropy is used to identify the most constrained cell (least risky choice).
    * A uniform distribution of possibilities has the highest entropy.
    * The entropy of a cell is the weighted sum of patterns that may still be validly placed at that cell
    * (with weights based on how often that pattern was seen in the input image)
    * @return minimum entropy for cell
    */
  private def calculateMinEntropy(onBoundary: (Int, Int) => Boolean, random: Random): Int = {
    var min = INITIAL_ENTROPY
    var argMin = UNSET

    for (i <- 0 until size()) {
      if (!onBoundary(i % FMX, i / FMX)) {
        val amount = waveCells(i).sumOfOnes
        // If amount is 0, then no neighboring cells have any possibilities
        if (amount == 0)
          return INCONSISTENT

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
    argMin
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

  /**
    * Updates the domains of variables at each grid location.
    *
    * function Propagate(wave_matrix, adjacencies):
    *  Initialize the propagation stack to contain the last cell observed.
    *  while there are cells on the propagation stack:
    *    for neighbor of this cell:
    *      for neighbor_pattern in neighbor_domain(neighbor):
    *        if not( adjacency(original_pattern, neighbor_pattern) ):
    *          Decrement count of neighbor_pattern.
    *        if count is zero:
    *          Remove neighbor_pattern from neighbor_cell.
    *          Add neighbor_cell to the propagation stack.
    *   return wave_matrix
    *
    * Once a cell is solved (the domain of the associated variable is reduced to a single value),
    * WFC propagates the implications of the change to the neighboring cells. WFCs propagation
    * ensures that a value only appears in a domain of a variable if there exists a valid value in
    * the domain of related variables such that constraints over those variables could be satisfied.
    */
  def propagate(onBoundary: (Int, Int) => Boolean, weights: DoubleArray, propState: PropagatorState): Unit = {
    while (stackSize > 0) {
      stackSize -= 1
      val e1 = stack(stackSize)

      if (e1 == null) {
        //println(s"WARNING: e1 was null at position $stackSize of total = ${stack.length}. ")
        throw new IllegalStateException(s"e1 was null at position ${stackSize - 1} of total = ${stack.length}. ")
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
