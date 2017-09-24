// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.rendering

import com.barrybecker4.simulation.snake.geometry.Particle
import com.barrybecker4.simulation.snake.geometry.Segment
import java.awt._


object SegmentRenderer {
  private val FORCE_COLOR = new Color(230, 0, 20, 100)
  private val FRICTIONAL_FORCE_COLOR = new Color(50, 10, 0, 70)
  private val VELOCITY_COLOR = new Color(80, 100, 250, 100)
  private val VECTOR_SIZE = 130.0
  private val VECTOR_STROKE = new BasicStroke(1)
}

/**
  * Render a segment of a snakes body. It is composed of edges.
  * @author Barry Becker
  */
class SegmentRenderer private[rendering](val renderParams: RenderingParameters) {
  //private val renderParams = new RenderingParameters
  private val edgeRenderer = new EdgeRenderer(renderParams)
  private var g: Graphics2D = _

  /** @param segment the segment to render. */
  def render(segment: Segment, g: Graphics2D): Unit = {
    this.g = g
    val edges = segment.edges
    val particles = segment.particles
    if (renderParams.drawMesh) {
      for (i <- edges.indices)
        if (i != 3) edgeRenderer.render(edges(i), g)
    }
    else {
      edgeRenderer.render(edges(0), g)
      edgeRenderer.render(edges(2), g)
    }
    if (segment.isHead) edgeRenderer.render(edges(1), g)
    if (segment.isTail) edgeRenderer.render(edges(3), g)
    // draw the force and velocity vectors acting on each particle
    if (renderParams.showForceVectors) renderForceVectors(particles)
    if (renderParams.showVelocityVectors) renderVelocityVectors(particles)
  }

  private def renderForceVectors(particles: Array[Particle]): Unit = {
    g.setStroke(SegmentRenderer.VECTOR_STROKE)
    g.setColor(SegmentRenderer.FORCE_COLOR)
    val scale = renderParams.scale
    for (particle <- particles) {
      val x = (scale * particle.x).toInt
      val y = (scale * particle.y).toInt
      g.drawLine(x, y,
        (x + SegmentRenderer.VECTOR_SIZE * particle.force.x).toInt,
        (y + SegmentRenderer.VECTOR_SIZE * particle.force.y).toInt)
    }
    g.setColor(SegmentRenderer.FRICTIONAL_FORCE_COLOR)
    for (particle <- particles) {
      g.drawLine((scale * particle.x).toInt,
        (scale * particle.y).toInt,
        (scale * particle.x + SegmentRenderer.VECTOR_SIZE * particle.frictionalForce.x).toInt,
        (scale * particle.y + SegmentRenderer.VECTOR_SIZE * particle.frictionalForce.y).toInt)
    }
  }

  private def renderVelocityVectors(particles: Array[Particle]): Unit = {
    g.setStroke(SegmentRenderer.VECTOR_STROKE)
    g.setColor(SegmentRenderer.VELOCITY_COLOR)
    val scale = renderParams.scale
    for (particle <- particles) {
      g.drawLine((scale * particle.x).toInt,
        (scale * particle.y).toInt,
        (scale * particle.x + SegmentRenderer.VECTOR_SIZE * particle.velocity.x).toInt,
        (scale * particle.y + SegmentRenderer.VECTOR_SIZE * particle.velocity.y).toInt)
    }
  }
}