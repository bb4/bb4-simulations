// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config.json

/**
  * Gson DTO for a liquid environment config file.
  * Optional [[walls]] / [[liquid]] may be null when omitted from JSON.
  */
case class EnvironmentJson(
  gridWidth: Int,
  gridHeight: Int,
  cellSize: Double,
  gravity: Double,
  walls: Array[WallJson],
  liquid: LiquidJson
)
