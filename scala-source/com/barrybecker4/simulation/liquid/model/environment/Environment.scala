// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model.environment

import com.barrybecker4.simulation.liquid.model.util.Vec2
import com.barrybecker4.simulation.liquid.model.{Particle, UiParameter}

trait Environment {
  
  def reset(): Unit
  def advance(): Double
  
  def getUiParameters(): List[UiParameter]

  def getWidth: Int
  def getHeight: Int
  def getParticles: List[Particle]
  
  def getIter: Int
  def getFaucetRunning: Boolean
  def getFaucetPosition: Vec2.Vec2
  def getFaucetVelocity: Vec2.Vec2
  def getFaucetSize: Double

  def applyForce(center: Vec2.Vec2, forceVector: Vec2.Vec2, radius: Double): Unit
}
