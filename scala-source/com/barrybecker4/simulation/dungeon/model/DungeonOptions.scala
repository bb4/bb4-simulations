// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.simulation.dungeon.model.DungeonOptions.*
import java.awt.Dimension


object DungeonOptions {
  val DEFAULT_DIMENSIONS = new Dimension(50, 50)
  val DEFAULT_MAX_ROOM_WIDTH = 20
  val DEFAULT_MAX_ROOM_HEIGHT = 15
  val MIN_ROOM_DIM = 3
  val DEFAULT_PERCENT_FILLED = 100
  val DEFAULT_ROOM_PADDING = 1
  val DEFAULT_CELL_SIZE = 10
}

/**
 * Dungeon attributes. All units are in cell grid units.
 * To convert from cell units to pixels, multiply by cellSize
 */
case class DungeonOptions(
  dimension: Dimension = DEFAULT_DIMENSIONS,
  maxRoomWidth: Int = DEFAULT_MAX_ROOM_WIDTH,
  maxRoomHeight: Int = DEFAULT_MAX_ROOM_HEIGHT,
  percentFilled: Int = DEFAULT_PERCENT_FILLED,
  roomPadding: Int = DEFAULT_ROOM_PADDING,
  cellSize: Int = DEFAULT_CELL_SIZE
) {
  def setDimension(d: Dimension): DungeonOptions =
    DungeonOptions(d, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, cellSize)
  def setMaxRoomWidth(w: Int): DungeonOptions =
    DungeonOptions(dimension, w, maxRoomHeight, percentFilled, roomPadding, cellSize)
  def setMaxRoomHeight(h: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, h, percentFilled, roomPadding, cellSize)
  def setPercentFilled(percent: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percent, roomPadding, cellSize)
  def setRoomPadding(b: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, b, cellSize)
  def setCellSize(s: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, s)

  def minRoomDim: Int = MIN_ROOM_DIM

  def getScreenDimension: Dimension =
    new Dimension(dimension.width * cellSize, dimension.height * cellSize)
}
