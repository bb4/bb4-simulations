// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import com.barrybecker4.simulation.fluid.rendering.EnvironmentRenderer
import java.awt.event.MouseEvent
import javax.swing.event.MouseInputAdapter


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * @author Barry Becker
  */
object InteractionHandler {
  private[ui] val DEFAULT_FORCE = 2.0
  private[ui] val DEFAULT_SOURCE_DENSITY = 1.0

  def dragStencilIndices(i: Int, j: Int, width: Int, height: Int): IndexedSeq[(Int, Int)] = {
    val startX = math.max(1, i - 1)
    val stopX  = math.min(width, i + 1)
    val startY = math.max(1, j - 1)
    val stopY  = math.min(height, j + 1)
    for (ii <- startX to stopX; jj <- startY to stopY) yield (ii, jj)
  }
}

class InteractionHandler private[ui](
    var env: FluidEnvironment,
    var scale: Double,
    var force: Double = InteractionHandler.DEFAULT_FORCE,
    var renderContentOffsetPixels: Int = EnvironmentRenderer.ContentOffsetPixels
) extends MouseInputAdapter {

  private var sourceDensity: Double = InteractionHandler.DEFAULT_SOURCE_DENSITY
  private var currentX = 0
  private var currentY = 0
  private var lastX = 0
  private var lastY = 0
  private var mouse1Down = false
  private var mouse3Down = false

  def setForce(force: Double): Unit = { this.force = force }
  def setScale(scale: Double): Unit = { this.scale = scale }
  def setEnvironment(env: FluidEnvironment): Unit = { this.env = env }
  private[ui] def setSourceDensity(sourceDensity: Double): Unit = { this.sourceDensity = sourceDensity }

  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    val ax = currentX - renderContentOffsetPixels
    val ay = currentY - renderContentOffsetPixels
    val i = (ax / scale).toInt
    val j = (ay / scale).toInt
    for ((ii, jj) <- InteractionHandler.dragStencilIndices(i, j, env.getWidth, env.getHeight)) {
      val weight = if (ii == i && jj == j) 1.0 else 0.3
      applyChange(ii, jj, weight)
    }
    lastX = currentX
    lastY = currentY
  }

  /** Make waves or adds ink depending on which mouse key is being held down. */
  private def applyChange(i: Int, j: Int, weight: Double): Unit = {
    if (mouse1Down) {
      val fu = weight * force * (currentX - lastX) / scale
      val fv = weight * force * (currentY - lastY) / scale
      env.incrementU(i, j, fu)
      env.incrementV(i, j, fv)
    }
    else if (mouse3Down) {
      env.incrementDensity(i, j, weight * sourceDensity)
    }
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  override def mousePressed(e: MouseEvent): Unit = {
    mouse1Down = e.getButton == MouseEvent.BUTTON1
    mouse3Down = e.getButton == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    if (e.getButton == MouseEvent.BUTTON1) mouse1Down = false
    else if (e.getButton == MouseEvent.BUTTON3) mouse3Down = false
  }
}
