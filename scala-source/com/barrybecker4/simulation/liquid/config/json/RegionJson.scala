// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config.json

/**
  * Gson DTO for a rectangular region (initial liquid or sink).
  * [[start]] / [[stop]] are `[x, y]`; [[stop]] may be null.
  */
case class RegionJson(
  start: Array[Int],
  stop: Array[Int]
)
