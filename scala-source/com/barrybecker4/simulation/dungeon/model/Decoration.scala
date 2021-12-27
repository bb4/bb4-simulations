// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import java.awt.{BasicStroke, Stroke, Color}


case class Decoration(
  wallColor: Color,
  floorColor: Color,
  wallStroke: BasicStroke = new BasicStroke(1.0))
