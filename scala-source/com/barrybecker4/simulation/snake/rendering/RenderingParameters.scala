// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.rendering


object RenderingParameters {
  /** scales the size of the snakes geometry */
  val SCALE = 0.9
}

/**
  * Tweakable rendering parameters.
  * @author Barry Becker
  */
class RenderingParameters(
   var showVelocityVectors: Boolean = false,
   var showForceVectors: Boolean = false,
   var drawMesh: Boolean = false,
   var scale: Double = RenderingParameters.SCALE)
