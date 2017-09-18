// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.geometry

import com.barrybecker4.simulation.snake1.LocomotionParameters
import com.barrybecker4.simulation.snake1.Snake
import javax.vecmath.Vector2d


/**
  * A segment of a snakes body. It is composed of edges and particles
  * The structure of the segment looks like this:
  * <pre>
  * p3&lt;-------e2&lt;-------p2    left edge
  * ^  \              / ^
  * |    e7        e6   |
  * |      \      /     |     all edges in the middle point to p4
  * e3       \  /       e1
  * ^        /p4\       ^
  * |     /       \     |
  * |   e4          e5  |
  * | /               \ |
  * p0-------&gt;e0-------&gt;p1     right edge
  * </pre>
  *
  * @author Barry Becker
  */
object Segment {
  /** Edge angles are not allowed to become less than this - to prevent instability. */
  private val MIN_EDGE_ANGLE = 0.3
  /** number of particles per segment (2 of which are shared between segments)  */
  private[geometry] val NUM_PARTICLES = 5
  /** index of the center particle */
  private[geometry] val CENTER_INDEX = 4
  private val EPS = 0.00001
  private val MASS_SCALE = 1.0
}

/**
  * Constructor for all segments but the nose
  * @param width1         the width of the segment that is nearest the nose
  * @param width2         the width of the segment nearest the tail
  * @param segmentInFront point to segment in the front
  */
class Segment(width1: Double, width2: Double, var length: Double, var segmentInFront: Segment, segmentIndex: Int, snake: Snake) {

  protected var halfLength = 0
  //private var segmentIndex = segmentIdx
  // keep pointers to the segments in front and in back
  //protected var segmentInFront: Segment = _
  protected var segmentInBack: Segment = _
  protected var edges: Array[Edge] = _
  protected var particles: Array[Particle] = _
  protected var particleMass: Double = 0
  /** The unit directional spinal vector */
  protected var direction = new Vector2d(0, 0)
  /** temporary vector to aid in calculations (saves creating a lot of new vector objects)  */
  private val velocityVec = new Vector2d(0, 0)
  private val changeVec = new Vector2d(0, 0)

  def getEdges: Array[Edge] = edges
  def getParticles: Array[Particle] = particles

  /** Initialize the segment. */
  protected def commonInit(width1: Double, width2: Double, xpos: Double, ypos: Double, segmentIdx: Int, snake: Snake): Unit = {

    particles = new Array[Particle](5)
    edges = new Array[Edge](8)
    val segmentMass: Double = (width1 + width2) * halfLength
    particleMass = Segment.MASS_SCALE * segmentMass / 3
    val scale = 1.0 //snake.getRenderingParams().getScale();
    particles(0) = new Particle(xpos - halfLength, ypos + scale * width2 / 2.0, particleMass)
    particles(3) = new Particle(xpos - halfLength, ypos - scale * width2 / 2.0, particleMass)
    particles(Segment.CENTER_INDEX) = new Particle(xpos, ypos, particleMass)
  }

  protected def initCommonEdges(): Unit = {
    edges(0) = new Edge(particles(0), particles(1)) // bottom (left of snake)
    edges(2) = new Edge(particles(2), particles(3)) // top (right of snake)
    edges(3) = new Edge(particles(0), particles(3)) // back

    // inner diagonal edges
    edges(4) = new Edge(particles(0), particles(Segment.CENTER_INDEX))
    edges(5) = new Edge(particles(1), particles(Segment.CENTER_INDEX))
    edges(6) = new Edge(particles(2), particles(Segment.CENTER_INDEX))
    edges(7) = new Edge(particles(3), particles(Segment.CENTER_INDEX))
  }

  def isHead: Boolean = segmentInFront == null
  def isTail: Boolean = segmentInBack == null
  private def getBackEdge = edges(3)
  private def getBackRightParticle = particles(0)
  private def getBackLeftParticle = particles(3)
  private def getRightEdge = edges(0)
  private def getLeftEdge = edges(2)
  def getCenterParticle: Particle = particles(Segment.CENTER_INDEX)
  private def getHalfLength = halfLength
  protected def getRightForce: Vector2d = edges(0).getForce
  protected def getLeftForce: Vector2d = edges(2).getForce
  protected def getRightBackDiagForce: Vector2d = edges(4).getForce
  protected def getLeftBackDiagForce: Vector2d = edges(7).getForce

  def getSpinalDirection: Vector2d = {
    if (isTail)
      direction.set(segmentInFront.getCenterParticle.x - particles(Segment.CENTER_INDEX).x,
                    segmentInFront.getCenterParticle.y - particles(Segment.CENTER_INDEX).y)
    else if (isHead)
      direction.set(particles(Segment.CENTER_INDEX).x - segmentInBack.getCenterParticle.x,
                    particles(Segment.CENTER_INDEX).y - segmentInBack.getCenterParticle.y)
    else direction.set(segmentInFront.getCenterParticle.x - segmentInBack.getCenterParticle.x,
                       segmentInFront.getCenterParticle.y - segmentInBack.getCenterParticle.y)
    direction.normalize()
    direction
  }

  /**
    * Contract the muscles on the left and right of the segment.
    * Don't contract the nose because there are no muscles there
    */
  def contractMuscles(params: LocomotionParameters, time: Double): Unit = {
    val waveSpeed = params.getWaveSpeed
    val amplitude = params.getWaveAmplitude
    val period = params.getWavePeriod
    //Vector2d muscleForce = v;
    val theta = segmentIndex.toDouble / period - waveSpeed * time
    var offset: Double = 0
    val dir = params.getDirection
    offset = amplitude * (params.getWaveType.calculateOffset(theta) - dir)
    val contractionLeft = 1.0 + offset
    val contractionRight = 1.0 - offset
    if (contractionRight < 0) {
      throw new IllegalArgumentException("Error contractionRight is less than 0 = " + contractionRight)
      //contractionRight = 0.0;
    }
    edges(0).setContraction(contractionLeft)
    edges(2).setContraction(contractionRight)
  }

  def translate(vec: Vector2d): Unit = {
    for (i <- 0 until Segment.NUM_PARTICLES) {
      if ((i != 3 && i != 0) || isTail) {
        velocityVec.set(particles(i).x, particles(i).y)
        velocityVec.add(vec)
        particles(i).set(velocityVec.x, velocityVec.y)
      }
    }
  }

  /** @return true if either of the edge segments bends to much when compared to its nbr in the next segment*/
  def isStable: Boolean = {
    val dot1 = edges(0).dot(segmentInFront.getRightEdge)
    val dot2 = edges(2).dot(segmentInFront.getLeftEdge)
    if (dot1 < Segment.MIN_EDGE_ANGLE || dot2 < Segment.MIN_EDGE_ANGLE) {
      System.out.println("dot1=" + dot1 + " dot2=" + dot2)
      return false
    }
    true
  }

  override def toString: String = {
    val str = new StringBuilder("Segment particles:\n")
    for (i <- 0 until 5) {
      str.append(" p").append(i).append('=').append(particles(i)).append(" \n")
    }
    str.toString
  }
}