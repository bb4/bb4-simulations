// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.HEIGHT
import com.barrybecker4.simulation.trebuchet.model.parts.{Lever, Projectile, Sling}

import java.awt.*
import java.lang.Math.{PI, atan}
import javax.vecmath.Vector2d


class Sling(val lever: Lever, projectile: Projectile, var length: Double, var releaseAngle: Double) {

  val attachPt: Vector2d = getHookPosition
  projectile.setX(attachPt.x + length)
  projectile.setY(attachPt.y - projectile.getRadius)

  def getHookPosition: Vector2d = {
    val leverLength = lever.getSlingLeverLength
    val cos = leverLength * Math.cos(lever.getAngle)
    val sin =  leverLength * Math.sin(lever.getAngle)
    val attachPt = new Vector2d(lever.getStrutBaseX - sin, -HEIGHT.toInt + cos)
    attachPt
  }

  def getProjectileAttachPoint: Vector2d = {
    val attachPt = getHookPosition
    val dir = new Vector2d(projectile.getX - attachPt.x, projectile.getY - attachPt.y)
    dir.normalize()
    dir.scale(length)
    attachPt.add(dir)
    attachPt
  }

  /**
    * the sling angle is the bottom angle between the lever and the sling.
    * @return the angle of the sling with the lever. Negative.
    */
  def getAngleWithLever: Double = {
    val leverAngleWithHorz = PI / 2.0 - lever.getAngle
    val slingAngleWithHorz = getAngleWithHorz
    //println("slingAngle = leverAngleWithHorz("+leverAngleWithHorz+") "
    // + "  slingAngleWithHorz("+ slingAngleWithHorz+") =  "+ (leverAngleWithHorz + slingAngleWithHorz));
    leverAngleWithHorz - slingAngleWithHorz
  }

  def getAngleWithHorz: Double = {
    val hookPos = getHookPosition
    val deltaY = projectile.getY - hookPos.y
    val deltaX = projectile.getX - hookPos.x
    var theAngle = atan(deltaY / deltaX)
    if (deltaX < 0 || lever.getAngle > PI / 2) theAngle += PI
    -theAngle
  }
}
