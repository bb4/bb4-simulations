/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

/**
  * Methods called when something about the graph has changed - either the defining parameters or if done rendering.
  * @author Barry Becker
  */
trait GraphStateChangeListener {

  /** Called when one of r1, r2, or position has changed defining the graph shape. */
  def parameterChanged(): Unit

  /** Called when the graph is done rendering. */
  def renderingComplete(): Unit
}