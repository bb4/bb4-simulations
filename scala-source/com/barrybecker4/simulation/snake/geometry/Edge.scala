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
  * @param p1 particle that anchors one end of the
  * @param p2 particle that anchors the other end of the edge
  */
class Edge private[geometry](val p1: Particle, val p2: Particle) {

  // the 2 endpoints defining the edge endpoints
  private var firstParticle: Particle = _
  private var secondParticle: Particle = _
  private var segment: Line2D.Double = _
  /** the spring constant K (large K = stiffer) */
  private var k = .0
  /** damping constant  */
  private var damping = .0
  /** the resting magnitude of the spring  */
  private var restingLength = .0
  /** usually the effectiveLength is the same as restingLength except when muscular contraction are happening  */
  private var effectiveLength = .0
  /** the current magnitude of the spring */
  private var length = .0
  /** these act like temporary variables for some calculations avoiding many object constructions */
  final private val direction = new Vector2d
  final private val force = new Vector2d
  final private val dampingVec = new Vector2d
  commonInit(p1, p2, Edge.K, Edge.D)

  def getFirstParticle: Particle = firstParticle
  def getSecondParticle: Particle = secondParticle
  def getRestingLength: Double = restingLength
  def getLength: Double = length

  private def commonInit(p1: Particle, p2: Particle, k: Double, d: Double): Unit = {
    segment = new Line2D.Double(p1.x, p1.y, p2.x, p2.y)
    firstParticle = p1
    secondParticle = p2
    this.k = k
    damping = d
    restingLength = firstParticle.distance(secondParticle)
    effectiveLength = restingLength
    length = restingLength // current magnitude
  }

  /**
    * This method simulates the contraction or expansion of a muscle
    * the rest magnitude restingLength is effectively changed by the contraction factor.
    *
    * @param contraction the amount that the spring model for the edge is contracting
    */
  private[geometry] def setContraction(contraction: Double): Unit = {
    if (contraction <= 0) {
      throw new IllegalArgumentException("Error contraction <=0 = " + contraction)
      //contraction = EPS;
    }
    effectiveLength = contraction * restingLength
  }

  /**
    * The force that the spring edge exerts is k times the vector (L-l)p2-p1
    * where L is the resting magnitude of the edge and l is the current magnitude
    * The official formula in proceedings of Siggraph 1988 p169 is
    * k(L-l) - D* dl/dt
    *
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
      force.scale(-(k) * (effectiveLength - length) * (effectiveLength - length) / effectiveLength - damp)
    else if (length < halfEffectiveL) { // prevent the springs from getting too compressed
      val lengthDiff = restingLength - length
      force.scale(k * (lengthDiff + 100000.0 * (halfEffectiveL - length)) / halfEffectiveL - damp)
    }
    else { //if (d>1.0)
      //   System.out.println("f="+k*(effectiveLength-length)+" - d="+d);
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
