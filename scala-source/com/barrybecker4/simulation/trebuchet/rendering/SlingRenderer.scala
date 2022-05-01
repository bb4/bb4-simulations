// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.trebuchet.model.parts.Sling
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.SCALE_FACTOR
import com.barrybecker4.simulation.trebuchet.rendering.SlingRenderer.{ARC_COLOR, COLOR, STROKE}

import java.awt.{BasicStroke, Color, Graphics2D}
import java.lang.Math.{PI, atan}


object SlingRenderer {
  private val STROKE = new BasicStroke(2.0f)
  private val COLOR = new Color(0, 30, 0)
  private val ARC_COLOR = new Color(60, 90, 70, 150)
}

class SlingRenderer(sling: Sling) extends AbstractPartRenderer {

  override def render(g2: Graphics2D, scale: Double, viewHeight: Int): Unit = {
    g2.setStroke(STROKE)
    g2.setColor(COLOR)

    val y = viewHeight - sling.lever.getBaseY
    val attachPt = sling.getHookPosition
    val projectileAttachPt = sling.getProjectileAttachPoint
    g2.drawLine((scale * attachPt.x).toInt, (y + scale * attachPt.y).toInt,
      (scale * projectileAttachPt.x).toInt, (y + scale * projectileAttachPt.y).toInt)
    val startAngle = (sling.getAngleWithHorz * 180.0 / PI).toInt
    val angle = (sling.getAngleWithLever * 180.0 / PI).toInt
    val endAngle = startAngle + angle
    val diameter = (SCALE_FACTOR * 2 * sling.length).toInt
    val rad = diameter >> 1

    g2.setColor(ARC_COLOR)
    g2.drawArc((scale * (attachPt.x - rad)).toInt, (y + scale * (attachPt.y - rad)).toInt,
      (scale * diameter).toInt, (scale * diameter).toInt, startAngle, angle)
    //g2.drawString("start = "+ startAngle, (int)(attachPt.x + rad), (int)(attachPt.y));
    //g2.drawString("end   = "+ endAngle, (int)(attachPt.x + rad), (int)(attachPt.y + 15));
    g2.drawString("angle = " + angle, (scale * (attachPt.x + rad)).toInt, (y + scale * (attachPt.y + 30)).toInt)
  }
}
