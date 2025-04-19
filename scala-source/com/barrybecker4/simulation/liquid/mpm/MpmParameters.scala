// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.liquid.mpm

class MpmParameters {
  var n: Int = 64              // Grid resolution
  var dx: Double = 1.0 / n     // Grid cell size
  var inv_dx: Double = n.toDouble // Inverse of dx
  var dt: Double = 1e-4        // Time step
  var particle_mass: Double = 1.0 // Particle mass
  var vol: Double = 1.0        // Particle volume
  var hardening: Double = 10.0 // Hardening coefficient for snow
  var E: Double = 1e4          // Young's modulus
  var nu: Double = 0.2         // Poisson ratio
  var gravity: Double = -9.8   // Gravity
  var forceScale: Double = 100.0 // Scale for external forces
  var boundary: Double = 0.05  // Boundary condition parameter
}