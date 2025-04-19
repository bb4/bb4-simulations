// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.liquid.mpm

case class UiParameter(
  name: String,
  minValue: Double,
  maxValue: Double,
  step: Double,
  displayName: String
)