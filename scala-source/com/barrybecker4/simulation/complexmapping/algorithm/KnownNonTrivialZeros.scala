/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.simulation.complexmapping.algorithm.model.Box


/**
  * First known non-trivial zeros of the Riemann zeta function on the critical line Re(s) = 1/2.
  * Imaginary parts from Odlyzko / standard tables.
  */
object KnownNonTrivialZeros {

  /** A zero ρ_n = 1/2 + i·t on the critical line. */
  case class Zero(index: Int, imag: Double) {
    def real: Double = 0.5
    /** Short label for graph overlays, e.g. "ρ1 14.13". */
    def label: String = f"ρ$index ${imag}%.2f"
  }

  /** First 15 positive imaginary parts. */
  val ImagParts: IndexedSeq[Double] = IndexedSeq(
    14.134725141734693,
    21.022039638771554,
    25.010857580145688,
    30.424876125859513,
    32.935061587739189,
    37.586178158825671,
    40.918719012147495,
    43.327073280914999,
    48.005150881167159,
    49.773832477672302,
    52.970321477714460,
    56.446247697063394,
    59.347044002602353,
    60.831778524609809,
    65.112544048081606
  )

  val All: IndexedSeq[Zero] =
    ImagParts.zipWithIndex.map { case (t, i) => Zero(i + 1, t) }

  /** Zeros whose critical-line point lies inside the axis-aligned viewport. */
  def inViewport(viewport: Box): Seq[Zero] =
    All.filter { z =>
      z.real >= viewport.leftX && z.real <= viewport.rightX &&
        z.imag >= viewport.bottomY && z.imag <= viewport.topY
    }
}
