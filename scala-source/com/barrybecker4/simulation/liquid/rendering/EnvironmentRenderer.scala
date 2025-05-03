// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

import com.barrybecker4.simulation.liquid.model.environment.MpmEnvironment
import com.barrybecker4.simulation.liquid.model.util.Vec2
import com.barrybecker4.simulation.liquid.model.Particle
import java.awt.geom.Ellipse2D
import java.awt.{Color, Graphics, Graphics2D, RenderingHints}
import javax.swing.JPanel
import EnvironmentRenderer.*


object EnvironmentRenderer {
  private val DEFAULT_WIDTH = 800
  private val DEFAULT_HEIGHT = 800
  private val FAUCET_LIQUID_COLOR = new Color(150, 255, 150)
}

/**
  * Visualization component for the MPM simulation
  * Handles rendering particles and delegates user interaction to MouseInteraction
  */
class EnvironmentRenderer(val environment: MpmEnvironment) extends JPanel {
  
  private val mouseInteraction = new MouseInteraction(this, environment)
  private val options = new RenderingOptions()
  private val statsRenderer = new StatsRenderer(environment)

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    val g2d = g.asInstanceOf[Graphics2D]
  }

  def getRenderingOptions: RenderingOptions = options
  
  def render(g2d: Graphics2D, width: Int, height: Int): Unit = {
    g2d.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    )

    mouseInteraction.setDimensions(width, height)
    drawGridOverlay(g2d, width, height)
    drawWalls(g2d, width, height)

    if (environment.getFaucetRunning) {
      g2d.setColor(FAUCET_LIQUID_COLOR)
      val (faucetX, faucetY) = mouseInteraction.simToScreen(environment.getFaucetPosition)
      val faucetSize = (environment.getFaucetSize * width).toInt
      g2d.fillRect(faucetX - faucetSize / 2, faucetY - faucetSize / 2, faucetSize, faucetSize)
    }

    renderParticles(g2d)

    if (mouseInteraction.isPressed) {
      mouseInteraction.drawInteractor(g2d, width)
    }
    statsRenderer.renderStats(g2d, 10, 20)
  }

  private def drawWalls(g2d: Graphics2D, width: Int, height: Int): Unit = {
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, 0, getWidth, getHeight)

    g2d.setColor(new Color(50, 50, 50))
    val boundary = environment.boundary
    val boundaryWidth = (boundary * width).toInt
    g2d.fillRect(0, 0, boundaryWidth, height)
    g2d.fillRect(width - boundaryWidth, 0, boundaryWidth, height)
    g2d.fillRect(0, height - boundaryWidth, width, boundaryWidth)
    g2d.fillRect(0, 0, width, boundaryWidth)
  }

  private def renderParticles(g2d: Graphics2D): Unit = {
    // Choose rendering method based on particle count for performance
    val particleCount = environment.getParticles.size

    if (particleCount > 5000) {
      // For very large numbers of particles, use more efficient rendering
      renderParticlesFast(g2d)
    } else {
      // For fewer particles, use higher quality rendering
      renderParticlesHighQuality(g2d)
    }
  }

  private def renderParticlesFast(g2d: Graphics2D): Unit = {
    // Set up buffers for batched rendering
    val screenWidth = getWidth
    val screenHeight = getHeight

    for (particle <- environment.getParticles) {
      val (x, y) = mouseInteraction.simToScreen(particle.position)

      // Skip particles outside view
      if (x < 0 || x >= screenWidth || y < 0 || y >= screenHeight)
      {}
      else {
        g2d.setColor(new Color(particle.color))
        g2d.fillRect(x, y, 2, 2) // Smallest possible size for speed
      }
    }
  }

  // Higher quality rendering for particles - uses circles and can add effects
  private def renderParticlesHighQuality(g2d: Graphics2D): Unit = {
    for (particle <- environment.getParticles) {
      val (x, y) = mouseInteraction.simToScreen(particle.position)

      val particleColor = new Color(particle.color)

      // Optional: Adjust color based on particle properties
      val adjustedColor = adjustParticleColor(particleColor, particle)
      g2d.setColor(adjustedColor)

      // Particle size can be constant or based on a property
      val baseSize = 4.0
      val particleSize = baseSize * (1.0 + particle.stability * 2.0)

      // Draw particle as a filled circle
      val circle = new Ellipse2D.Double(
        x - particleSize / 2,
        y - particleSize / 2,
        particleSize,
        particleSize
      )
      g2d.fill(circle)

      // Optional: Draw velocity vectors
      //drawVelocityVector(g2d, particle, x, y)
    }
  }

  private def adjustParticleColor(baseColor: Color, particle: Particle): Color = {
    if (particle.stability > 0.0) {
      // Mix with white based on stability
      val mixFactor = Math.min(particle.stability * 2.0, 0.7)
      val r = (baseColor.getRed + (255 - baseColor.getRed) * mixFactor).toInt
      val g = (baseColor.getGreen + (255 - baseColor.getGreen) * mixFactor).toInt
      val b = (baseColor.getBlue + (255 - baseColor.getBlue) * mixFactor).toInt
      new Color(
        Math.min(255, r),
        Math.min(255, g),
        Math.min(255, b)
      )
    } else {
      baseColor
    }
  }

  private def drawVelocityVector(g2d: Graphics2D, particle: Particle, x: Int, y: Int): Unit = {
    val velocityScale = 10.0 // Scale factor for velocity vector
    val vx = particle.velocity._1 * velocityScale
    val vy = -particle.velocity._2 * velocityScale // Invert Y for screen coordinates

    if (Vec2.length(particle.velocity) > 0.1) {
      g2d.setColor(Color.WHITE)
      g2d.drawLine(x, y, x + vx.toInt, y + vy.toInt)
    }
  }

  private def drawGridOverlay(g2d: Graphics2D, width: Int, height: Int): Unit = {
    g2d.setColor(new Color(80, 80, 80, 100)) // Semi-transparent gray

    val n = environment.n
    val cellSize = Math.min(width, height) / n

    for (i <- 0 to n) { // vertical grid lines
      val x = i * cellSize
      g2d.drawLine(x, 0, x, height)
    }

    for (j <- 0 to n) { // horizontal grid lines
      val y = j * cellSize
      g2d.drawLine(0, y, width, y)
    }
  }

}