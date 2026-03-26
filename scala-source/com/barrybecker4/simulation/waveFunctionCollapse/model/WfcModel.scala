// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.PropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave
import com.barrybecker4.simulation.waveFunctionCollapse.utils.WfcDebug

import java.awt.Dimension
import java.awt.image.BufferedImage
import scala.compiletime.uninitialized
import scala.util.Random
import scala.util.boundary
import scala.util.boundary.break

/**
  * For a good explanation of the algorithm, see
  * https://escholarship.org/content/qt1fb9k44q/qt1fb9k44q_noSplash_1c5dcf5090d4595f7605b2653c89b245.pdf?t=qwpcsb
  */
abstract class WfcModel(name: String,
                        val FMX: Int, val FMY: Int,
                        limit: Int, allowInconsistencies: Boolean = true) {

  protected var wave: Wave = uninitialized
  protected var propagator: PropagatorState = uninitialized
  protected var weights: DoubleArray = uninitialized
  protected var periodic: Boolean = false
  protected var tCounter: Int = 0
  protected var dimensions: Dimension = uninitialized
  private var random: Random = uninitialized
  private var ready: Boolean = false
  private var iterationCt = 0
  private var result: Option[Boolean] = None

  def getName: String = name
  def onBoundary(x: Int, y: Int): Boolean
  def isReady: Boolean = ready

  /**
    * Runs the solver for up to `limit` observe/propagate steps (or 10M if limit is 0).
    *
    * @return true iff the wave fully collapsed successfully (`advance` returned `Some(true)`).
    */
  def runWithLimit(seed: Int, limit: Int = this.limit): Boolean = {
    ready = false
    if (wave == null) {
      wave = new Wave(dimensions.width, dimensions.height, allowInconsistencies)
      wave.init(tCounter, weights)
    }

    clear()
    random = new Random(seed)
    iterationCt = 0
    val steps = if (limit == 0) 10000000 else limit
    val outcome = advance(steps)
    ready = true
    outcome.contains(true)
  }

  /** One observe/propagate step. Returns whether the run completed successfully. */
  def startRun(seed: Int): Boolean = {
    runWithLimit(seed, 1)
  }

  /**
    * function Solve(adjacencies, wave_matrix):
    *   loop while no contradiction:
    *     wave_matrix := Propagate(wave_matrix, adjacencies)
    *     if all domains collapsed:
    *       return wave_matrix
    *     wave_matrix := Observe(wave_matrix)
    *   throw error: generation attempt failed
    *
    * @return None if not done computing, return true if done successfully; false if done unsuccessfully
    */
  def advance(steps: Int): Option[Boolean] = synchronized {
    boundary:
      if (random == null) {
        if (WfcDebug.enabled) println("no advance. Result found")
        break(None)
      }

      for (i <- 0 until steps) {
        result = wave.observe(tCounter, weights, onBoundary, random)
        if (result.isDefined) {
          ready = true
          if (!result.get && WfcDebug.enabled) {
            println("*** Inconsistent state. No solution found!")
          }
          break(result)
        }
        wave.propagate(onBoundary, weights, propagator)
        iterationCt += 1
        if (iterationCt % 100 == 0 && WfcDebug.enabled) println(s"$iterationCt, ")
      }
      if (WfcDebug.enabled) println(s"iteration=$iterationCt")
      None
  }

  def clear(): Unit = wave.clear(tCounter, weights, propagator)
  def graphics(): BufferedImage
}
