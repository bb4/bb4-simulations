/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import GrayScottModel.*
import ConcentrationField.wrap
import java.awt._
import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration._
import scala.compiletime.uninitialized


/**
  * Data structure for the Gray-Scott algorithm.
  * Double-buffered flat concentration fields with fully periodic topology.
  * @author Barry Becker.
  */
object GrayScottModel {
  /** Feed rate */
  val F0: Double =  0.025

  /** Decay rate */
  val K0: Double =  0.079

  val H0: Double = 1.0

  /** Radius for area of effect when doing manual modification with click/drag brush */
  val DEFAULT_BRUSH_RADIUS = 2
  /** The amount of impact that the brush has. */
  val DEFAULT_BRUSH_STRENGTH = 0.3

  /** Periodic boundary conditions (index in `[0, max)`). Prefer [[ConcentrationField.wrap]]. */
  def getPeriodicXValue(x: Int, max: Int): Int = wrap(x, max)
}

/**
  * @param width  width of computational space.
  * @param height height of computational space.
  */
final class GrayScottModel(var width: Int, var height: Int) extends InitializableGrid {

  var initializer: Initializer = Initializer.DEFAULT_INITIALIZER

  /** Display / write buffers (after each step these hold the latest committed state once swapped). */
  private var uField: ConcentrationField = uninitialized
  private var vField: ConcentrationField = uninitialized

  /** Read buffers for the Jacobi step. */
  private[algorithm] var tmpU: ConcentrationField = uninitialized
  private[algorithm] var tmpV: ConcentrationField = uninitialized

  private var k: Double = uninitialized
  private var f: Double = uninitialized
  resetState()

  def getWidth: Int = width
  def getHeight: Int = height

  def u(x: Int, y: Int): Double = uField(x, y)
  def v(x: Int, y: Int): Double = vField(x, y)

  private[algorithm] def uWrite: ConcentrationField = uField
  private[algorithm] def vWrite: ConcentrationField = vField

  /** Updates dimensions and reallocates concentration grids so they stay in sync. Preserves F/K. */
  def setSize(requestedNewSize: Dimension): Unit =
    reallocate(requestedNewSize.width, requestedNewSize.height, resetParams = false)

  def setInitializer(init: Initializer): Unit = {
    initializer = init
    resetState()
  }

  def setF(f: Double): Unit = { this.f = f }
  private[algorithm] def getF = f

  def setK(k: Double): Unit = {this.k = k}
  private[algorithm] def getK = k

  /** Exchange the u, v fields with the tmp versions. */
  private[algorithm] def commitChanges(): Unit = {
    var temp = tmpU
    tmpU = uField
    uField = temp
    temp = tmpV
    tmpV = vField
    vField = temp
  }

  /** Create some initial pattern of chemical that represents the initial condition. Resets F/K. */
  private[algorithm] def resetState(): Unit = reallocate(width, height, resetParams = true)

  /**
    * Allocate new grids, then publish width/height. Publishing size before the
    * arrays exist lets AWT paint index past the old u/v (resize AIOOBE).
    */
  private def reallocate(w: Int, h: Int, resetParams: Boolean): Unit = {
    if (resetParams) {
      f = F0
      k = K0
    }
    val newU = new ConcentrationField(w, h)
    val newV = new ConcentrationField(w, h)
    val newTmpU = new ConcentrationField(w, h)
    val newTmpV = new ConcentrationField(w, h)
    uField = newU
    vField = newV
    tmpU = newTmpU
    tmpV = newTmpV
    width = w
    height = h
    initializer.initialize(this, w, h)
    commitChanges()
  }

  def initializePoint(x: Int, y: Int, initialU: Double, initialV: Double): Unit = {
    if (x >= 0 && x < width && y >= 0 && y < height) {
      tmpU.update(x, y, initialU)
      tmpV.update(x, y, initialV)
      uField.update(x, y, initialU)
      vField.update(x, y, initialV)
    }
  }

  /** Add chemical U at a point in both display and read buffers (for brush). */
  def addU(x: Int, y: Int, amount: Double): Unit = {
    if (x >= 0 && x < width && y >= 0 && y < height) {
      uField.add(x, y, amount)
      tmpU.add(x, y, amount)
    }
  }

  /** Add chemical V at a point in both display and read buffers (for brush). */
  def addV(x: Int, y: Int, amount: Double): Unit = {
    if (x >= 0 && x < width && y >= 0 && y < height) {
      vField.add(x, y, amount)
      tmpV.add(x, y, amount)
    }
  }

  /** Copy all concentration buffers from another same-sized model (tests). */
  private[algorithm] def copyConcentrationsFrom(other: GrayScottModel): Unit = {
    require(width == other.width && height == other.height)
    uField.copyFrom(other.uField)
    vField.copyFrom(other.vField)
    tmpU.copyFrom(other.tmpU)
    tmpV.copyFrom(other.tmpV)
  }
}
