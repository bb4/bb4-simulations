// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

/**
  * Use this for Newtonian physics type simulations.
  * @param name the name fo the simulator (eg Snake, Liquid, or Trebuchet)
  * @author Barry Becker
  */
abstract class NewtonianSimulator(name: String) extends Simulator(name) {
  def setShowVelocityVectors(show: Boolean): Unit
  def getShowVelocityVectors: Boolean
  def setShowForceVectors(show: Boolean): Unit
  def getShowForceVectors: Boolean
  def setDrawMesh(use: Boolean): Unit
  def getDrawMesh: Boolean
  def setStaticFriction(staticFriction: Double): Unit
  def getStaticFriction: Double
  def setDynamicFriction(dynamicFriction: Double): Unit
  def getDynamicFriction: Double
}
