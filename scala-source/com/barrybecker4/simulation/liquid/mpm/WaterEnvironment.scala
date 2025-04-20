// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm

import scala.util.Random
import com.barrybecker4.simulation.liquid.mpm.util.Vec2
import com.barrybecker4.simulation.liquid.mpm.util.Mat2


class WaterEnvironment extends MpmSimulation {
  import Mat2._

  // Material constants for water
  var density0: Double = 1000.0        // water density
  var bulk_modulus: Double = 200.0     // bulk modulus of water (Pa). Resistance to compression
  var dynamic_viscosity: Double = 0.1  // water viscosity
  var gamma: Double = 7.0              // For nonlinear pressure response
  var maxJ: Double = 1.1               // Maximum volume change factor
  var minJ: Double = 0.9               // Minimum volume change factor
  var maxNewJ: Double = 1.05           // Maximum new volume change factor
  var minNewJ: Double = 0.95           // Minimum new volume change factor

  override def getUiParameters(): List[UiParameter] = {
    super.getUiParameters() ++ List(
      UiParameter("density0", 500.0, 2000.0, 10.0, "Density"),
      UiParameter("bulk_modulus", 50.0, 4000.0, 10.0, "Bulk Modulus"),
      UiParameter("dynamic_viscosity", 0.01, 1.0, 0.01, "Viscosity"),
      UiParameter("gamma", 1.0, 10.0, 0.1, "Gamma"),
      UiParameter("maxJ", 1.0, 1.9, 0.05, "Max Volume Change"),
      UiParameter("minJ", 0.2, 1.0, 0.05, "Min Volume Change")
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
