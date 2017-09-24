// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import javax.vecmath.Point2d
import com.barrybecker4.simulation.snake.LocomotionParameters
import com.barrybecker4.simulation.snake.Snake



/**
  * The head/nose segment of the snakes body.
  * @param width1 the width of the segment that is nearest the nose
  * @param width2 the width of the segment nearest the tail
  * @param pos position of the center of the segment
  * @author Barry Becker
  */
class HeadSegment(width1: Double, width2: Double, length: Double, val pos: Point2d, snake: Snake)
  extends Segment(width1, width2, length, pos, snake) {

  halfLength = length / 2.0

  val scale = 1.0 //snake.getRenderingParams().getScale();
  particles(1) = new Particle(pos.x + halfLength, pos.y + scale * width1 / 2.0, particleMass)
  particles(2) = new Particle(pos.x + halfLength, pos.y - scale * width1 / 2.0, particleMass)

  initCommonEdges()
  edges(1) = new Edge(particles(1), particles(2)) // front

  /** There are no muscles to contract in the head since there is no segment in front. */
  override def contractMuscles(params: LocomotionParameters, time: Double) {}
}