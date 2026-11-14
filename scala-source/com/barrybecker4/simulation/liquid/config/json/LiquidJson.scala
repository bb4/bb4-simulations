// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config.json

/**
  * Gson DTO for the liquid section. Arrays may be null when omitted.
  */
case class LiquidJson(
  sources: Array[SourceJson],
  sinks: Array[RegionJson],
  regions: Array[RegionJson]
)
