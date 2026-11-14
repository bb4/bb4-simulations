// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid

package model:

  /** Flat field index for Stam-style (N+2)×(N+2) padded grids, column-major in i. */
  inline def ix(i: Int, j: Int, rowStride: Int): Int = i + j * rowStride

  /** Cell boundaries are one of 3 types */
  enum Boundary:
    /** negates on neither, the vertical, or horizontal boundary */
    case NEITHER, HORIZONTAL, VERTICAL

  /** The fields of a cell */
  enum CellProperty:
    case U, V, DENSITY
