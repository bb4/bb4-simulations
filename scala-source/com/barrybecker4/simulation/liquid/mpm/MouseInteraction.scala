// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm

import com.barrybecker4.simulation.liquid.mpm.util.Vec2
import java.awt.event.{MouseAdapter, MouseEvent, MouseMotionAdapter}
import javax.swing.JPanel

/**
  * Handles user interaction with the MPM simulation
  * Separates interaction logic from rendering
  */
class MouseInteraction(
  val panel: JPanel,
  val simulation: MpmSimulation,
  val windowWidth: Int,
  val windowHeight: Int
) {
  private var mouseX = 0
  private var mouseY = 0
  private var isMousePressed = false
  private var lastMousePos: Vec2.Vec2 = (0.0, 0.0)
  private val forceRadius = 0.05 // Radius of force application

  initializeMouseListeners()

  def isPressed: Boolean = isMousePressed
  def getMouseX: Int = mouseX
  def getMouseY: Int = mouseY
  def getForceRadius: Double = forceRadius

  // Set up mouse listeners for user interaction
  private def initializeMouseListeners(): Unit = {
    panel.addMouseListener(new MouseAdapter {
      override def mousePressed(e: MouseEvent): Unit = {
        isMousePressed = true
        mouseX = e.getX
        mouseY = e.getY
        lastMousePos = screenToSim(mouseX, mouseY)
      }

      override def mouseReleased(e: MouseEvent): Unit = {
        isMousePressed = false
      }
    })

    panel.addMouseMotionListener(new MouseMotionAdapter {
      override def mouseMoved(e: MouseEvent): Unit = {
        mouseX = e.getX
        mouseY = e.getY
      }

      override def mouseDragged(e: MouseEvent): Unit = {
        val newX = e.getX
        val newY = e.getY

        if (isMousePressed) {
          val currentPos = screenToSim(newX, newY)
          val force = Vec2.sub(currentPos, lastMousePos)

          // Scale the force based on mouse movement
          val scaledForce = Vec2.scale(force, 20.0)

          // Apply force at mouse position
          simulation.applyForce(currentPos, scaledForce, forceRadius)

          // Update for next frame
          mouseX = newX
          mouseY = newY
          lastMousePos = currentPos
        }
      }
    })
  }

  // Convert screen coordinates to simulation coordinates
  def screenToSim(x: Int, y: Int): Vec2.Vec2 = {
    val simX = x.toDouble / windowWidth
    val simY = 1.0 - (y.toDouble / windowHeight) // Invert Y for screen coordinates
    (simX, simY)
  }

  // Convert simulation coordinates to screen coordinates
  def simToScreen(pos: Vec2.Vec2): (Int, Int) = {
    val screenX = (pos._1 * windowWidth).toInt
    val screenY = ((1.0 - pos._2) * windowHeight).toInt // Invert Y for screen coordinates
    (screenX, screenY)
  }
}