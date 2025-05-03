// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model.environment

import com.barrybecker4.simulation.liquid.model.environment.WaterEnvironment.{DEFAULT_MAX_J, DEFAULT_MIN_J}
import com.barrybecker4.simulation.liquid.model.util.{Mat2, Vec2}
import com.barrybecker4.simulation.liquid.model.{Particle, UiParameter}

import scala.util.Random
import WaterEnvironment.*


object WaterEnvironment {
  private val INITIAL_WIDTH = 900
  private val INITIAL_HEIGHT = 1000
  private val DEFAULT_DENSITY0 = 1000.0
  private val DEFAULT_BULK_MODULUS = 200.0
  private val DEFAULT_DYNAMIC_VISCOSITY = 0.1
  private val DEFAULT_GAMMA = 7.0
  private val DEFAULT_MIN_J = 0.95
  private val DEFAULT_MAX_J = 1.05
  private val minNewJ = 0.9 // Minimum new volume change factor
  private val maxNewJ = 1.1 // Maximum new volume change factor
}

class WaterEnvironment(configFile: String) extends MpmEnvironment {
  import Mat2.*

  // Material constants for water
  var density0: Double = DEFAULT_DENSITY0         // water density
  var bulk_modulus: Double = DEFAULT_BULK_MODULUS // bulk modulus of water (Pa). Resistance to compression
  var dynamic_viscosity: Double = DEFAULT_DYNAMIC_VISCOSITY  // water viscosity
  var gamma: Double = DEFAULT_GAMMA                         // For nonlinear pressure response
  var maxJ: Double = DEFAULT_MAX_J                // Maximum volume change factor
  var minJ: Double = DEFAULT_MIN_J                // Minimum volume change factor
  
  def getHeight: Int = INITIAL_HEIGHT
  def getWidth: Int = INITIAL_WIDTH

  override def getUiParameters(): List[UiParameter] = {
    super.getUiParameters() ++ List(
      UiParameter("density0", 500.0, 2000.0, DEFAULT_DENSITY0, 100, "Density"),
      UiParameter("bulk_modulus", 50.0, 4000.0, DEFAULT_BULK_MODULUS, 100, "Bulk Modulus"),
      UiParameter("dynamic_viscosity", 0.01, 1.0, DEFAULT_DYNAMIC_VISCOSITY, 100, "Viscosity"),
      UiParameter("gamma", 1.0, 10.0, DEFAULT_GAMMA, 100, "Gamma"),
      UiParameter("maxJ", 1.0, 1.9, DEFAULT_MAX_J, 100, "Max Volume Change"),
      UiParameter("minJ", 0.2, 1.0, DEFAULT_MIN_J, 100,"Min Volume Change"),
    )
  }

  override def initialize(): Unit = {
    addObject((0.40, 0.75), 0.11, 0x51B8FF)
    addObject((0.55, 0.45), 0.09, 0xA681FE)
  }

  override def getMaterialProperties(particle: Particle): MaterialProps = {
    var J = determinant(particle.F)

    if (J.isNaN) {
      throw new Error(s"J should never be NaN. J=$J particle.F=${particle.F}")
    }

    if (J < minJ || J > maxJ) {
      println(s"Problem with the determinant J=$J particle.F=${particle.F}. Constraining it")
      J = math.max(minJ, math.min(J, maxJ))
    }

    // Pressure directly proportional to volume change
    val pressure = bulk_modulus * (26.0 - math.pow(J, -gamma))

    if (pressure.isNaN) {
      throw new Error(s"pressure=$pressure J=$J particle.F=${particle.F} pow=${math.pow(J, -gamma)}")
    }

    // For fluids, lambda should provide the pressure response
    // divide by J to get Kirchhoff stress. negative because pressure resists compression
    val lambda = pressure / J

    if (!lambda.isFinite) {
      throw new Error(s"lambda: $lambda pressure: $pressure J=$J")
    }

    val mu = dynamic_viscosity
    MaterialProps(lambda, mu)
  }

  override def updateDeformationGradient(particle: Particle, F: Mat2.Mat2): Unit = {
    // For water, mainly track volume changes
    var J = determinant(F)

    if (J.isNaN) {
      throw new Error(s"J should never be NaN. J=$J F=$F particle.F=${particle.F}")
    }

    // Track particle stability based on volume changes
    if (J < minJ || J > maxJ) {
      println(s"J out of bounds: $J")
      particle.stability = if (J > maxJ) J - maxJ else minJ - J
      J = math.max(minJ, math.min(J, maxJ))
    } else {
      particle.stability = 0.0
    }

    val newJ = math.max(minNewJ, math.min(J, maxNewJ))

    if (newJ.isNaN || J.isNaN || J == 0) {
      throw new Error(s"newJ=$newJ J=$J particle.F=${particle.F}")
    }

    val scale = math.sqrt(newJ / J)

    if (scale.isNaN || scale < 0) {
      throw new Error(s"scale=$scale newJ=$newJ J=$J")
    }

    // Scale the deformation gradient to maintain acceptable volume changes
    particle.F = (
      F._1 * scale,
      F._2 * scale,
      F._3 * scale,
      F._4 * scale
    )

    particle.Jp = newJ
  }

  override def addObject(center: Vec2.Vec2, color: Int): Unit = {
    addObject(center, 0.1, color)
  }

  def addObject(center: Vec2.Vec2, radius: Double, color: Int): Unit = {
    val numParticles = 1000
    val random = new Random()

    for (_ <- 0 until numParticles) {
      val theta = random.nextDouble() * 2.0 * math.Pi
      val r = math.sqrt(random.nextDouble()) * radius
      val offset = (r * math.cos(theta), r * math.sin(theta))
      val position = Vec2.add(center, offset)

      // Skip particles outside bounds
      if (position._1 <= 0 || position._1 >= 1 || position._2 <= 0 || position._2 >= 0.9) {
        // continue
      } else {
        particles = Particle(position, color) :: particles
      }
    }
  }
}
