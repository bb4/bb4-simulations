// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config.json

/** Gson DTO for a wall segment. [[start]] / [[stop]] are `[x, y]`. */
case class WallJson(
  start: Array[Int],
  stop: Array[Int]
)
