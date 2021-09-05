/* Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.utils.Utils

import java.awt.image.BufferedImage
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


abstract class Model(name: String, val FMX: Int, val FMY: Int) {

  var dxFun: IntArray = Array[Int](-1, 0, 1, 0)
  var dyFun: IntArray = Array[Int](0, 1, 0, -1)
  private val opposite = Array[Int](2, 3, 0, 1)

  var wave: Array[WaveCell] = _
  var hasObserved = false
  var propagator: Array[Array[IntArray]] = _
  private var stack: Array[(Int, Int)] = _
  private var random: Random = _
  var weights: DoubleArray = _
  private var weightLogWeights: DoubleArray = _
  private var sumOfWeights: Double = 0
  private var sumOfWeightLogWeights: Double = 0
  private var startingEntropy: Double = 0
  protected var tCounter: Int = 0
  private var stackSize: Int = 0
  var periodic: Boolean = false

  def getName: String = name

  private def init(): Unit = {

    wave = Array.fill(FMX * FMY)(null)

    for (i <- 0 until wave.length) {
      val waveCell = new WaveCell()
      wave(i) = waveCell
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

    stack = Array.fill(wave.length * tCounter)(null)
    stackSize = 0
  }

  private def observe(): Option[Boolean] = {
    var min = 1E+3
    var argMin = -1

    for (i <- 0 until wave.length) {
      if (!onBoundary(i % FMX, i / FMX)) {
        val amount = wave(i).sumOfOnes
        if (amount == 0) return Some(false)

        val entropy = wave(i).entropy
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
      for (i <- 0 until wave.length) {
        breakable {
          val waveCell = wave(i)
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
      distribution(t) = if (wave(argMin).enabled != null && wave(argMin).enabled(t)) weights(t) else 0.0
    }
    val r = Utils.randomFromArray(distribution, random.nextDouble())

    val waveCell = wave(argMin)
    for (t <- 0 until tCounter)
      if (waveCell.enabled != null && waveCell.enabled(t) != (t == r)) ban(argMin, t)

    None
  }

  def onBoundary(x: Int, y: Int): Boolean

  def ban(i: Int, t: Int): Unit = {
    val waveCell = wave(i)
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

  protected def propagate(): Unit = {
    while (stackSize > 0) {
      val e1 = stack(stackSize - 1)
      stackSize -= 1

      val i1 = e1._1
      val x1 = i1 % FMX
      val y1 = i1 / FMX

      for (d <- 0 to 3) {
        val dx = dxFun(d)
        val dy = dyFun(d)
        var x2 = x1 + dx
        var y2 = y1 + dy
        if (!onBoundary(x2, y2)) {
          if (x2 < 0) x2 += FMX
          else if (x2 >= FMX) x2 -= FMX
          if (y2 < 0) y2 += FMY
          else if (y2 >= FMY) y2 -= FMY

          val i2 = x2 + y2 * FMX
          val p = propagator(d)(e1._2) // check for null  propagator(d)?
          val compat = wave(i2).compatible

          for (l <- 0 until (if (p == null) 0 else p.length)) {
            val t2 = if (p == null) 0 else p(l)
            val comp = if (compat == null) null else compat(t2)

            if (comp != null) {
              comp(d) -= 1
              if (comp(d) == 0) ban(i2, t2)
            }
          }
        }
      }
    }
  }

  def run(seed: Int, limit: Int): Boolean = {
    if (wave == null) init()

    clear()
    random = new Random(seed)

    var l = 0
    do {
      val result = observe()
      if (result.isDefined) {
        return result.get
      }
      propagate()
      l += 1
    } while (l < limit || limit == 0)

    true
  }

  def clear(): Unit = {
    for (i <- 0 until wave.length) {
      val waveCell = wave(i)
      for (t <- 0 until tCounter) {
        if (waveCell != null) {
          waveCell.enabled(t) = true
          for (d <- 0 to 3)
            waveCell.compatible(t)(d) = propagator(opposite(d))(t).length
        }
      }

      waveCell.sumOfOnes = weights.length
      waveCell.sumOfWeights = sumOfWeights
      waveCell.sumOfWeightLogWeights = sumOfWeightLogWeights
      waveCell.entropy = startingEntropy
    }
  }

  def graphics(): BufferedImage
}
