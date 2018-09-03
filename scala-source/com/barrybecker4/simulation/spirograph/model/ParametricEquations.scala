/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

/**
  * Represents 2 parametric equations for defining the epicycloid curve (IOW, a spirograph curve).
  * @param radius      radius of main circle
  * @param combinedRad main circle radius plus outer circle radius
  * @param position    position of spoke.
  * @author Barry Becker
  */
case class ParametricEquations(radius: Int, combinedRad: Int, position: Int) {
  var sign1 = "-"
  var sign2 = "-"
  var actualRadius: Int = radius
  var actualPosition: Int = position
  private var xEquation: String = _
  private var yEquation: String = _

  if (radius == 0) {
    xEquation = "x(t)=undefined"
    yEquation = "y(t)=undefined"
  }
  else if (position == 0) {
    xEquation = "x(t)=" + combinedRad + "cos(t)"
    yEquation = "y(t)=" + combinedRad + "sin(t)"
  }
  else {
    if (position < 0 && radius < 0) {
      actualPosition *= -1
      actualRadius *= -1
      sign1 = "+"
    }
    else if (position < 0 && radius > 0) {
      actualPosition *= -1
      sign1 = "+"
      sign2 = "+"
    }
    else if (position > 0 && radius < 0) {
      actualRadius *= -1
      sign2 = "+"
    }
    xEquation = "x(t)=" + combinedRad + "cos(t)" + sign1 + actualPosition + "cos(" + combinedRad + "t / " + actualRadius + ')'
    yEquation = "y(t)=" + combinedRad + "sin(t)" + sign2 + actualPosition + "sin(" + combinedRad + "t / " + actualRadius + ')'
  }

  def getXEquation: String = xEquation
  def getYEquation: String = yEquation
}