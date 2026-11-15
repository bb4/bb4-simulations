// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeData
import com.barrybecker4.simulation.snake.geometry.HeadSegment
import com.barrybecker4.simulation.snake.geometry.Segment
import javax.vecmath.Point2d
import javax.vecmath.Vector2d
import scala.compiletime.uninitialized


/**
  * A single dynamic snake whose geometry comes from [[SnakeData]].
  * @param snakeData defines the snake geometry
  * @param locomotionParams shared parameters for mass, springs, and locomotion (same object as [[SnakeSimulator]]'s updater)
  */
class Snake(val snakeData: SnakeData, val locomotionParams: LocomotionParameters) {

  /** the array of segments which make up the snake */
  private var segment: Array[Segment] = uninitialized
  initFromData()

  def reset(): Unit = { resetFromData() }
  def getNumSegments: Int = snakeData.numSegments
  def getSegment(i: Int): Segment = segment(i)

  /** Apply current [[LocomotionParameters.massScale]] to all segment particles (call once per simulation step). */
  def syncParticleMassesFromLocomotionParams(): Unit = {
    var i = 0
    while (i < snakeData.numSegments) {
      getSegment(i).syncParticleMassFromLocomotionParams()
      i += 1
    }
  }

  /** use this if you need to avoid reading from a file */
  private def initFromData(): Unit = {
    segment = Array.ofDim[Segment](snakeData.numSegments)
    resetFromData()
  }

  private def resetFromData(): Unit = {
    var width1 = snakeData.widths(0)
    var width2 = snakeData.widths(1)
    val numSegments = snakeData.numSegments
    val segmentLength = snakeData.segmentLength
    val length = 80 + numSegments * segmentLength
    var segment: Segment = new HeadSegment(width1, width2, segmentLength, new Point2d(length, 320.0), this)
    this.segment(0) = segment
    var segmentInFront = segment
    // Segment i uses front cross-section widths(i) and rear widths(i+1).
    width1 = width2
    for (i <- 1 until numSegments) {
      width2 = snakeData.widths(i + 1)
      segment = new Segment(width1, width2, segmentLength, segmentInFront, i, this)
      this.segment(i) = segment
      segmentInFront = segment
      width1 = width2
    }
  }

  /** @return the center point of the snake */
  def getCenter: Point2d = {
    val center = new Point2d(0.0, 0.0)
    var ct = 0
    for (i <- 0 until snakeData.numSegments by 2) {
      ct += 1
      center.add(segment(i).getCenterParticle)
    }
    center.scale(1.0 / ct.toDouble)
    center
  }

  /** shift/translate the whole snake by the specified vector */
  def translate(vec: Vector2d): Unit =
    for (i <- 0 until snakeData.numSegments) segment(i).translate(vec)

  /**
    * The snake is not considered stable if the angle between any edge segments exceeds
    * [[com.barrybecker4.simulation.snake.geometry.Segment.MinEdgeAngleDotProduct]] (see segment stability).
    * @return true if the snake has not gotten twisted too badly
    */
  def isStable: Boolean =
    (2 until snakeData.numSegments).forall(i => segment(i).isStable)
}
