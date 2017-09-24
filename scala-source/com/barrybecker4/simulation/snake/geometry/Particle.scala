// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import javax.vecmath.Point2d
import javax.vecmath.Vector2d


/**
  * Particle is a point mass that approximates the mass of the snake.
  * It is also a vertex composing the geometry. Assumes that the initial velocity is 0.
  * No getter/setter because we need speed.
  * @author Barry Becker
  */
class Particle(x: Double, y: Double, val m: Double) extends Point2d(x, y) {

  /** the velocity vector of the particle in m/s */
  var velocity = new Vector2d(0.0, 0.0)

  /** the acceleration vector of the particle in m/s&#94;2 */
  private[geometry] var acceleration = new Vector2d(0.0, 0.0)

  /** the force vector (sum of all forces acting on the particle) */
  var force = new Vector2d(0.0, 0.0)

  /** the frictional force if used */
  var frictionalForce = new Vector2d(0.0, 0.0)

  /** the mass of the particle in kg */
  var mass: Double = m
}
