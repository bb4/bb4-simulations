// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.model.RoomOptions.{DEFAULT_MAX_ROOM_HEIGHT, DEFAULT_MAX_ROOM_WIDTH, DEFAULT_PERCENT_FILLED, DEFAULT_ROOM_PADDING, MIN_ROOM_DIM}

import java.awt.{Color, Dimension}


object RoomOptions {
  val DEFAULT_MAX_ROOM_WIDTH = 20
  val DEFAULT_MAX_ROOM_HEIGHT = 20
  val MIN_ROOM_DIM = 3
  val DEFAULT_PERCENT_FILLED = 100
  val DEFAULT_ROOM_PADDING = 1
}

case class RoomOptions(
  maxRoomWidth: Int = DEFAULT_MAX_ROOM_WIDTH,
  maxRoomHeight: Int = DEFAULT_MAX_ROOM_HEIGHT,
  percentFilled: Int = DEFAULT_PERCENT_FILLED,
  roomPadding: Int = DEFAULT_ROOM_PADDING) {

  private val doublePadding = roomPadding * 2

  def setMaxRoomWidth(w: Int): RoomOptions = RoomOptions(w, maxRoomHeight, percentFilled, roomPadding)
  def setMaxRoomHeight(h: Int): RoomOptions = RoomOptions(maxRoomWidth, h, percentFilled, roomPadding)
  def setPercentFilled(percent: Int): RoomOptions = RoomOptions( maxRoomWidth, maxRoomHeight, percent, roomPadding)
  def setRoomPadding(b: Int): RoomOptions = RoomOptions(maxRoomWidth, maxRoomHeight, percentFilled, b)

  def getMaxPaddedWidth: Int = maxRoomWidth + doublePadding
  def getMaxPaddedHeight: Int = maxRoomHeight + doublePadding
  def getMinPaddedDim: Int = minRoomDim + doublePadding

  def minRoomDim: Int = MIN_ROOM_DIM

  override def toString: String =
    s"RoomOptions[maxRoom=[$maxRoomWidth, $maxRoomHeight] padding=$roomPadding minPaddedDim=$getMinPaddedDim]"
}
