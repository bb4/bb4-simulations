/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

/**
  * Display-only parametric equation strings for the UI labels.
  * Numeric drawing uses [[SpirographMath]], not these strings.
  *
  * @param rollingRadius secondary/rolling circle radius (R2 from the sliders)
  * @param combinedRad   main + rolling radius (R1 + R2)
  * @param position      pen offset along the rolling-circle spoke
  */
object ParametricEquations {

  def apply(rollingRadius: Int, combinedRad: Int, position: Int): ParametricEquations = {
    val (xEq, yEq) = computeStrings(rollingRadius, combinedRad, position)
    new ParametricEquations(xEq, yEq)
  }

  private def computeStrings(radius: Int, combinedRad: Int, position: Int): (String, String) =
    if (radius == 0) ("x(t)=undefined", "y(t)=undefined")
    else if (position == 0) {
      val x = s"x(t)=${combinedRad}cos(t)"
      val y = s"y(t)=${combinedRad}sin(t)"
      (x, y)
    } else {
      var sign1 = "-"
      var sign2 = "-"
      var actualRadius = radius
      var actualPosition = position
      if (position < 0 && radius < 0) {
        actualPosition *= -1
        actualRadius *= -1
        sign1 = "+"
      } else if (position < 0 && radius > 0) {
        actualPosition *= -1
        sign1 = "+"
        sign2 = "+"
      } else if (position > 0 && radius < 0) {
        actualRadius *= -1
        sign2 = "+"
      }
      val x =
        s"x(t)=${combinedRad}cos(t)$sign1${actualPosition}cos(${combinedRad}t / $actualRadius)"
      val y =
        s"y(t)=${combinedRad}sin(t)$sign2${actualPosition}sin(${combinedRad}t / $actualRadius)"
      (x, y)
    }
}

final class ParametricEquations private[model] (
  val xEquation: String,
  val yEquation: String,
)
