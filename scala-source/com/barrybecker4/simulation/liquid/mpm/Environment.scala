// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm

import com.barrybecker4.simulation.liquid.mpm.util.Vec2

trait Environment {
  def reset(): Unit
  def stepForward(timeStep: Double): Double

  def getWidth: Int
  def getHeight: Int
  def getParticles: List[Particle]

  def getParams: MpmParameters
  def getIter: Int
  def getFaucetRunning: Boolean
  def getFaucetPosition: Vec2.Vec2
  def getFaucetVelocity: Vec2.Vec2
  def getFaucetSize: Double

  def applyForce(center: Vec2.Vec2, forceVector: Vec2.Vec2, radius: Double): Unit
}
