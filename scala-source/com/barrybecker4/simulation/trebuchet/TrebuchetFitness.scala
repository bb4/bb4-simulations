// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.simulation.trebuchet.model.{RenderablePart, Trebuchet}

/**
  * One headless throw.
  *
  * @param fitness  negative horizontal distance when the projectile is released, otherwise [[TrebuchetFitness.UnstablePenalty]]
  * @param range    horizontal distance from where the projectile started. Zero when the throw does not count.
  * @param released true when the sling let go and the integration stayed finite
  * @param steps    physics steps taken
  */
final case class TrebuchetShot(fitness: Double, range: Double, released: Boolean, steps: Int)

/**
  * Scores a trebuchet design by how far it throws. Lower fitness is better.
  * The search steps the physics directly: no window and no sleep.
  */
object TrebuchetFitness {

  /** Same step the animation uses, in seconds. */
  val TimeStep = 0.002

  /** Long enough for a throw to land. A design that has not released by then is a miss. */
  val MaxSteps = 20000

  /** Worse than any real throw. Matches the idea of the snake's unstable penalty. */
  val UnstablePenalty = 100000.0

  /** Ground in model coordinates. The projectile starts above this (negative y) and falls toward it. */
  private val GroundY = 0.0

  /** A coordinate past this means the integration ran away. */
  private val BlowUp = 1.0e6

  /**
    * @param x horizontal distance traveled, not the raw coordinate
    * @param y height at the end of the throw
    */
  def score(released: Boolean, x: Double, y: Double): Double =
    if (!released || !x.isFinite || !y.isFinite) UnstablePenalty
    else -x

  def shoot(trebuchet: Trebuchet, params: ParameterArray): TrebuchetShot = {
    TrebuchetParameters.apply(trebuchet, params)
    if (!RenderablePart.angle.isFinite)
      return TrebuchetShot(UnstablePenalty, 0.0, released = false, steps = 0)

    val startX = trebuchet.projectileX
    var steps = 0
    while (steps < MaxSteps) {
      trebuchet.stepForward(TimeStep)
      steps += 1
      val x = trebuchet.projectileX
      val y = trebuchet.projectileY
      if (blewUp(x, y))
        return TrebuchetShot(UnstablePenalty, 0.0, released = false, steps = steps)
      val released = trebuchet.isProjectileReleased
      if (released && y >= GroundY)
        return landed(x - startX, y, steps)
    }
    if (!trebuchet.isProjectileReleased)
      TrebuchetShot(UnstablePenalty, 0.0, released = false, steps = steps)
    else {
      val range = trebuchet.projectileX - startX
      TrebuchetShot(score(released = true, range, trebuchet.projectileY), range, released = true, steps = steps)
    }
  }

  /** Text shown beside the Optimize button. */
  def statusText(fitness: Double): String =
    if (!fitness.isFinite || fitness >= UnstablePenalty) f"no throw (fitness $fitness%.0f)"
    else f"fitness $fitness%.2f, range ${-fitness}%.2f"

  private def landed(range: Double, y: Double, steps: Int): TrebuchetShot =
    TrebuchetShot(score(released = true, range, y), range, released = true, steps = steps)

  private def blewUp(x: Double, y: Double): Boolean =
    !x.isFinite || !y.isFinite || math.abs(x) > BlowUp || math.abs(y) > BlowUp
}
