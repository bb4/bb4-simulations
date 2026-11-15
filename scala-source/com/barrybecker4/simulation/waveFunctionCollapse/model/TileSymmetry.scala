// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

/**
  * Tile symmetry from simple-tiled `data.json` (`symmetry` field on each tile).
  * Encodes cardinality and the rotate/reflect index maps used by the reference WFC.
  */
enum TileSymmetry {
  case L, T, I, Slash, X

  def cardinality: Int = this match {
    case L | T => 4
    case I | Slash => 2
    case X => 1
  }

  /** Rotate index map `a` from the mxgmn simple-tiled action tables. */
  def rotate: Int => Int = this match {
    case L | T => i => (i + 1) % 4
    case I | Slash => i => 1 - i
    case X => identity
  }

  /** Reflect index map `b` from the mxgmn simple-tiled action tables. */
  def reflect: Int => Int = this match {
    case L => i => if (i % 2 == 0) i + 1 else i - 1
    case T => i => if (i % 2 == 0) i else 4 - i
    case I => identity
    case Slash => i => 1 - i
    case X => identity
  }
}

object TileSymmetry {
  def fromJson(sym: String): TileSymmetry = sym match {
    case "L" => L
    case "T" => T
    case "I" => I
    case "\\" => Slash
    case _ => X
  }
}
