// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import javax.vecmath.Vector2d
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D

/**
  * @author Barry Becker
  */
object Edge {
  /** constants related the the spring for this edge segment  */
  private val K = 0.8 // default  .6

  /** the damping coefficient */
  private val D = 1.2 // default
}

/**
  * Constructor - assumes defaults for the spring constant and damping
  * @param firstParticle particle that anchors one end of the
  * @param secondParticle particle that anchors the other end of the edge
  */
class Edge private[geometry](val firstParticle: Particle, val secondParticle: Particle) {

  private var segment = new Line2D.Double(firstParticle.x, firstParticle.y, secondParticle.x, secondParticle.y)
  /** the spring constant K (large K = stiffer) */
  private var k = Edge.K
  /** damping constant  */
  private var damping = Edge.D

  /** these act like temporary variables for some calculations avoiding many object constructions */
  private val direction = new Vector2d
  private val force = new Vector2d
  private val dampingVec = new Vector2d

  /** the resting magnitude of the spring  */
  var restingLength: Double = firstParticle.distance(secondParticle)
  /** usually the effectiveLength is the same as restingLength except when muscular contraction are happening  */
  var effectiveLength: Double = restingLength
  /** the current magnitude of the spring */
  var length: Double = restingLength // current magnitude


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
  def getForce: Vector2d = {
    force.set(secondParticle)
    force.sub(firstParticle)
    direction.set(force)
    direction.normalize()
    // adjust the force by the damping term
    dampingVec.set(secondParticle.velocity)
    dampingVec.sub(firstParticle.velocity)
    val halfEffectiveL = effectiveLength / 2.0
    val damp = damping * dampingVec.dot(direction)
    length = force.length
    // never let the force get too great or too small
    if (length > 2.0 * effectiveLength)
      force.scale(-k * (effectiveLength - length) * (effectiveLength - length) / effectiveLength - damp)
    else if (length < halfEffectiveL) { // prevent the springs from getting too compressed
      val lengthDiff = restingLength - length
      force.scale(k * (lengthDiff + 100000.0 * (halfEffectiveL - length)) / halfEffectiveL - damp)
    }
    else { //if (d>1.0)
      //   println("f="+k*(effectiveLength-length)+" - d="+d);
      force.scale(k * (effectiveLength - length) - damp)
    }
    force
  }

  /** A unit vector in the direction p2-p1 */
  def getDirection: Vector2d = {
    direction.set(secondParticle)
    direction.sub(firstParticle)
    direction.normalize()
    direction
  }

  def intersects(rect: Rectangle2D.Double): Boolean = segment.intersects(rect)

  /**
    * find the result of taking the dot product of this edge iwth another
    * @param edge to dot this edge with
    * @return the dot product
    */
  def dot(edge: Edge): Double = getDirection.dot(edge.getDirection)
}
