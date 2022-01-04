// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model.options

import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.uniongraph.UnionGraphDungeonGenerator
import com.barrybecker4.simulation.dungeon.model.options.{DungeonOptions, RoomOptions}
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions.*
import com.barrybecker4.simulation.dungeon.model.Decoration

import java.awt.{Color, Dimension}


object DungeonOptions {
  val DEFAULT_DIMENSIONS = new Dimension(50, 50)
  val DEFAULT_CELL_SIZE = 10
  val DEFAULT_CONNECTIVITY = 0.1f
  val DEFAULT_HALF_PADDED = false
  val DEFAULT_SHOW_GRID = false
  val DEFAULT_DUNGEON_GENERATOR: DungeonGeneratorStrategy = UnionGraphDungeonGenerator()

  val DECORATION: Decoration = Decoration(new Color(100, 0, 230), new Color(230, 190, 255))
}

/**
 * Dungeon attributes. All units are in cell grid units.
 * To convert from cell units to pixels, multiply by cellSize
 */
case class DungeonOptions(
  dimension: Dimension = DEFAULT_DIMENSIONS,
  roomOptions: RoomOptions = RoomOptions(),
  cellSize: Int = DEFAULT_CELL_SIZE,
  connectivity: Float = DEFAULT_CONNECTIVITY,
  halfPadded: Boolean = DEFAULT_HALF_PADDED,
  showGrid: Boolean = DEFAULT_SHOW_GRID,
  generator: DungeonGeneratorStrategy = DEFAULT_DUNGEON_GENERATOR
) {

  def setDimension(d: Dimension): DungeonOptions =
    DungeonOptions(d, roomOptions, cellSize, connectivity, halfPadded, showGrid, generator)
  def setMaxRoomWidth(w: Int): DungeonOptions =
    DungeonOptions(dimension, roomOptions.setMaxRoomWidth(w), cellSize, connectivity, halfPadded, showGrid, generator)
  def setMaxRoomHeight(h: Int): DungeonOptions =
    DungeonOptions(dimension, roomOptions.setMaxRoomHeight(h), cellSize, connectivity, halfPadded, showGrid, generator)
  def setRandomSkew(skew: Double): DungeonOptions =
    DungeonOptions(dimension, roomOptions.setRandomSkew(skew), cellSize, connectivity, halfPadded, showGrid, generator)
  def setRandomBias(bias: Double): DungeonOptions =
    DungeonOptions(dimension, roomOptions.setRandomBias(bias), cellSize, connectivity, halfPadded, showGrid, generator)
  def setPercentFilled(percent: Int): DungeonOptions =
    DungeonOptions(dimension, roomOptions.setPercentFilled(percent), cellSize, connectivity, halfPadded, showGrid, generator)
  def setRoomPadding(b: Int): DungeonOptions =
    DungeonOptions(dimension, roomOptions.setRoomPadding(b), cellSize, connectivity, halfPadded, showGrid, generator)
  def setCellSize(s: Int): DungeonOptions =
    DungeonOptions(dimension, roomOptions, s, connectivity, halfPadded, showGrid, generator)
  def setConnectivity(c: Float): DungeonOptions =
    DungeonOptions(dimension, roomOptions, cellSize, c, halfPadded, showGrid, generator)
  def setHalfPadded(h: Boolean): DungeonOptions =
    DungeonOptions(dimension, roomOptions, cellSize, connectivity, h, showGrid, generator)
  def setShowGrid(sg: Boolean): DungeonOptions =
    DungeonOptions(dimension, roomOptions, cellSize, connectivity, halfPadded, sg, generator)
  def setGenerator(gen: DungeonGeneratorStrategy): DungeonOptions =
    DungeonOptions(dimension, roomOptions, cellSize, connectivity, halfPadded, showGrid, gen)

  def getScreenDimension: Dimension =
    new Dimension(dimension.width * cellSize + 1, dimension.height * cellSize + 1)

  override def toString: String =
    s"DungeonOptions[roomOpts=[$roomOptions] " +
    s"connectivity: $connectivity genStrategy: ${generator.getClass.getSimpleName}"
}
