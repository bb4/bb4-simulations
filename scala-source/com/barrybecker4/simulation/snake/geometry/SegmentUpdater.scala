// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import com.barrybecker4.simulation.snake.LocomotionParameters
import javax.vecmath.Vector2d
import com.barrybecker4.simulation.snake.geometry.Segment.CENTER_INDEX
import com.barrybecker4.simulation.snake.geometry.Segment.NUM_PARTICLES


object SegmentUpdater {
  private val EPS = 0.00001
}

/**
  * Updates a segment forward in time one timestep
  * @author Barry Becker
  */
class SegmentUpdater() {
  /** temporary vector to aid in calculations (saves creating a lot of new vector objects)  */
  private val velocityVec = new Vector2d(0, 0)
  private val changeVec = new Vector2d(0, 0)

  /**
    * update particle forces
    * look at how much the springs are deflected to determine how much force to apply
    * to each particle. Also include the frictional forces
    */
  def updateForces(segment: Segment): Unit = {
    val edges = segment.edges
    val particles = segment.particles
    val k = segment.snake.locomotionParams.springK
    val d = segment.snake.locomotionParams.springDamping
    val e0Force = edges(0).getForce(k, d)
    val e1Force = edges(1).getForce(k, d)
    val e2Force = edges(2).getForce(k, d)
    val e4Force = edges(4).getForce(k, d)
    val e5Force = edges(5).getForce(k, d)
    val e6Force = edges(6).getForce(k, d)
    val e7Force = edges(7).getForce(k, d)
    applyMainParticleForcesFromEdges(particles, e0Force, e1Force, e2Force, e4Force, e5Force, e6Force, e7Force)
    if (!segment.isHead) applyCouplingFromSegmentInFront(segment, particles)
    if (segment.isTail) applyTailParticleForces(edges, particles, k, d, e0Force, e2Force, e4Force, e7Force)
  }

  private def applyMainParticleForcesFromEdges(
    particles: Array[Particle],
    e0Force: Vector2d,
    e1Force: Vector2d,
    e2Force: Vector2d,
    e4Force: Vector2d,
    e5Force: Vector2d,
    e6Force: Vector2d,
    e7Force: Vector2d
  ): Unit = {
    particles(1).force.set(0, 0)
    particles(1).force.add(e0Force)
    particles(1).force.sub(e5Force)
    particles(1).force.sub(e1Force)
    particles(2).force.set(0, 0)
    particles(2).force.sub(e2Force)
    particles(2).force.sub(e6Force)
    particles(2).force.add(e1Force)
    particles(CENTER_INDEX).force.set(0, 0)
    particles(CENTER_INDEX).force.add(e4Force)
    particles(CENTER_INDEX).force.add(e7Force)
    particles(CENTER_INDEX).force.add(e5Force)
    particles(CENTER_INDEX).force.add(e6Force)
  }

  private def applyCouplingFromSegmentInFront(segment: Segment, particles: Array[Particle]): Unit = {
    val segmentInFront = segment.segmentInFront.get
    particles(1).force.sub(segmentInFront.getRightForce)
    particles(1).force.sub(segmentInFront.getRightBackDiagForce)
    particles(2).force.add(segmentInFront.getLeftForce)
    particles(2).force.sub(segmentInFront.getLeftBackDiagForce)
  }

  private def applyTailParticleForces(
    edges: Array[Edge],
    particles: Array[Particle],
    springK: Double,
    springDamping: Double,
    e0Force: Vector2d,
    e2Force: Vector2d,
    e4Force: Vector2d,
    e7Force: Vector2d
  ): Unit = {
    val e3Force = edges(3).getForce(springK, springDamping)
    particles(0).force.set(0, 0)
    particles(0).force.sub(e3Force)
    particles(0).force.sub(e0Force)
    particles(0).force.sub(e4Force)
    particles(3).force.set(0, 0)
    particles(3).force.add(e3Force)
    particles(3).force.sub(e7Force)
    particles(3).force.add(e2Force)
  }

  /**
    * Update frictional forces acting on particles.
    * The coefficient of static friction should be used until the particle
    * is in motion, then the coefficient of dynamic friction is use to determine the
    * frictional force acting in the direction opposite to the velocity.
    * Static friction acts opposite to the force, while dynamic friction is applied
    * opposite the velocity vector.
    *
    * @param params locomotion params controlled by sliders
    */
  def updateFrictionalForce(segment: Segment, params: LocomotionParameters): Unit = {
    val i = CENTER_INDEX
    val particles = segment.particles
    velocityVec.set(particles(i).force)
    val forceMag = velocityVec.length
    changeVec.set(particles(i).velocity)
    val velMag = changeVec.length
    if (velMag > SegmentUpdater.EPS) {
      applyDynamicFriction(segment, particles, i, params, changeVec)
    } else if (velMag <= SegmentUpdater.EPS && forceMag > SegmentUpdater.EPS) {
      applyStaticFriction(particles, i, params)
    } else {
      zeroCenterForceAndVelocity(particles, i)
    }
    particles(i).frictionalForce.set(changeVec)
  }

  private def applyDynamicFriction(
    segment: Segment,
    particles: Array[Particle],
    i: Int,
    params: LocomotionParameters,
    velDir: Vector2d
  ): Unit = {
    velDir.normalize()
    var frictionalForce = -particles(i).mass * params.dynamicFriction
    changeVec.set(velDir)
    changeVec.scale(frictionalForce)
    val spineDir = segment.getSpinalDirection
    val dot = spineDir.dot(changeVec)
    if (dot < 0) {
      velocityVec.set(spineDir)
      velocityVec.scale(dot)
      changeVec.sub(velocityVec)
    }
  }

  private def applyStaticFriction(particles: Array[Particle], i: Int, params: LocomotionParameters): Unit = {
    changeVec.set(particles(i).force)
    changeVec.normalize()
    val frictionalForce = -particles(i).mass * params.staticFriction
    changeVec.scale(frictionalForce)
  }

  private def zeroCenterForceAndVelocity(particles: Array[Particle], i: Int): Unit = {
    particles(i).force.set(0.0, 0.0)
    particles(i).velocity.set(0.0, 0.0)
    changeVec.set(0.0, 0.0)
  }

  /**
    * Update accelerations of particles.
    * recall that F=ma so we can get the acceleration
    * by dividing F by m
    */
  def updateAccelerations(segment: Segment): Unit = {
    val particles: Array[Particle] = segment.particles
    for (i <- 0 until NUM_PARTICLES) {
      if (segment.particleParticipatesInBodyMotion(i)) {
        velocityVec.set(particles(i).force)
        velocityVec.add(particles(i).frictionalForce)
        velocityVec.scale(1.0 / particles(i).mass)
        particles(i).acceleration.set(velocityVec)
      }
    }
  }

  /**
    * Update velocities of particles by integrating the acceleration
    * recall that this is just  vel0 + acceleration * dt
    *
    * We must also update velocities based taking into account the friction
    * on the bottom of the snake. Only the center particle of the snake segment
    * is in contact with the ground.
    *
    * @return unstable if the velocities are getting to big. This is an indication that we should reduce the timestep.
    */
  def updateVelocities(segment: Segment, timeStep: Double): Boolean = {
    val particles: Array[Particle] = segment.particles
    var unstable = false
    for (i <- 0 until NUM_PARTICLES) {
      if (segment.particleParticipatesInBodyMotion(i)) {
        velocityVec.set(particles(i).velocity)
        changeVec.set(particles(i).acceleration)
        changeVec.scale(timeStep)
        if (changeVec.length > 100.0) {
          unstable = true
        }
        velocityVec.add(changeVec)
        particles(i).velocity.set(velocityVec)
      }
    }
    unstable
  }

  /**
    * Move particles according to vector field by integrating the velocity.
    * Recall that this is just pos0 + velocity * dt
    * and pos = pos0 + velocity * dt + 1/2 acceleration * dt*dt
    * where dt = timeStep
    */
  def updatePositions(segment: Segment, timeStep: Double): Unit = {
    val particles: Array[Particle] = segment.particles
    for (i <- 0 until NUM_PARTICLES) {
      if (segment.particleParticipatesInBodyMotion(i)) {
        velocityVec.set(particles(i).x, particles(i).y)
        changeVec.set(particles(i).velocity)
        changeVec.scale(timeStep)
        velocityVec.add(changeVec)
        particles(i).set(velocityVec.x, velocityVec.y)
      }
    }
  }
}
