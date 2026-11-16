// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

/**
  * Selectable snake locomotion gaits.
  */
enum GaitType(val displayName: String, val gait: Gait):

  case HORIZONTAL_UNDULATORY extends GaitType("Horizontal Undulatory", HorizontalUndulatoryGait)
  case RECTILINEAR extends GaitType("Rectilinear", RectilinearGait)
  case SIDE_WINDING extends GaitType("Side-winding", SideWindingGait)
  case CONCERTINA extends GaitType("Concertina", ConcertinaGait)

  override def toString: String = displayName

object GaitType:
  val VALUES: Array[GaitType] = GaitType.values
