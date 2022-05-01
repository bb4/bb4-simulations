// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.trebuchet.model.parts.Projectile
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.{HEIGHT, SCALE_FACTOR}
import com.barrybecker4.simulation.trebuchet.rendering.ProjectileRenderer.{BORDER_COLOR, FILL_COLOR, TRAIL_COLOR, TRAIL_STROKE}
import com.barrybecker4.simulation.trebuchet.rendering.RenderingConstants.*

import java.awt.{BasicStroke, Color, Graphics2D}
import javax.vecmath.Vector2d

object ProjectileRenderer {
  private val LEVER_STROKE = new BasicStroke(10.0f)
  private val BORDER_COLOR = new Color(140, 50, 110)
  private val FILL_COLOR = new Color(80, 150, 10)
  private val TRAIL_COLOR = new Color(100, 0, 0, 50)
  private val TRAIL_STROKE = new BasicStroke(2.0f)
}

class ProjectileRenderer(projectile: Projectile)  extends AbstractPartRenderer {

  private var pastPositions = Seq[Vector2d]()

  override def render(g2: Graphics2D, scale: Double, viewHeight: Int): Unit = {

    val position = projectile.getPosition
    val location = getOvalLocation(position, viewHeight, scale)

    val radius = (SCALE_FACTOR * projectile.getRadius).toInt
    val diameter = (scale * 2.0 * radius).toInt
    g2.setColor(BORDER_COLOR)

    g2.drawOval(location.getX, location.getY, diameter, diameter)
    g2.setColor(FILL_COLOR)
    g2.fillOval(location.getX, location.getY, diameter, diameter)

    if (projectile.isReleased) {
      // show a little larger once released
      val d = (diameter + scale * 4.0).toInt
      g2.drawOval(location.getX, location.getY, d, d)

      pastPositions :+= position
      drawTrail(g2, diameter, viewHeight, scale)
    }

    val y = viewHeight - projectile.getBaseY
    if (showVelocityVectors) {
      g2.setStroke(VELOCITY_VECTOR_STROKE)
      g2.setColor(VELOCITY_VECTOR_COLOR)
      def vel = projectile.getVelocity
      g2.drawLine((scale * position.x).toInt, (y + scale * position.y).toInt,
        (scale * (position.x + vel.x)).toInt, (y + scale * (position.y + vel.y)).toInt)
    }
    if (showForceVectors) {
      g2.setStroke(FORCE_VECTOR_STROKE)
      g2.setColor(FORCE_VECTOR_COLOR)
      def force = projectile.getForce
      g2.drawLine((scale * position.x).toInt, (y + scale * position.y).toInt,
        (scale * (position.x + force.x)).toInt, (y + scale * (position.y + force.y)).toInt)
    }
  }

  private def drawTrail(g2: Graphics2D, diameter: Int, height: Int, scale: Double): Unit = {
    g2.setColor(TRAIL_COLOR)
    g2.setStroke(TRAIL_STROKE)
    if (pastPositions.nonEmpty) {
      var lastLoc = getOvalLocation(pastPositions.head, height, scale)
      for (pos <- pastPositions.tail) {
        val newLoc = getOvalLocation(pos, height, scale)
        g2.drawLine(lastLoc.getX, lastLoc.getY, newLoc.getX, newLoc.getY)
        lastLoc = newLoc
      }
    }
  }

  private def getOvalLocation(position: Vector2d, height: Int, scale: Double): IntLocation = {
    val y = height - projectile.getBaseY
    val rad = projectile.getRadius
    IntLocation((scale * (position.y - rad) + y).toInt, (scale * (position.x - rad)).toInt)
  }
}
