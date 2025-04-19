// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.liquid.mpm

import com.barrybecker4.simulation.liquid.mpm.util.{Mat2, Vec2}

case class Particle(
  var position: Vec2.Vec2,
  color: Int
) {
  var velocity: Vec2.Vec2 = (0.0, 0.0)
  var F: Mat2.Mat2 = (1.0, 0.0, 0.0, 1.0)  // Deformation gradient
  var Cauchy: Mat2.Mat2 = (0.0, 0.0, 0.0, 0.0) // Cauchy tensor
  var Jp: Double = 1.0                     // Jacobian determinant
  var stability: Double = 0.0              // 0 is stable, closer to 1 is unstable
  var externalForce: Option[Vec2.Vec2] = None // External force if any
}