// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import javax.vecmath.Vector2d
import java.awt._
import java.lang.Math.PI
import java.lang.Math.atan
import RenderablePart._


/**
  * @author Barry Becker
  */
object Sling {
  private val STROKE = new BasicStroke(2.0f)
  private val COLOR = new Color(0, 30, 0)
  private val ARC_COLOR = new Color(60, 90, 70, 150)
}

class Sling(var length: Double, var releaseAngle: Double, var lever: Lever, var projectile: Projectile)
  extends RenderablePart {

  val attachPt: Vector2d = getHookPosition
  projectile.setX(attachPt.x + SCALE_FACTOR * length)
  projectile.setY(attachPt.y - SCALE_FACTOR * projectile.getRadius)

  def getLength: Double = length

  def setLength(length: Double): Unit = {
    this.length = length
  }

  def getReleaseAngle: Double = releaseAngle

  def setReleaseAngle(releaseAngle: Double): Unit = {
    this.releaseAngle = releaseAngle
  }

  def getHookPosition: Vector2d = {
    val leverLength = lever.getSlingLeverLength
    val cos = SCALE_FACTOR * leverLength * Math.cos(angle)
    val sin = SCALE_FACTOR * leverLength * Math.sin(angle)
    val attachPt = new Vector2d(STRUT_BASE_X - sin, (-SCALE_FACTOR * height).toInt + cos)
    attachPt
  }

  def getProjectileAttachPoint: Vector2d = {
    val attachPt = getHookPosition
    val dir = new Vector2d(projectile.getX - attachPt.x, projectile.getY - attachPt.y)
    dir.normalize()
    dir.scale(SCALE_FACTOR * length)
    attachPt.add(dir)
    attachPt
  }

  /**
    * the sling angle is the bottom angle bwetween the lever and the sling.
    * @return the angle of the sling with the lever.
    */
  def getAngleWithLever: Double = {
    val leverAngleWithHorz = PI / 2.0 - angle
    val slingAngleWithHorz = getAngleWithHorz
    //System.out.println("slingAngle = leverAngleWithHorz("+leverAngleWithHorz+") "
    // + "  slingAngleWithHorz("+ slingAngleWithHorz+") =  "+(leverAngleWithHorz + slingAngleWithHorz));
    leverAngleWithHorz - slingAngleWithHorz
  }

  def getAngleWithHorz: Double = {
    val hookPos = getHookPosition
    val deltaY = projectile.getY - hookPos.y
    val deltaX = projectile.getX - hookPos.x
    var theAngle = atan(deltaY / deltaX)
    //System.out.println("angle=  "+angle);
    if (deltaX < 0 || angle > PI / 2) theAngle += PI
    -theAngle
  }

  override def render(g2: Graphics2D, scale: Double): Unit = {
    g2.setStroke(Sling.STROKE)
    g2.setColor(Sling.COLOR)
    val attachPt = getHookPosition
    val projectileAttachPt = getProjectileAttachPoint
    g2.drawLine((scale * attachPt.x).toInt, (BASE_Y + scale * attachPt.y).toInt,
      (scale * projectileAttachPt.x).toInt, (BASE_Y + scale * projectileAttachPt.y).toInt)
    val startAngle = (getAngleWithHorz * 180.0 / PI).toInt
    val angle = (getAngleWithLever * 180.0 / PI).toInt
    val endAngle = startAngle + angle
    val diameter = (SCALE_FACTOR * 2 * length).toInt
    val rad = diameter >> 1
    g2.setColor(Sling.ARC_COLOR)
    g2.drawArc((scale * (attachPt.x - rad)).toInt, (BASE_Y + scale * (attachPt.y - rad)).toInt,
      (scale * diameter).toInt, (scale * diameter).toInt, startAngle, angle)
    //g2.drawString("start = "+ startAngle, (int)(attachPt.x + rad), (int)(attachPt.y));
    //g2.drawString("end   = "+ endAngle, (int)(attachPt.x + rad), (int)(attachPt.y + 15));
    g2.drawString("angle = " + angle, (scale * (attachPt.x + rad)).toInt, (BASE_Y + scale * (attachPt.y + 30)).toInt)
  }
}
