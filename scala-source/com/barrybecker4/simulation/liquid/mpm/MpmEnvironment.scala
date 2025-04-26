// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm

import com.barrybecker4.simulation.liquid.mpm.util.{Decomp, Mat2, Utils, Vec2, Vec3}


abstract class MpmEnvironment extends Environment {
  import Vec2._
  import Vec3._
  import Mat2._

  var isPaused: Boolean = false
  val params: MpmParameters = new MpmParameters()
  var particles: List[Particle] = List.empty
  var grid: Array[Vec3.Vec3] = Array.empty
  var iter: Int = 0
  var faucetRunning: Boolean = false
  var faucetPosition: Vec2.Vec2 = (0.0, 0.0)
  var faucetVelocity: Vec2.Vec2 = (0.0, 0.0)
  var faucetSize: Double = 0.0
  
  def getParams: MpmParameters = params
  def getParticles: List[Particle] = particles
  def getIter: Int = iter

  def getFaucetRunning: Boolean = faucetRunning
  def getFaucetPosition: Vec2.Vec2 = faucetPosition
  def getFaucetVelocity: Vec2.Vec2 = faucetVelocity
  def getFaucetSize: Double = faucetSize
  

  def getUiParameters(): List[UiParameter] = List(
    UiParameter("particle_mass", 0.5, 2.0, 0.1, "Particle Mass"),
    UiParameter("vol", 0.5, 2.0, 0.1, "Volume"),
    UiParameter("gravity", -400.0, 400.0, 20.0, "Gravity"),
    UiParameter("forceScale", 50.0, 300.0, 10.0, "Force Scale"),
    UiParameter("dt", 0.0001, 0.0009, 0.0001, "time step (dt)")
  )

  def initialize(): Unit

  // Progress simulation by one time step
  def advance(): Unit = {
    if (!isPaused) {
      advanceSimulation()
      iter += 1
    }
  }

  // Reset and restart simulation
  def restart(): Unit = {
    particles = List.empty
    grid = Array.empty
    iter = 0
    resetGrid()
    initialize()
  }

  // Main simulation step
  private def advanceSimulation(): Unit = {
    resetGrid()
    particlesToGrid()
    updateGridVelocities(params.gravity)
    gridToParticles()
  }

  def pause(): Unit = {
    isPaused = true
  }

  def resume(): Unit = {
    isPaused = false
  }

  def addObject(center: Vec2.Vec2, color: Int): Unit

  def startFaucet(faucetPos: Vec2.Vec2, velocity: Vec2.Vec2, size: Double): Unit = {
    faucetPosition = faucetPos
    faucetVelocity = velocity
    faucetSize = size
    faucetRunning = true
    println(s"faucetRunning = $faucetRunning faucetPos = $faucetPos velocity = $velocity size = $size")
  }

  def stopFaucet(): Unit = {
    faucetRunning = false
  }

  def incrementFaucetFlow(): Unit = {
    val numParticles = 2
    for (_ <- 0 until numParticles) {
      val position = (
        faucetPosition._1 + 0.2 * math.random * faucetSize,
        faucetPosition._2 + math.random * faucetSize
      )
      println(s"adding faucet particle $position")
      val particle = Particle(position, 0x44ff55)
      particle.velocity = faucetVelocity
      particles = particle :: particles
    }
  }

  def applyForce(center: Vec2.Vec2, forceVector: Vec2.Vec2, radius: Double): Unit = {
    particles.foreach { particle =>
      val dist = distance(center, particle.position)
      if (dist < radius) {
        particle.externalForce = Some(forceVector)
      }
    }
  }

  // Material properties - to be implemented by subclasses
  case class MaterialProps(lambda: Double, mu: Double)
  def getMaterialProperties(particle: Particle): MaterialProps

  // Update deformation gradient - to be implemented by subclasses
  def updateDeformationGradient(particle: Particle, F: Mat2.Mat2): Unit

  // Transfer particle data to grid
  def particlesToGrid(): Unit = {
    particles.foreach(particleToGrid)
  }

  def particleToGrid(particle: Particle): Unit = {
    val baseCoord = calcBaseCoord(particle)
    val invDx = params.inv_dx
    val fx = Vec2.sub(Vec2.scale(particle.position, invDx), (baseCoord._1.toDouble, baseCoord._2.toDouble))
    val w = Utils.createKernel(fx)

    val materialProps = getMaterialProperties(particle)
    val mu = materialProps.mu
    val lambda = materialProps.lambda

    // Stress computation
    val J = determinant(particle.F)
    val (r, _) = Decomp.polar(particle.F)
    val k1 = -4 * invDx * invDx * params.dt * params.vol
    val k2 = lambda * (J - 1) * J

    val temp = map(Mat2.mul(Mat2.sub(Mat2.transpose(particle.F), r), particle.F), _ * 2 * mu)
    val k2Mat = (k2, 0.0, 0.0, k2)
    val stress = map(Mat2.add(temp, k2Mat), _ * k1)

    val particleMass = params.particle_mass
    val cauchyScaled = map(particle.Cauchy, _ * particleMass)
    val affine = Mat2.add(stress, cauchyScaled)

    if (affine._1.isNaN || affine._2.isNaN) {
      throw new Error(s"Invalid affine: $affine stress=$stress p.velocity=${particle.velocity} " +
        s"p.Cauchy=${particle.Cauchy} p.F=${particle.F} p.position=${particle.position} k1=$k1 k2=$k2 lambda=$lambda mu=$mu")
    }

    transferToGrid(particle, affine, particleMass, baseCoord, fx, w)
  }

  def gridToParticles(): Unit = {
    particles.foreach(gridToParticle)
    if (faucetRunning) {
      incrementFaucetFlow()
    }
  }

  def gridToParticle(particle: Particle): Unit = {
    val baseCoord = calcBaseCoord(particle)
    val fx = Vec2.sub(Vec2.scale(particle.position, params.inv_dx), (baseCoord._1.toDouble, baseCoord._2.toDouble))
    val w = Utils.createKernel(fx)

    particle.Cauchy = (0.0, 0.0, 0.0, 0.0)
    particle.velocity = (0.0, 0.0)

    // Gather from grid
    for (i <- 0 until 3; j <- 0 until 3) {
      val dpos = Vec2.sub((i.toDouble, j.toDouble), fx)
      val ii = gridIndex(baseCoord._1 + i, baseCoord._2 + j)
      val weight = w(i)(0) * w(j)(1)

      val gridCell = grid(ii)
      particle.velocity = Vec2.add(particle.velocity, Vec2.scale((gridCell._1, gridCell._2), weight))

      if (particle.velocity._1.isNaN || particle.velocity._2.isNaN) {
        throw new Error(s"Invalid velocity: ${particle.velocity._1} ${particle.velocity._2} weight=$weight ii:$ii grid[ii]=${grid(ii)}")
      }

      val weightedGrid = Vec2.scale((gridCell._1, gridCell._2), weight)
      val outer = Mat2.outer(weightedGrid, dpos)
      val scaledOuter = map(outer, _ * 4 * params.inv_dx)
      particle.Cauchy = Mat2.add(particle.Cauchy, scaledOuter)

      if (particle.Cauchy._1.isNaN || particle.Cauchy._2.isNaN) {
        throw new Error(s"Invalid p.C: ${particle.Cauchy} dpos=$dpos weight=$weight ii:$ii grid[ii]=${grid(ii)}")
      }
    }

    // Apply external force, if any
    particle.externalForce.foreach { force =>
      particle.velocity = Vec2.add(particle.velocity, Vec2.scale(force, params.forceScale))
      particle.externalForce = None
    }

    // Advection
    particle.position = Vec2.add(particle.position, Vec2.scale(particle.velocity, params.dt))

    // F update
    val identity = (1.0, 0.0, 0.0, 1.0)
    val cauchyDt = map(particle.Cauchy, _ * params.dt)
    val F = Mat2.mul(particle.F, Mat2.add(identity, cauchyDt))

    if (F._1.isNaN || F._2.isNaN) {
      throw new Error(s"Invalid F: $F particle.F=${particle.F} particle.Cauchy=${particle.Cauchy} p.velocity=${particle.velocity} particle.position=${particle.position}")
    }

    updateDeformationGradient(particle, F)
  }

  def calcBaseCoord(particle: Particle): (Int, Int) = {
    val x = (particle.position._1 * params.inv_dx - 0.5).toInt
    val y = (particle.position._2 * params.inv_dx - 0.5).toInt

    if (x < 0 /*|| x.isNaN*/) {
      println(s"Invalid base_coord: $x $y particle.pos=${particle.position} inv_dx=${params.inv_dx}")
    }

    (x, y)
  }

  def resetGrid(): Unit = {
    val maxIndex = (params.n + 1) * (params.n + 1)
    grid = Array.fill(maxIndex)((0.0, 0.0, 0.0))
  }

  def gridIndex(i: Int, j: Int): Int = {
    i + (params.n + 1) * j
  }

  def transferToGrid(
    particle: Particle,
    affine: Mat2.Mat2,
    mass: Double,
    baseCoord: (Int, Int),
    fx: Vec2.Vec2,
    w: Array[Array[Double]]
  ): Unit = {
    val mv = (particle.velocity._1 * mass, particle.velocity._2 * mass, mass)

    for (i <- 0 until 3; j <- 0 until 3) {
      val dpos = ((i - fx._1) * params.dx, (j - fx._2) * params.dx)
      val idx = gridIndex(baseCoord._1 + i, baseCoord._2 + j)

      if (idx < 0 || idx >= grid.length) {
        println(s"Invalid grid index: $idx baseCoord._1=${baseCoord._1} i=$i baseCoord._2=${baseCoord._2} j=$j")
      }

      val weight = w(i)(0) * w(j)(1)
      val mulVecResult = mulVec(affine, dpos)
      val changeVec = (mulVecResult._1, mulVecResult._2, 0.0)
      val change = Vec3.scale(Vec3.add(mv, changeVec), weight)

      if (change._1.isNaN || change._2.isNaN) {
        throw new Error(s"Invalid change: $change p.velocity=${particle.velocity} mv=$mv dpos=$dpos affine=$affine weight=$weight idx=$idx")
      }

      grid(idx) = Vec3.add(grid(idx), change)
    }
  }

  // Update grid velocities
  def updateGridVelocities(gravity: Double): Unit = {
    val n = params.n
    for (i <- 0 to n; j <- 0 to n) {
      val idx = gridIndex(i, j)
      updateGridVelocity(idx, i, j, gravity)
    }
  }

  def updateGridVelocity(idx: Int, i: Int, j: Int, gravity: Double): Unit = {
    if (grid(idx)._3 > 0) {
      // Normalize by mass
      val mass = grid(idx)._3
      grid(idx) = (grid(idx)._1 / mass, grid(idx)._2 / mass, grid(idx)._3 / mass)

      // Add gravity
      grid(idx) = Vec3.add(grid(idx), (0.0, gravity * params.dt, 0.0))

      val x = i.toDouble / params.n
      val y = j.toDouble / params.n

      // Apply boundary conditions
      val boundary = params.boundary
      if (x < boundary || x > 1.0 - boundary || y > 1.0 - boundary) {
        grid(idx) = (0.0, 0.0, 0.0)
      }
      if (y < boundary) {
        grid(idx) = (grid(idx)._1, math.max(0.0, grid(idx)._2), grid(idx)._3)
      }
    }
  }
}
