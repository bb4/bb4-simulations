// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model.options

import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions.*
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions.DEFAULT_RANDOM_SKEW

import java.awt.{Color, Dimension}


object RoomOptions {
  val DEFAULT_MAX_ROOM_WIDTH = 20
  val DEFAULT_MAX_ROOM_HEIGHT = 20
  // SKEW of 0.6 is roughly a uniform distribution
  val DEFAULT_RANDOM_SKEW = 0.6
  val DEFAULT_RANDOM_BIAS = 0.0
  val MIN_ROOM_DIM = 3
  val DEFAULT_PERCENT_FILLED = 100
  val DEFAULT_ROOM_PADDING = 1
}

case class RoomOptions(
      maxRoomWidth: Int = DEFAULT_MAX_ROOM_WIDTH,
      maxRoomHeight: Int = DEFAULT_MAX_ROOM_HEIGHT,
      randomSkew: Double = DEFAULT_RANDOM_SKEW,
      randomBias: Double = DEFAULT_RANDOM_BIAS,
      percentFilled: Int = DEFAULT_PERCENT_FILLED,
      roomPadding: Int = DEFAULT_ROOM_PADDING) {

  private val doublePadding = roomPadding * 2

  def setMaxRoomWidth(w: Int): RoomOptions = RoomOptions(w, maxRoomHeight, randomSkew, randomBias, percentFilled, roomPadding)
  def setMaxRoomHeight(h: Int): RoomOptions = RoomOptions(maxRoomWidth, h, randomSkew, randomBias, percentFilled, roomPadding)
  def setRandomSkew(rndSkew: Double): RoomOptions = RoomOptions(maxRoomWidth, maxRoomHeight, rndSkew, randomBias, percentFilled, roomPadding)
  def setRandomBias(rndBias: Double): RoomOptions = RoomOptions(maxRoomWidth, maxRoomHeight, randomSkew, rndBias, percentFilled, roomPadding)
  def setPercentFilled(percent: Int): RoomOptions = RoomOptions(maxRoomWidth, maxRoomHeight, randomSkew, randomBias, percent, roomPadding)
  def setRoomPadding(b: Int): RoomOptions = RoomOptions(maxRoomWidth, maxRoomHeight, randomSkew, randomBias, percentFilled, b)

  def getMaxPaddedWidth: Int = maxRoomWidth + doublePadding
  def getMaxPaddedHeight: Int = maxRoomHeight + doublePadding
  def getMinPaddedDim: Int = minRoomDim + doublePadding

  def minRoomDim: Int = MIN_ROOM_DIM

  override def toString: String =
    s"RoomOptions[maxRoom=[$maxRoomWidth, $maxRoomHeight] padding=$roomPadding minPaddedDim=$getMinPaddedDim]"
}
