// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

import com.barrybecker4.simulation.liquid.model.environment.{Environment, MpmEnvironment}
import com.barrybecker4.simulation.liquid.model.util.Vec2

import java.awt.{Color, Graphics2D}
import java.awt.event.{MouseAdapter, MouseEvent, MouseMotionAdapter}
import javax.swing.JPanel
import MouseInteraction.*


object MouseInteraction {
  private val INTERACTOR_GLYPH_COLOR = new Color(255, 255, 255, 100) // Translucent white
}
/**
  * Handles user interaction with the MPM simulation
  * Separates interaction logic from rendering
  */
class MouseInteraction(val panel: JPanel, val environment: MpmEnvironment) {

  private var width: Int = 500
  private var height: Int = 500
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

  def setDimensions(width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
  }

  def drawInteractor(g2d: Graphics2D, width: Int): Unit = {
    g2d.setColor(INTERACTOR_GLYPH_COLOR)
    val radiusInPixels = (getForceRadius * width).toInt
    g2d.drawOval(
      getMouseX - radiusInPixels,
      getMouseY - radiusInPixels,
      radiusInPixels * 2,
      radiusInPixels * 2
    )
  }

  // Set up mouse listeners for user interaction
  private def initializeMouseListeners(): Unit = {

    panel.addMouseListener(new MouseAdapter {
      override def mousePressed(e: MouseEvent): Unit = {
        val mousePos = screenToSim(mouseX, mouseY)
        if (e.getButton == MouseEvent.BUTTON1) {  // Left mouse button
          isMousePressed = true
          mouseX = e.getX
          mouseY = e.getY
          lastMousePos = mousePos
        }
        else if (e.getButton == MouseEvent.BUTTON3) {  // Right mouse button
          if (environment.faucetRunning) {
            environment.stopFaucet()
          } else {
            environment.startFaucet(mousePos, (1.0, 0.0), 0.05)
          }
        }
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
          environment.applyForce(currentPos, scaledForce, forceRadius)

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
    val simX = x.toDouble / width
    val simY = 1.0 - (y.toDouble / height) // Invert Y for screen coordinates
    (simX, simY)
  }

  // Convert simulation coordinates to screen coordinates
  def simToScreen(pos: Vec2.Vec2): (Int, Int) = {
    val screenX = (pos._1 * width).toInt
    val screenY = ((1.0 - pos._2) * height).toInt // Invert Y for screen coordinates
    (screenX, screenY)
  }
}