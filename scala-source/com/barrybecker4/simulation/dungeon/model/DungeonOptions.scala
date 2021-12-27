// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.simulation.dungeon.model.DungeonOptions.*

import java.awt.{Color, Dimension}


object DungeonOptions {
  val DEFAULT_DIMENSIONS = new Dimension(50, 50)
  val DEFAULT_MAX_ROOM_WIDTH = 20
  val DEFAULT_MAX_ROOM_HEIGHT = 20
  val MIN_ROOM_DIM = 3
  val DEFAULT_PERCENT_FILLED = 100
  val DEFAULT_ROOM_PADDING = 1
  val DEFAULT_CELL_SIZE = 10
  val DEFAULT_CONNECTIVITY = 0.1f
  val DEFAULT_HALF_PADDED = false
  val DEFAULT_SHOW_GRID = false
  val DECORATION: Decoration = Decoration(new Color(100, 0, 230), new Color(230, 190, 255))
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
  cellSize: Int = DEFAULT_CELL_SIZE,
  connectivity: Float = DEFAULT_CONNECTIVITY,
  halfPadded: Boolean = DEFAULT_HALF_PADDED,
  showGrid: Boolean = DEFAULT_SHOW_GRID
) {
  private val doublePadding = roomPadding * 2

  def setDimension(d: Dimension): DungeonOptions =
    DungeonOptions(d, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, cellSize, connectivity, halfPadded, showGrid)
  def setMaxRoomWidth(w: Int): DungeonOptions =
    DungeonOptions(dimension, w, maxRoomHeight, percentFilled, roomPadding, cellSize, connectivity, halfPadded, showGrid)
  def setMaxRoomHeight(h: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, h, percentFilled, roomPadding, cellSize, connectivity, halfPadded, showGrid)
  def setPercentFilled(percent: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percent, roomPadding, cellSize, connectivity, halfPadded, showGrid)
  def setRoomPadding(b: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, b, cellSize, connectivity, halfPadded, showGrid)
  def setCellSize(s: Int): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, s, connectivity, halfPadded, showGrid)
  def setConnectivity(c: Float): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, cellSize, c, halfPadded, showGrid)
  def setHalfPadded(h: Boolean): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, cellSize, connectivity, h, showGrid)
  def setShowGrid(g: Boolean): DungeonOptions =
    DungeonOptions(dimension, maxRoomWidth, maxRoomHeight, percentFilled, roomPadding, cellSize, connectivity, halfPadded, g)

  def getMaxPaddedWidth: Int = maxRoomWidth + doublePadding
  def getMaxPaddedHeight: Int = maxRoomHeight + doublePadding
  def getMinPaddedDim: Int = minRoomDim + doublePadding

  def minRoomDim: Int = MIN_ROOM_DIM

  def getScreenDimension: Dimension =
    new Dimension(dimension.width * cellSize + 1, dimension.height * cellSize + 1)

  override def toString: String =
    s"DungeonOptions[maxRoom=[$maxRoomWidth, $maxRoomHeight] padding=$roomPadding minPaddedDim=$getMinPaddedDim]"
}
