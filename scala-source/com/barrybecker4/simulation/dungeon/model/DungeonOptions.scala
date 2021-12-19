// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.simulation.dungeon.model.DungeonOptions.*
import java.awt.Dimension


object DungeonOptions {
  val DEFAULT_DIMENSIONS = new Dimension(100, 100)
  val DEFAULT_MAX_ROOM_WIDTH = 20
  val DEFAULT_MAX_ROOM_HEIGHT = 15
  val DEFAULT_PERCENT_FILLED = 75
  val DEFAULT_BORDER_WIDTH = 1
}

case class DungeonOptions(
  dimension: Dimension = DEFAULT_DIMENSIONS,
  maxRoomWidth: Int = DEFAULT_MAX_ROOM_WIDTH, 
  maxRoomHeight: Int = DEFAULT_MAX_ROOM_HEIGHT, 
  percentFilled: Int = DEFAULT_PERCENT_FILLED,
  wallBorderWidth: Int = DEFAULT_BORDER_WIDTH
) {
  def setDimension(d: Dimension) = DungeonOptions(d, maxRoomWidth, maxRoomHeight, percentFilled, wallBorderWidth)
  def setMaxRoomWidth(w: Int) = DungeonOptions(dimension, w, maxRoomHeight, percentFilled, wallBorderWidth)
  def setMaxRoomHeight(h: Int) = DungeonOptions(dimension, maxRoomWidth, h, percentFilled, wallBorderWidth)
  def setPercentFilled(percent: Int) = DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percent, wallBorderWidth)
  def setWallBorderWidth(b: Int) = DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, b)
}

