// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import javax.vecmath.Vector2d


object Edge {
  /** Defaults used when spring constants are not supplied (e.g. tests). */
  val DefaultSpringK: Double = 0.8
  val DefaultSpringDamping: Double = 1.2
  /** Below this length, direction is undefined; return zero force / skip normalize. */
  private val MinLengthForForce: Double = 1e-12
}

/**
  * Spring edge between two particles (also used as a muscle when contracted).
  * @param firstParticle particle that anchors one end of the edge
  * @param secondParticle particle that anchors the other end of the edge
  */
class Edge private[geometry](val firstParticle: Particle, val secondParticle: Particle) {

  /** Scratch vectors to avoid per-call allocation in [[getForce]] / [[getDirection]]. */
  private val direction = new Vector2d
  private val force = new Vector2d
  private val dampingVec = new Vector2d

  /** the resting magnitude of the spring  */
  var restingLength: Double = firstParticle.distance(secondParticle)
  /** usually the effectiveLength is the same as restingLength except when muscular contraction are happening  */
  var effectiveLength: Double = restingLength
  /** the current magnitude of the spring */
  var length: Double = restingLength


  /**
    * This method simulates the contraction or expansion of a muscle
    * the rest magnitude restingLength is effectively changed by the contraction factor.
    * @param contraction the amount that the spring model for the edge is contracting
    */
  private[geometry] def setContraction(contraction: Double): Unit = {
    if (contraction <= 0) {
      throw new IllegalArgumentException("Error contraction <=0 = " + contraction)
    }
    effectiveLength = contraction * restingLength
  }

  /**
    * The force that the spring edge exerts is k times the vector (L-l)p2-p1
    * where L is the resting magnitude of the edge and l is the current magnitude
    * The official formula in proceedings of Siggraph 1988 p169 is
    * k(L-l) - D* dl/dt
    * @return the computed force exerted on the particle.
    */
  def getForce(springK: Double, springDamping: Double): Vector2d = {
    force.set(secondParticle)
    force.sub(firstParticle)
    length = force.length
    if (length < Edge.MinLengthForForce) {
      force.set(0.0, 0.0)
      return force
    }
    direction.set(force)
    direction.normalize()
    // adjust the force by the damping term
    dampingVec.set(secondParticle.velocity)
    dampingVec.sub(firstParticle.velocity)
    val halfEffectiveL = effectiveLength / 2.0
    val damp = springDamping * dampingVec.dot(direction)
    // never let the force get too great or too small
    if (length > 2.0 * effectiveLength)
      force.scale(-springK * (effectiveLength - length) * (effectiveLength - length) / effectiveLength - damp)
    else if (length < halfEffectiveL) { // prevent the springs from getting too compressed
      val lengthDiff = restingLength - length
      force.scale(springK * (lengthDiff + 100000.0 * (halfEffectiveL - length)) / halfEffectiveL - damp)
    }
    else {
      force.scale(springK * (effectiveLength - length) - damp)
    }
    force
  }

  /** A unit vector in the direction p2-p1; zero vector if the edge has collapsed. */
  def getDirection: Vector2d = {
    direction.set(secondParticle)
    direction.sub(firstParticle)
    if (direction.length < Edge.MinLengthForForce) direction.set(0.0, 0.0)
    else direction.normalize()
    direction
  }

  /**
    * find the result of taking the dot product of this edge with another
    * @param edge to dot this edge with
    * @return the dot product
    */
  def dot(edge: Edge): Double = getDirection.dot(edge.getDirection)
}
