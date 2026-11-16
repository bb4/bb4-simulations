/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm


/**
  * How to color the domain heatmap of a complex function.
  */
enum DomainColorMode(val label: String):
  case Magnitude extends DomainColorMode("log |f| (magnitude)")
  case Argument extends DomainColorMode("arg(f) (phase)")
end DomainColorMode
